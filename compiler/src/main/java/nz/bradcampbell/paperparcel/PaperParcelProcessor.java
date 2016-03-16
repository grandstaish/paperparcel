package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.generateWrappedTypeName;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.getFields;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.getPackageName;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.hasTypeArguments;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;

import com.squareup.javapoet.ArrayTypeName;
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
import nz.bradcampbell.paperparcel.internal.DataClass;
import nz.bradcampbell.paperparcel.internal.Property;
import nz.bradcampbell.paperparcel.internal.utils.AnnotationUtils;
import nz.bradcampbell.paperparcel.internal.utils.PropertyUtils;
import nz.bradcampbell.paperparcel.internal.utils.TypeUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * An annotation processor that creates Parcelable wrappers for all Kotlin data classes annotated with @PaperParcel
 */
@AutoService(Processor.class)
public class PaperParcelProcessor extends AbstractProcessor {
  private static final String DATA_VARIABLE_NAME = "data";

  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");
  private static final ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
  private static final ClassName TYPED_PARCELABLE = ClassName.get("nz.bradcampbell.paperparcel", "TypedParcelable");

  private Filer filer;
  private Types typeUtil;
  private Elements elementUtils;

  private Map<TypeName, TypeName> globalTypeAdapters = new HashMap<>();
  private Map<String, TypeMirror> allWrapperTypes = new HashMap<>();

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    typeUtil = env.getTypeUtils();
    elementUtils = env.getElementUtils();
    filer = env.getFiler();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    types.add(PaperParcel.class.getCanonicalName());
    types.add(GlobalTypeAdapter.class.getCanonicalName());
    types.add(FieldTypeAdapter.class.getCanonicalName());
    return types;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

    // Processing is over. Generate java files for every data class found
    if (roundEnvironment.processingOver()) {

      Set<DataClass> dataClasses = new LinkedHashSet<>();
      for (TypeMirror paperParcelType : allWrapperTypes.values()) {
        DataClass dataClass = createParcel(paperParcelType);
        dataClasses.add(dataClass);
        try {
          generateParcelableWrapper(dataClass).writeTo(filer);
        } catch (IOException e) {
          throw new RuntimeException("An error occurred while writing to filer." + e.getMessage(), e);
        }
      }
      try {
        ParcelMappingGenerator.generateParcelableMapping(dataClasses).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing Lookup to filer." + e.getMessage(), e);
      }

      return true;
    }

    // Find all global-scoped TypeAdapters
    for (Element element : roundEnvironment.getElementsAnnotatedWith(GlobalTypeAdapter.class)) {

      // Ensure we are dealing with a TypeElement
      if (!(element instanceof TypeElement)) {
        error(processingEnv,
            "@GlobalTypeAdapter applies to a type, " + element.getSimpleName() + " is a " + element.getKind(),
            element);
        continue;
      }

      // Ensure we are dealing with a TypeAdapter
      TypeMirror elementMirror = element.asType();
      TypeMirror typeAdapterMirror = typeUtil.erasure(elementUtils.getTypeElement(TypeAdapter.class.getCanonicalName()).asType());
      if (!(typeUtil.isAssignable(elementMirror, typeAdapterMirror))) {
        error(processingEnv, element.getSimpleName() + " needs to implement TypeAdapter", element);
        continue;
      }

      DeclaredType ta = (DeclaredType) element.asType();
      TypeName typeAdapterType = PropertyUtils.getTypeAdapterType(typeUtil, ta);
      globalTypeAdapters.put(typeAdapterType, TypeName.get(ta));
    }

    // Create a DataClass models for all classes annotated with @PaperParcel
    for (Element element : roundEnvironment.getElementsAnnotatedWith(PaperParcel.class)) {

      // Ensure we are dealing with a TypeElement
      if (!(element instanceof TypeElement)) {
        error(processingEnv,
            "@PaperParcel applies to a type, " + element.getSimpleName() + " is a " + element.getKind(),
            element);
        continue;
      }

      TypeMirror elementTypeMirror = element.asType();

      // Ensure the root element isn't parameterized
      if (hasTypeArguments(elementTypeMirror)) {
        error(processingEnv, "@PaperParcel cannot be used directly on generic data classes.", element);
        continue;
      }

      allWrapperTypes.put(elementTypeMirror.toString(), elementTypeMirror);

      findNonParcelableDependencies(elementTypeMirror);
    }

    return true;
  }

  private JavaFile generateParcelableWrapper(DataClass dataClass) throws IOException {
    TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(dataClass.getWrapperClassName().simpleName())
        .addModifiers(PUBLIC, FINAL)
        .addSuperinterface(dataClass.isClassParameterized()
                           ? PARCELABLE
                           : ParameterizedTypeName.get(TYPED_PARCELABLE, dataClass.getClassName()));

    FieldSpec classLoader = null;
    if (dataClass.requiresClassLoader()) {
      classLoader = generateClassLoaderField(dataClass.getClassName());
      wrapperBuilder.addField(classLoader);
    }

    wrapperBuilder.addField(generateCreator(dataClass.getClassName(), dataClass.getWrapperClassName(), dataClass.isSingleton()))
        .addField(generateContentsField(dataClass.getClassName()))
        .addMethod(generateWrapMethod(dataClass))
        .addMethod(generateContentsConstructor(dataClass.getClassName()));

    if (!dataClass.isSingleton()) {
      wrapperBuilder.addMethod(generateParcelConstructor(dataClass.getProperties(), dataClass.getClassName(), classLoader));
    }

    wrapperBuilder.addMethod(generateGetter(dataClass.getClassName()))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(dataClass.getProperties(), dataClass.getGetterMethodMap()));

    // Build the java file
    return JavaFile.builder(dataClass.getClassPackage(), wrapperBuilder.build()).build();
  }

  /**
   * Create a Parcel wrapper for the given data class
   *
   * @param typeMirror The data class
   */
  private DataClass createParcel(TypeMirror typeMirror) {
    TypeElement typeElement = (TypeElement) typeUtil.asElement(typeMirror);

    String classPackage = getPackageName(typeElement);
    String wrappedClassName = generateWrappedTypeName(typeElement, typeMirror);
    List<Property> properties = new ArrayList<>();
    List<VariableElement> variableElements = getFields(typeUtil, typeElement);
    Map<String, String> getterMethodMap = new HashMap<>(variableElements.size());
    boolean requiresClassLoader = false;
    Map<TypeName, TypeName> typeAdapters = new HashMap<>();
    boolean isSingleton = TypeUtils.isSingleton(typeUtil, typeElement);

    // If the class is a singleton, we don't need to read/write variables. We can just use the static instance.
    if (!isSingleton) {

      // Override the type adapters with the current element preferences
      Map<String, Object> annotation = AnnotationUtils.getAnnotation(PaperParcel.class, typeElement);
      if (annotation != null) {
        Object[] typeAdaptersArray = (Object[]) annotation.get("typeAdapters");
        for (Object o : typeAdaptersArray) {
          DeclaredType ta = (DeclaredType) o;
          TypeName typeAdapterType = PropertyUtils.getTypeAdapterType(typeUtil, ta);
          typeAdapters.put(typeAdapterType, TypeName.get(ta));
        }
      }

      for (VariableElement variableElement : variableElements) {

        // Determine how we will access this property and in doing so, validate the property
        ExecutableElement accessorMethod;
        try {
          accessorMethod = getAccessorMethod(typeElement, variableElement);
        } catch (PropertyValidationException e) {
          error(processingEnv, e.getMessage(), e.source);
          continue;
        } catch (IrrelevantPropertyException e) {
          continue;
        }

        Map<TypeName, TypeName> variableScopedTypeAdapters = getTypeAdapterMapForVariable(
            typeAdapters, variableElement, accessorMethod);

        String name = variableElement.getSimpleName().toString();

        // A field is considered "nullable" when it is a non-primitive and not annotated with @NonNull or @NotNull
        boolean isPrimitive = variableElement.asType().getKind().isPrimitive();
        boolean annotatedWithNonNull = accessorMethod != null
                                       ? AnnotationUtils.isFieldRequired(accessorMethod)
                                       : AnnotationUtils.isFieldRequired(variableElement);
        boolean isNullable = !isPrimitive && !annotatedWithNonNull;

        // Parse the property type into a Property.Type object and find all recursive data class dependencies
        Property.Type propertyType =
            parsePropertyType(variableElement.asType(), typeMirror, variableScopedTypeAdapters);

        getterMethodMap.put(name, accessorMethod == null ? null : accessorMethod.getSimpleName().toString());

        Property property = PropertyUtils.createProperty(propertyType, isNullable, name);
        properties.add(property);

        requiresClassLoader |= property.requiresClassLoader();
      }
    }

    return new DataClass(properties, classPackage, wrappedClassName, getterMethodMap, TypeName.get(typeMirror),
        requiresClassLoader, isSingleton);
  }

  private ExecutableElement getAccessorMethod(TypeElement typeElement, VariableElement variableElement)
      throws PropertyValidationException, IrrelevantPropertyException {

    String variableName = variableElement.getSimpleName().toString().toLowerCase();

    // If the name is custom, return this straight away
    GetterMethodName getterMethodName = variableElement.getAnnotation(GetterMethodName.class);
    if (getterMethodName != null) {
      variableName = getterMethodName.value().toLowerCase();
    }

    Set<Modifier> modifiers = variableElement.getModifiers();

    // If the property is transient, ignore it
    if (modifiers.contains(TRANSIENT)) {
      throw new IrrelevantPropertyException();
    }

    // If the property visibility is package default or public, then we don't need a "getter" method
    if (!(modifiers.contains(PRIVATE) || modifiers.contains(PROTECTED))) {
      return null;
    }

    for (Element enclosedElement : MoreElements.getLocalAndInheritedMethods(typeElement, elementUtils)) {

      // Find all enclosing methods
      if (enclosedElement instanceof ExecutableElement) {
        ExecutableElement method = (ExecutableElement) enclosedElement;

        String result = method.getSimpleName().toString();
        String name = result.toLowerCase();

        // Check the method name is equal to the variable name, "get" + variable name, or "is" + variable name
        if (name.equals(variableName) || name.equals("get" + variableName) || name.equals("is" + variableName)) {

          // Check this method returns something
          TypeName returnType = TypeName.get(method.getReturnType());
          if (returnType.equals(TypeName.get(variableElement.asType()))) {

            // Check this method takes no parameters
            if (method.getParameters().size() == 0) {

              return method;
            }
          }
        }
      }
    }

    throw new PropertyValidationException("Could not find getter method for variable '" + variableName + "'.\nTry annotating your " +
                                          "variable with '" + GetterMethodName.class.getCanonicalName() + "' or renaming your variable to follow " +
                                          "the documented conventions.\nAlternatively your property can be have default or public visibility.", variableElement);
  }

  private Map<TypeName, TypeName> getTypeAdapterMapForVariable(
      Map<TypeName, TypeName> classScopedTypeAdapters,
      VariableElement variableElement,
      ExecutableElement accessorMethod) {

    // Find a field-scoped adapter if it exists
    Map<TypeName, TypeName> tempTypeAdapters = classScopedTypeAdapters;
    FieldTypeAdapter variableAdapter = variableElement.getAnnotation(FieldTypeAdapter.class);

    // Check the accessor method for the annotation too (AutoValue support)
    if (variableAdapter == null && accessorMethod != null) {
      variableAdapter = accessorMethod.getAnnotation(FieldTypeAdapter.class);
    }

    // http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
    DeclaredType fieldAdapter = null;
    if (variableAdapter != null) {
      try {
        variableAdapter.value();
      } catch (MirroredTypeException mte) {
        fieldAdapter = (DeclaredType) mte.getTypeMirror();
      }
    }

    // Temporarily override the type adapters map with the new type adapter (for the scope of this property)
    if (fieldAdapter != null) {
      tempTypeAdapters = new HashMap<>(classScopedTypeAdapters);
      TypeName typeAdapterType = PropertyUtils.getTypeAdapterType(typeUtil, fieldAdapter);
      tempTypeAdapters.put(typeAdapterType, TypeName.get(fieldAdapter));
    }

    return tempTypeAdapters;
  }

  /**
   * Parses a TypeMirror into a Property.Type object. While doing so, this method will find all PaperParcel
   * dependencies and append them to variableDependencies.
   *
   * @param variable The member variable variable
   * @param dataClass The class that owns this property
   * @return The parsed variable
   */
  private Property.Type parsePropertyType(TypeMirror variable, TypeMirror dataClass, Map<TypeName, TypeName> typeAdapters) {
    variable = getActualTypeParameter(variable, dataClass);

    TypeMirror erasedType = typeUtil.erasure(variable);

    // List of variable arguments for this property
    List<Property.Type> childTypes = null;

    // The variable that allows this variable to be parcelable, or null
    TypeName parcelableTypeName = allWrapperTypes.containsKey(variable.toString()) ? null : PropertyUtils.getParcelableType(typeUtil, erasedType);
    boolean isParcelable = parcelableTypeName != null;

    TypeName typeName = ClassName.get(erasedType);
    TypeName wrappedTypeName = typeName;
    TypeName wildcardTypeName = typeName;

    TypeName typeAdapter = typeAdapters.get(typeName);
    if (typeAdapter == null) {
      typeAdapter = globalTypeAdapters.get(typeName);
    }

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

          childTypes = new ArrayList<>(numTypeArgs);
          TypeName[] parameterArray = new TypeName[numTypeArgs];
          TypeName[] wildcardParameterArray = new TypeName[numTypeArgs];
          TypeName[] wrappedParameterArray = new TypeName[numTypeArgs];

          for (int i = 0; i < numTypeArgs; i++) {
            Property.Type argType = parsePropertyType(typeArguments.get(i), dataClass, typeAdapters);
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
        childTypes = new ArrayList<>(1);
        Property.Type componentType = parsePropertyType(arrayType.getComponentType(), dataClass, typeAdapters);
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

      // This is (one of) the reason(s) it is not parcelable. Assume it contains a data object as a parameter
      TypeElement requiredElement = (TypeElement) typeElement;
      String packageName = getPackageName(requiredElement);
      String className = generateWrappedTypeName(requiredElement, noWildCardType);
      parcelableTypeName = wrappedTypeName = ClassName.get(packageName, className);
    }

    boolean isInterface = typeElement != null && typeElement.getKind() == ElementKind.INTERFACE;

    boolean requiresClassLoader = PropertyUtils.requiresClassLoader(parcelableTypeName);
    if (childTypes != null) {
      for (Property.Type childProperty : childTypes) {
        requiresClassLoader |= childProperty.requiresClassLoader();
      }
    }

    // Use the AutoValue generated name as the "wrapped" type name so that the generated CREATOR object can be
    // found when un-parcelling the AutoParcel object
    if (typeElement != null) {
      try {
        //noinspection unchecked
        Class<? extends Annotation> autoValueAnnotation = (Class<? extends Annotation>) Class.forName("com.google.auto.value.AutoValue");
        Annotation autoValue = typeElement.getAnnotation(autoValueAnnotation);
        if (autoValue != null) {
          TypeElement requiredElement = (TypeElement) typeElement;
          wrappedTypeName = ClassName.bestGuess(autoValueClassName(requiredElement));
        }
      } catch (ClassNotFoundException ignored) {
      }
    }

    return new Property.Type(childTypes, parcelableTypeName, typeName, wrappedTypeName, wildcardTypeName, isInterface,
        requiresClassLoader, typeAdapter);
  }

  private String autoValueClassName(TypeElement type) {
    String name = type.getSimpleName().toString();
    while (type.getEnclosingElement() instanceof TypeElement) {
      type = (TypeElement) type.getEnclosingElement();
      name = type.getSimpleName() + "_" + name;
    }
    String pkg = getPackageName(type);
    String dot = pkg.isEmpty() ? "" : ".";
    return pkg + dot + "AutoValue_" + name;
  }

  private void findNonParcelableDependencies(TypeMirror typeMirror) {
    TypeElement typeElement = (TypeElement) typeUtil.asElement(typeMirror);
    for (VariableElement variableElement : getFields(typeUtil, typeElement)) {
      TypeMirror variableMirror = variableElement.asType();
      findNonParcelableDependencies(variableMirror, typeMirror);
    }
  }

  private void findNonParcelableDependencies(TypeMirror variable, TypeMirror dataClass) {
    variable = getActualTypeParameter(variable, dataClass);

    if (variable instanceof WildcardType) {
      // Properties using Kotlin's @JvmWildcard will fall into here
      variable = ((WildcardType) variable).getExtendsBound();
    }

    boolean isParcelable = PropertyUtils.getParcelableType(typeUtil, variable) != null;
    if (!isParcelable) {
      allWrapperTypes.put(variable.toString(), variable);
    }

    if (variable instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) variable;
      for (TypeMirror parameterType : declaredType.getTypeArguments()) {
        findNonParcelableDependencies(parameterType, dataClass);
      }
    }

    if (variable instanceof ArrayType) {
      ArrayType arrayType = (ArrayType) variable;
      findNonParcelableDependencies(arrayType.getComponentType(), dataClass);
    }

    Element childElement = typeUtil.asElement(variable);
    if (!isParcelable && childElement instanceof TypeElement) {
      findNonParcelableDependencies(variable);
    }
  }

  private TypeMirror getActualTypeParameter(TypeMirror variable, TypeMirror dataClass) {
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

    return variable;
  }

  private FieldSpec generateClassLoaderField(TypeName className) {
    if (className instanceof ParameterizedTypeName) {
      className = ((ParameterizedTypeName) className).rawType;
    }
    return FieldSpec.builder(ClassLoader.class, "CLASS_LOADER", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
        .initializer("$T.class.getClassLoader()", className)
        .build();
  }

  private FieldSpec generateCreator(TypeName typeName, ClassName wrapperClassName, boolean isSingleton) {
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, wrapperClassName);

    CodeBlock.Builder initializer = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, wrapperClassName))
        .beginControlFlow("@$T public $T createFromParcel($T in)", ClassName.get(Override.class), wrapperClassName, PARCEL);

    if (isSingleton) {
      initializer.addStatement("return new $T($T.INSTANCE)", wrapperClassName, typeName);
    } else {
      initializer.addStatement("return new $T(in)", wrapperClassName);
    }

    initializer.endControlFlow()
        .beginControlFlow("@$T public $T[] newArray($T size)", ClassName.get(Override.class), wrapperClassName, int.class)
        .addStatement("return new $T[size]", wrapperClassName)
        .endControlFlow()
        .unindent()
        .add("}");

    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(initializer.build())
        .build();
  }

  private FieldSpec generateContentsField(TypeName className) {
    return FieldSpec.builder(className, DATA_VARIABLE_NAME, PRIVATE, FINAL).build();
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

  private MethodSpec generateContentsConstructor(TypeName className) {
    return MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(className, DATA_VARIABLE_NAME)
        .addStatement("this.$N = $N", DATA_VARIABLE_NAME, DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateParcelConstructor(List<Property> properties, TypeName className, FieldSpec classLoader) {
    ParameterSpec in = ParameterSpec
        .builder(ClassName.get("android.os", "Parcel"), "in")
        .build();

    MethodSpec.Builder builder = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(in);

    if (properties != null) {

      String initializer;
      if (className instanceof ParameterizedTypeName) {
        className = ((ParameterizedTypeName) className).rawType;
        initializer = "this.$N = new $T<>(";
      } else {
        initializer = "this.$N = new $T(";
      }

      int paramsOffset = 2;
      Object[] params = new Object[properties.size() + paramsOffset];
      params[0] = DATA_VARIABLE_NAME;
      params[1] = className;

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

  private MethodSpec generateGetter(TypeName className) {
    return MethodSpec.methodBuilder("getContents")
        .addModifiers(PUBLIC)
        .returns(className)
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

  private MethodSpec generateWriteToParcel(List<Property> properties, Map<String, String> getterMethods) {
    ParameterSpec dest = ParameterSpec
        .builder(PARCEL, "dest")
        .build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(int.class, "flags");

    CodeBlock.Builder block = CodeBlock.builder();
    for (Property p : properties) {
      String getterMethodName = getterMethods == null ? null : getterMethods.get(p.getName());
      String accessorStrategy = getterMethodName == null ? p.getName() : getterMethodName + "()";
      TypeName wildCardTypeName = p.getPropertyType().getWildcardTypeName();
      if (wildCardTypeName instanceof WildcardTypeName) {
        wildCardTypeName = ((WildcardTypeName) wildCardTypeName).upperBounds.get(0);
      }
      block.addStatement("$T $N = $N.$N", wildCardTypeName, p.getName(), DATA_VARIABLE_NAME, accessorStrategy);
      CodeBlock sourceLiteral = PropertyUtils.literal("$N", p.getName());
      p.writeToParcel(block, dest, sourceLiteral);
    }

    return builder.addCode(block.build()).build();
  }

  private static void error(ProcessingEnvironment processingEnv, String message, Element element) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  static class PropertyValidationException extends IllegalStateException {
    final VariableElement source;

    public PropertyValidationException(String message, VariableElement source) {
      super(message);
      this.source = source;
    }
  }

  static class IrrelevantPropertyException extends Exception {
  }
}
