package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static nz.bradcampbell.paperparcel.PaperParcelProcessor.DATA_VARIABLE_NAME;
import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;
import static nz.bradcampbell.paperparcel.utils.StringUtils.uncapitalizeFirstCharacter;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.primitives.Ints;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.DataClass;
import nz.bradcampbell.paperparcel.model.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.lang.model.element.Modifier;

public class WrapperGenerator {
  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");
  private static final ClassName TYPED_PARCELABLE = ClassName.get(TypedParcelable.class);

  public JavaFile generateParcelableWrapper(DataClass dataClass) throws IOException {
    TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(dataClass.getWrapperClassName().simpleName())
        .addModifiers(PUBLIC, FINAL)
        .addSuperinterface(ParameterizedTypeName.get(TYPED_PARCELABLE, dataClass.getClassName()));

    FieldSpec classLoader = null;
    if (dataClass.getRequiresClassLoader()) {
      classLoader = generateClassLoaderField(dataClass.getClassName());
      wrapperBuilder.addField(classLoader);
    }

    FieldSpec creator = generateCreator(
        dataClass.getClassName(), dataClass.getWrapperClassName(), dataClass.isSingleton(), dataClass.getProperties(),
        classLoader, dataClass.getRequiredTypeAdapters());

    wrapperBuilder.addField(creator)
        .addField(generateContentsField(dataClass.getClassName()))
        .addMethod(generateContentsGetter(dataClass.getClassName()))
        .addMethod(generateContentsConstructor(dataClass.getClassName()))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(dataClass.getProperties(), dataClass.getRequiredTypeAdapters()));

    // Build the java file
    return JavaFile.builder(dataClass.getClassPackage(), wrapperBuilder.build()).build();
  }

  private FieldSpec generateClassLoaderField(TypeName className) {
    if (className instanceof ParameterizedTypeName) {
      className = ((ParameterizedTypeName) className).rawType;
    }
    return FieldSpec.builder(ClassLoader.class, "CLASS_LOADER", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
        .initializer("$T.class.getClassLoader()", className)
        .build();
  }

  private FieldSpec generateCreator(
      TypeName typeName, ClassName wrapperClassName, boolean isSingleton, List<Property> properties,
      FieldSpec classLoader, Set<Adapter> typeAdapters) {

    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, wrapperClassName);

    String inParameterName = "in";
    ParameterSpec in = ParameterSpec.builder(PARCEL, inParameterName).build();

    final CodeBlock.Builder block = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, wrapperClassName))
        .beginControlFlow("@$T public $T createFromParcel($T $N)", Override.class, wrapperClassName, PARCEL, in);

    if (!isSingleton) {
      List<FieldWriteInfo> fields = new ArrayList<>();

      Set<String> scopedVariableNames = new LinkedHashSet<>();
      scopedVariableNames.add(inParameterName);

      Map<ClassName, CodeBlock> typeAdapterMap = getTypeAdaptersMap(typeAdapters, block, scopedVariableNames);

      for (Property p : properties) {
        String name = p.getName();
        CodeBlock value = p.readFromParcel(block, in, classLoader, typeAdapterMap, scopedVariableNames);
        fields.add(new FieldWriteInfo(name, value, p.isVisible(), p.getConstructorPosition(), p.getSetterMethodName()));
      }

      final String fieldName = getUniqueName(PaperParcelProcessor.DATA_VARIABLE_NAME, scopedVariableNames);

      List<FieldWriteInfo> constructorArgs = FluentIterable.from(fields)
          .filter(new Predicate<FieldWriteInfo>() {
            @Override public boolean apply(FieldWriteInfo input) {
              return input.constructorPosition >= 0;
            }
          })
          .toSortedList(new Comparator<FieldWriteInfo>() {
            @Override public int compare(FieldWriteInfo left, FieldWriteInfo right) {
              return Ints.compare(left.constructorPosition, right.constructorPosition);
            }
          });

      // Construct data class
      constructType(constructorArgs, typeName, fieldName, block);

      // Write fields to data class directly
      FluentIterable.from(fields)
          .filter(new Predicate<FieldWriteInfo>() {
            @Override public boolean apply(FieldWriteInfo input) {
              return input.constructorPosition < 0;
            }
          })
          .filter(new Predicate<FieldWriteInfo>() {
            @Override public boolean apply(FieldWriteInfo input) {
              return input.isVisible;
            }
          })
          .forEach(new Consumer<FieldWriteInfo>() {
            @Override public void accept(FieldWriteInfo field) {
              block.addStatement("$N.$N = $L", fieldName, field.name, field.value);
            }
          });

      // Write remaining fields via setters
      FluentIterable.from(fields)
          .filter(new Predicate<FieldWriteInfo>() {
            @Override public boolean apply(FieldWriteInfo input) {
              return input.constructorPosition < 0;
            }
          })
          .filter(new Predicate<FieldWriteInfo>() {
            @Override public boolean apply(FieldWriteInfo input) {
              return !input.isVisible;
            }
          })
          .forEach(new Consumer<FieldWriteInfo>() {
            @Override public void accept(FieldWriteInfo field) {
              block.addStatement("$N.$N($L)", fieldName, field.setterMethodName, field.value);
            }
          });

      block.addStatement("return new $T($N)", wrapperClassName, fieldName);
    } else {
      block.addStatement("return new $T($T.INSTANCE)", wrapperClassName, typeName);
    }

    block.endControlFlow()
        .beginControlFlow("@$T public $T[] newArray($T size)", Override.class, wrapperClassName, int.class)
        .addStatement("return new $T[size]", wrapperClassName)
        .endControlFlow()
        .unindent()
        .add("}");

    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(block.build())
        .build();
  }

  private FieldSpec generateContentsField(TypeName className) {
    return FieldSpec.builder(className, DATA_VARIABLE_NAME, PRIVATE, FINAL).build();
  }

  private MethodSpec generateContentsGetter(TypeName className) {
    return MethodSpec.methodBuilder("get")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(className)
        .addStatement("return this.$N", DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateContentsConstructor(TypeName className) {
    return MethodSpec.constructorBuilder()
        .addModifiers(PUBLIC)
        .addParameter(className, DATA_VARIABLE_NAME)
        .addStatement("this.$N = $N", DATA_VARIABLE_NAME, DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateDescribeContents() {
    return MethodSpec.methodBuilder("describeContents")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("return 0")
        .build();
  }

  private MethodSpec generateWriteToParcel(List<Property> properties, Set<Adapter> typeAdapters) {
    String destParameterName = "dest";
    String flagsParameterName = "flags";

    ParameterSpec dest = ParameterSpec.builder(PARCEL, destParameterName).build();
    ParameterSpec flags = ParameterSpec.builder(int.class, flagsParameterName).build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(flags);

    CodeBlock.Builder block = CodeBlock.builder();

    Set<String> scopedVariableNames = new LinkedHashSet<>();
    scopedVariableNames.add(destParameterName);
    scopedVariableNames.add(flagsParameterName);

    Map<ClassName, CodeBlock> typeAdapterMap = getTypeAdaptersMap(typeAdapters, block, scopedVariableNames);

    for (Property p : properties) {
      String accessorStrategy = p.isVisible() ? p.getName() : p.getGetterMethodName() + "()";

      TypeName wildCardTypeName = p.getTypeName();
      if (wildCardTypeName instanceof WildcardTypeName) {
        wildCardTypeName = ((WildcardTypeName) wildCardTypeName).upperBounds.get(0);
      }

      String propertyName = getUniqueName(p.getName(), scopedVariableNames);

      String dataFieldName = "this." + DATA_VARIABLE_NAME;
      block.addStatement("$T $N = $N.$N", wildCardTypeName, propertyName, dataFieldName, accessorStrategy);

      // Add propertyName to scoped names
      scopedVariableNames.add(propertyName);

      CodeBlock sourceLiteral = CodeBlock.of("$N", propertyName);
      p.writeToParcel(block, dest, flags, sourceLiteral, typeAdapterMap, scopedVariableNames);
    }

    return builder.addCode(block.build()).build();
  }

  private void constructType(List<FieldWriteInfo> args, TypeName typeName, String fieldName, CodeBlock.Builder block) {
    String initializer = "$1T $2N = new $1T(";
    int paramsOffset = 2;
    int numConstructorArgs = args.size();
    Object[] params = new Object[numConstructorArgs + paramsOffset];
    params[0] = typeName;
    params[1] = fieldName;
    for (int i = 0; i < numConstructorArgs; i++) {
      FieldWriteInfo field = args.get(i);
      params[i + paramsOffset] = field.value;
      initializer += "$" + (i + paramsOffset + 1) + "L";
      if (i != args.size() - 1) {
        initializer += ", ";
      }
    }
    initializer += ")";
    block.addStatement(initializer, params);
  }

  private Map<ClassName, CodeBlock> getTypeAdaptersMap(Set<Adapter> typeAdapters, CodeBlock.Builder block,
                                                       Set<String> scopedVariableNames) {
    Map<ClassName, CodeBlock> typeAdapterMap = new LinkedHashMap<>(typeAdapters.size());
    for (Adapter adapter : typeAdapters) {
      CodeBlock literal;
      if (adapter.isSingleton()) {
        literal = CodeBlock.of("$T.INSTANCE", adapter.getClassName());
      } else {
        String simpleName = uncapitalizeFirstCharacter(adapter.getClassName().simpleName());
        String typeAdapterName = getUniqueName(simpleName, scopedVariableNames);
        block.addStatement("$T $N = new $T()", adapter.getClassName(), typeAdapterName, adapter.getClassName());
        scopedVariableNames.add(typeAdapterName);
        literal = CodeBlock.of("$N", typeAdapterName);
      }
      typeAdapterMap.put(adapter.getClassName(), literal);
    }
    return typeAdapterMap;
  }

  public static class FieldWriteInfo {
    private final String name;
    private final CodeBlock value;
    private final boolean isVisible;
    private final int constructorPosition;
    private final String setterMethodName;

    public FieldWriteInfo(String name, CodeBlock value, boolean isVisible, int constructorIndex, String setterMethod) {
      this.name = name;
      this.value = value;
      this.isVisible = isVisible;
      this.constructorPosition = constructorIndex;
      this.setterMethodName = setterMethod;
    }
  }
}
