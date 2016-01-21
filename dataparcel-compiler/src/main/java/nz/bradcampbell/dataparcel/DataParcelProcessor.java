package nz.bradcampbell.dataparcel;

import android.os.Parcelable;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.DataClass;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;

import static javax.lang.model.element.Modifier.*;
import static nz.bradcampbell.dataparcel.internal.Utils.*;

/**
 * An annotation processor that creates Parcelable wrappers for all Kotlin data classes annotated with @DataParcel
 */
@AutoService(Processor.class)
public class DataParcelProcessor extends AbstractProcessor {
  public static final String DATA_VARIABLE_NAME = "data";

  private Filer filer;
  private Types typeUtil;
  private Map<String, DataClass> parcels = new HashMap<String, DataClass>();

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    filer = env.getFiler();
    typeUtil = env.getTypeUtils();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(DataParcel.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
    if (annotations.isEmpty()) {

      // Nothing to do
      return true;
    }

    // Create a DataClass models for all classes annotated with @DataParcel
    for (Element element : roundEnvironment.getElementsAnnotatedWith(DataParcel.class)) {

      // Ensure we are dealing with a TypeElement
      if (!(element instanceof TypeElement)) {
        error(processingEnv, "@DataParcel applies to a type, " + element.getSimpleName() + " is a " + element.getKind(),
            element);
        continue;
      }

      TypeMirror elementTypeMirror = element.asType();

      // Ensure the root element isn't parameterized
      if (elementTypeMirror instanceof DeclaredType) {
        DeclaredType declaredType = (DeclaredType) elementTypeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
          error(processingEnv, "@DataParcel cannot be used directly on generic data classes.", element);
          continue;
        }
      }

      createParcel(elementTypeMirror);
    }

    // Generate java files for every data class found
    for (DataClass p : parcels.values()) {
      try {
        generateJavaFileFor(p).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing to filer.", e);
      }
    }

    return true;
  }

  /**
   * Create a Parcel wrapper for the given data class
   *
   * @param typeMirror The data class
   */
  private void createParcel(TypeMirror typeMirror) {
    TypeElement typeElement = (TypeElement) typeUtil.asElement(typeMirror);

    String classPackage = getPackageName(typeElement);
    String className = typeElement.getQualifiedName().toString();
    String wrappedClassName = generateWrappedTypeName(typeElement, typeMirror);

    // Exit early if we have already created a parcel for this data class
    if (parcels.containsKey(wrappedClassName)) return;

    List<Property> properties = new ArrayList<Property>();
    List<TypeMirror> variableDataParcelDependencies = new ArrayList<TypeMirror>();

    // Get all member variable elements in the data class
    List<VariableElement> variableElements = getFields(typeElement);

    boolean requiresClassLoader = false;

    for (int i = 0; i < variableElements.size(); i++) {
      VariableElement variableElement = variableElements.get(i);

      // A field is only "nullable" when annotated with @Nullable
      boolean isNullable = !isFieldRequired(variableElement);

      // Parse the property type into a Property.Type object and find all recursive data class dependencies
      Property.Type propertyType = parsePropertyType(variableElement.asType(), typeMirror, variableDataParcelDependencies);

      // TODO: Validation of data class
      String getterMethodName = "component" + (i + 1);

      Property property = createProperty(propertyType, isNullable, getterMethodName);
      properties.add(property);

      requiresClassLoader |= property.requiresClassLoader();
    }

    parcels.put(wrappedClassName, new DataClass(properties, classPackage, wrappedClassName, TypeName.get(typeMirror), requiresClassLoader));

    // Build parcel dependencies
    for (TypeMirror requiredParcel : variableDataParcelDependencies) {
      createParcel(requiredParcel);
    }
  }

  private String generateWrappedTypeName(TypeElement typeElement, TypeMirror typeMirror) {
    StringBuilder sb = new StringBuilder();
    typeToString(typeMirror, sb, '$');
    String ss = sb.toString();
    String inner = "";
    if (!typeElement.toString().equals(ss)) {
      inner = Long.toString(ss.hashCode()).replace('-', '_');
    }
    return typeElement.getSimpleName().toString() + inner + "Parcel";
  }

  /**
   * Parses a TypeMirror into a Property.Type object. While doing so, this method will find all DataParcel
   * dependencies and append them to variableDataParcelDependencies.
   *
   * @param variable The member variable variable
   * @param variableDataParcelDependencies A list to hold all recursive dependencies
   * @return The parsed variable
   */
  private Property.Type parsePropertyType(TypeMirror variable, TypeMirror dataClass, List<TypeMirror> variableDataParcelDependencies) {

    // The element associated, or null
    Element element = typeUtil.asElement(variable);

    // Find and replace type parameter arguments with the provided type in the data class. e.g. a variable defined
    // as ExampleClass<Integer>, this will replace type T with Integer when processing ExampleClassIntegerParcel.java.
    if (element != null && element.getKind() == ElementKind.TYPE_PARAMETER) {
      TypeElement dataClassElement = (TypeElement) typeUtil.asElement(dataClass);
      List<? extends TypeParameterElement> typeParameterElements = dataClassElement.getTypeParameters();
      int numTypeParams = typeParameterElements.size();
      for (int i = 0; i < numTypeParams; i++) {
        TypeParameterElement p = typeParameterElements.get(i);
        if (p.equals(element)) {
          variable = ((DeclaredType) dataClass).getTypeArguments().get(i);
          break;
        }
      }
    }

    TypeMirror erasedType = typeUtil.erasure(variable);

    // List of variable arguments for this property
    List<Property.Type> childTypes = null;

    // The variable that allows this variable to be parcelable, or null
    TypeName parcelableTypeName = getParcelableType(typeUtil, erasedType);
    boolean isParcelable = parcelableTypeName != null;

    TypeName typeName = ClassName.get(erasedType);
    TypeName wrappedTypeName = typeName;
    TypeName wildcardTypeName = typeName;

    // The variable element associated, or null
    Element typeElement = typeUtil.asElement(erasedType);

    TypeMirror noWildCardType = variable;
    if (variable instanceof WildcardType) {

      // Properties using Kotlin's @JvmWildcard will fall into here
      noWildCardType = ((WildcardType) variable).getExtendsBound();
    }

    if (isParcelable) {

      if (noWildCardType instanceof DeclaredType) {

        // Parse variable arguments
        DeclaredType declaredType = (DeclaredType) noWildCardType;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        // Parse a "child variable" for each variable argument
        int numTypeArgs = typeArguments.size();
        if (numTypeArgs > 0) {

          childTypes = new ArrayList<Property.Type>(numTypeArgs);
          TypeName[] parameterArray = new TypeName[numTypeArgs];
          TypeName[] wildcardParameterArray = new TypeName[numTypeArgs];
          TypeName[] wrappedParameterArray = new TypeName[numTypeArgs];

          for (int i = 0; i < numTypeArgs; i++) {
            Property.Type argType = parsePropertyType(typeArguments.get(i), dataClass, variableDataParcelDependencies);
            childTypes.add(argType);
            parameterArray[i] = argType.getTypeName();
            wildcardParameterArray[i] = argType.getWildcardTypeName();
            wrappedParameterArray[i] = argType.getWrappedTypeName();
          }

          wrappedTypeName = ParameterizedTypeName.get((ClassName) typeName, wrappedParameterArray);
          wildcardTypeName = ParameterizedTypeName.get((ClassName) typeName, wildcardParameterArray);
          typeName = ParameterizedTypeName.get((ClassName) typeName, parameterArray);
        }
      }

      if (noWildCardType instanceof ArrayType) {
        ArrayType arrayType = (ArrayType) noWildCardType;

        // Array types will always have 1 "child variable" which is the component variable
        childTypes = new ArrayList<Property.Type>(1);
        Property.Type componentType = parsePropertyType(arrayType.getComponentType(), dataClass, variableDataParcelDependencies);
        childTypes.add(componentType);

        wrappedTypeName = ArrayTypeName.of(componentType.getWrappedTypeName());
        typeName = ArrayTypeName.of(componentType.getTypeName());
        wildcardTypeName = ArrayTypeName.of(componentType.getWildcardTypeName());
      }

      // Add the wildcard back if it existed
      if (variable instanceof WildcardType) {
        wildcardTypeName = WildcardTypeName.subtypeOf(wildcardTypeName);
      }

    } else {

      // Update wildcard and typename to include wildcards and generics
      wildcardTypeName = TypeName.get(variable);
      typeName = TypeName.get(noWildCardType);

      variableDataParcelDependencies.add(noWildCardType);

      // This is (one of) the reason(s) it is not parcelable. Assume it contains a data object as a parameter
      TypeElement requiredElement = (TypeElement) typeElement;
      String packageName = getPackageName(requiredElement);
      String className = generateWrappedTypeName(requiredElement, noWildCardType);
      parcelableTypeName = wrappedTypeName = ClassName.get(packageName, className);
    }

    boolean isInterface = typeElement != null && typeElement.getKind() == ElementKind.INTERFACE;

    boolean requiresClassLoader = requiresClassLoader(parcelableTypeName);
    if (childTypes != null) {
      for (Property.Type childProperty : childTypes) {
        requiresClassLoader |= childProperty.requiresClassLoader();
      }
    }

    return new Property.Type(childTypes, parcelableTypeName, typeName, wrappedTypeName, wildcardTypeName, isInterface,
        requiresClassLoader);
  }

  private JavaFile generateJavaFileFor(DataClass dataClass) {
    TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(dataClass.getWrapperClassName().simpleName())
        .addModifiers(PUBLIC)
        .addSuperinterface(Parcelable.class);

    FieldSpec classLoader = null;
    if (dataClass.requiresClassLoader()) {
      classLoader = generateClassLoaderField(dataClass);
      wrapperBuilder.addField(classLoader);
    }

    wrapperBuilder.addField(generateCreator(dataClass))
        .addField(generateContentsField(dataClass))
        .addMethod(generateWrapMethod(dataClass))
        .addMethod(generateContentsConstructor(dataClass))
        .addMethod(generateParcelConstructor(dataClass, classLoader))
        .addMethod(generateGetter(dataClass))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(dataClass));

    // Build the java file
    return JavaFile.builder(dataClass.getClassPackage(), wrapperBuilder.build()).build();
  }

  private FieldSpec generateClassLoaderField(DataClass dataClass) {
    return FieldSpec.builder(ClassLoader.class, "CLASS_LOADER",  Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
        .initializer("$T.class.getClassLoader()", dataClass.getClassName())
        .build();
  }

  private FieldSpec generateCreator(DataClass dataClass) {
    ClassName className = dataClass.getWrapperClassName();
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, className);

    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(CodeBlock.builder()
            .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, className))
            .beginControlFlow("@$T public $T createFromParcel($T in)", ClassName.get(Override.class), className,
                ClassName.get(android.os.Parcel.class))
            .addStatement("return new $T(in)", className)
            .endControlFlow()
            .beginControlFlow("@$T public $T[] newArray($T size)", ClassName.get(Override.class), className, int.class)
            .addStatement("return new $T[size]", className)
            .endControlFlow()
            .unindent()
            .add("}")
            .build())
        .build();
  }

  private FieldSpec generateContentsField(DataClass dataClass) {
    return FieldSpec.builder(dataClass.getClassName(), DATA_VARIABLE_NAME, PRIVATE, FINAL).build();
  }

  private MethodSpec generateWrapMethod(DataClass dataClass) {
    ClassName className = dataClass.getWrapperClassName();
    return MethodSpec.methodBuilder("wrap")
        .addModifiers(PUBLIC, STATIC, FINAL)
        .addParameter(dataClass.getClassName(), DATA_VARIABLE_NAME)
        .addStatement("return new $T($N)", className, DATA_VARIABLE_NAME)
        .returns(className)
        .build();
  }

  private MethodSpec generateContentsConstructor(DataClass dataClass) {
    return MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(dataClass.getClassName(), DATA_VARIABLE_NAME)
        .addStatement("this.$N = $N", DATA_VARIABLE_NAME, DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateParcelConstructor(DataClass dataClass, FieldSpec classLoader) {
    ParameterSpec in = ParameterSpec
        .builder(ClassName.get("android.os", "Parcel"), "in")
        .build();

    MethodSpec.Builder builder = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(in);

    List<Property> properties = dataClass.getProperties();
    if (properties != null) {

      String initializer = "this.$N = new $T(";
      int paramsOffset = 2;
      Object[] params = new Object[properties.size() + paramsOffset];
      params[0] = DATA_VARIABLE_NAME;
      params[1] = dataClass.getClassName();

      CodeBlock.Builder block = CodeBlock.builder();

      for (int i = 0; i < properties.size(); i++) {
        Property p = properties.get(i);
        params[i + paramsOffset] = p.readFromParcel(block, in, classLoader);
        initializer += "$L";
        if (i != properties.size() - 1) {
          initializer += ", ";
        }
      }

      builder.addCode(block.build());

      initializer += ")";
      builder.addStatement(initializer, params);
    }

    return builder.build();
  }

  private MethodSpec generateGetter(DataClass dataClass) {
    return MethodSpec.methodBuilder("getContents")
        .addModifiers(PUBLIC)
        .returns(dataClass.getClassName())
        .addStatement("return $N", DATA_VARIABLE_NAME)
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

  private MethodSpec generateWriteToParcel(DataClass dataClass) {
    ParameterSpec dest = ParameterSpec
        .builder(ClassName.get("android.os", "Parcel"), "dest")
        .build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(int.class, "flags");

    CodeBlock.Builder block = CodeBlock.builder();
    for (Property p : dataClass.getProperties()) {
      TypeName wildCardTypeName = p.getPropertyType().getWildcardTypeName();
      block.addStatement("$T $N = $N.$N()", wildCardTypeName, p.getName(), DATA_VARIABLE_NAME, p.getName());
      CodeBlock sourceLiteral = literal("$N", p.getName());
      p.writeToParcel(block, dest, sourceLiteral);
    }

    return builder.addCode(block.build()).build();
  }
}
