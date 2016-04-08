package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static nz.bradcampbell.paperparcel.PaperParcelProcessor.DATA_VARIABLE_NAME;
import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;
import static nz.bradcampbell.paperparcel.utils.StringUtils.uncapitalizeFirstCharacter;

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
import nz.bradcampbell.paperparcel.DataClassInitializer.InitializationStrategy;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.DataClass;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class WrapperGenerator {
  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");
  private static final ClassName TYPED_PARCELABLE = ClassName.get("nz.bradcampbell.paperparcel", "TypedParcelable");

  private final Filer filer;

  public WrapperGenerator(Filer filer) {
    this.filer = filer;
  }

  public void generateParcelableWrappers(Set<DataClass> dataClasses) {
    for (DataClass dataClass : dataClasses) {
      try {
        generateParcelableWrapper(dataClass).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing to filer." + e.getMessage(), e);
      }
    }
  }

  private JavaFile generateParcelableWrapper(DataClass dataClass) throws IOException {
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
        classLoader, dataClass.getRequiredTypeAdapters(), dataClass.getInitializationStrategy());

    wrapperBuilder.addField(creator)
        .addField(generateContentsField(dataClass.getClassName()))
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
      FieldSpec classLoader, Set<Adapter> typeAdapters, @Nullable InitializationStrategy initializationStrategy) {

    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, wrapperClassName);

    String inParameterName = "in";
    ParameterSpec in = ParameterSpec.builder(PARCEL, inParameterName).build();

    CodeBlock.Builder creatorInitializer = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, wrapperClassName))
        .beginControlFlow("@$T public $T createFromParcel($T $N)", Override.class, wrapperClassName, PARCEL, in);

    if (isSingleton) {
      creatorInitializer.addStatement("return new $T($T.INSTANCE)", wrapperClassName, typeName);
    } else {
      List<DataClassInitializer.Field> fields = new ArrayList<>(properties.size());

      CodeBlock.Builder block = CodeBlock.builder();

      Set<String> scopedVariableNames = new LinkedHashSet<>();
      scopedVariableNames.add(inParameterName);

      Map<ClassName, CodeBlock> typeAdapterMap = new LinkedHashMap<>(typeAdapters.size());
      for (Adapter adapter : typeAdapters) {
        CodeBlock literal;
        if (adapter.isSingleton()) {
          literal = CodeBlock.of("$T.INSTANCE", adapter.getClassName());
        } else {
          String typeAdapterName = getUniqueName(
              uncapitalizeFirstCharacter(adapter.getClassName().simpleName()), scopedVariableNames);
          block.addStatement("$T $N = new $T()", adapter.getClassName(), typeAdapterName, adapter.getClassName());
          scopedVariableNames.add(typeAdapterName);
          literal = CodeBlock.of("$N", typeAdapterName);
        }
        typeAdapterMap.put(adapter.getClassName(), literal);
      }

      for (Property p : properties) {
        String name = p.getName();
        CodeBlock value = p.readFromParcel(block, in, classLoader, typeAdapterMap, scopedVariableNames);
        fields.add(new DataClassInitializer.Field(name, value));
      }

      creatorInitializer.add(block.build());

      DataClassInitializer dataClassInitializer = new DataClassInitializer();
      CodeBlock dataInitializer = dataClassInitializer.initialize(typeName, creatorInitializer, fields,
                                                                  scopedVariableNames, initializationStrategy);
      creatorInitializer.addStatement("return new $T($L)", wrapperClassName, dataInitializer);
    }

    creatorInitializer.endControlFlow()
        .beginControlFlow("@$T public $T[] newArray($T size)", Override.class, wrapperClassName, int.class)
        .addStatement("return new $T[size]", wrapperClassName)
        .endControlFlow()
        .unindent()
        .add("}");

    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(creatorInitializer.build())
        .build();
  }

  private FieldSpec generateContentsField(TypeName className) {
    return FieldSpec.builder(className, DATA_VARIABLE_NAME, PUBLIC, FINAL).build();
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

    // Create the required TypeAdapters
    Map<ClassName, CodeBlock> typeAdapterMap = new LinkedHashMap<>(typeAdapters.size());
    for (Adapter adapter : typeAdapters) {
      CodeBlock literal;
      if (adapter.isSingleton()) {
        literal = CodeBlock.of("$T.INSTANCE", adapter.getClassName());
      } else {
        String typeAdapterName = getUniqueName(
            uncapitalizeFirstCharacter(adapter.getClassName().simpleName()), scopedVariableNames);
        block.addStatement("$T $N = new $T()", adapter.getClassName(), typeAdapterName, adapter.getClassName());
        // Add type adapter name to scoped names
        scopedVariableNames.add(typeAdapterName);
        literal = CodeBlock.of("$N", typeAdapterName);
      }
      typeAdapterMap.put(adapter.getClassName(), literal);
    }

    for (Property p : properties) {
      String getterMethodName = p.getAccessorMethodName();
      String accessorStrategy = getterMethodName == null ? p.getName() : getterMethodName + "()";

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
}
