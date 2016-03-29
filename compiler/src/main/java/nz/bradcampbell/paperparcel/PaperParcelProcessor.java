package nz.bradcampbell.paperparcel;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOOLEAN_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_BOOLEAN;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_BYTE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_CHAR;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_DOUBLE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_FLOAT;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_INT;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_LONG;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BOXED_SHORT;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BUNDLE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.BYTE_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.CHAR_SEQUENCE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.ENUM;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.IBINDER;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.INT_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.LIST;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.LONG_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.MAP;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.OBJECT_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.PERSISTABLE_BUNDLE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SERIALIZABLE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SET;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SIZE;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SIZEF;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SPARSE_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.SPARSE_BOOLEAN_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.STRING;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.STRING_ARRAY;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.TYPE_ADAPTER;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.getParcelableType;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.generateWrappedTypeName;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.getFields;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.getPackageName;
import static nz.bradcampbell.paperparcel.internal.utils.TypeUtils.hasTypeArguments;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;

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
import nz.bradcampbell.paperparcel.internal.properties.ArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.BooleanArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.BooleanProperty;
import nz.bradcampbell.paperparcel.internal.properties.BundleProperty;
import nz.bradcampbell.paperparcel.internal.properties.ByteArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.ByteProperty;
import nz.bradcampbell.paperparcel.internal.properties.CharProperty;
import nz.bradcampbell.paperparcel.internal.properties.CharSequenceProperty;
import nz.bradcampbell.paperparcel.internal.properties.DoubleProperty;
import nz.bradcampbell.paperparcel.internal.properties.FloatProperty;
import nz.bradcampbell.paperparcel.internal.properties.IBinderProperty;
import nz.bradcampbell.paperparcel.internal.properties.IntArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.IntProperty;
import nz.bradcampbell.paperparcel.internal.properties.ListProperty;
import nz.bradcampbell.paperparcel.internal.properties.LongArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.LongProperty;
import nz.bradcampbell.paperparcel.internal.properties.MapProperty;
import nz.bradcampbell.paperparcel.internal.properties.ParcelableProperty;
import nz.bradcampbell.paperparcel.internal.properties.PersistableBundleProperty;
import nz.bradcampbell.paperparcel.internal.properties.SerializableProperty;
import nz.bradcampbell.paperparcel.internal.properties.SetProperty;
import nz.bradcampbell.paperparcel.internal.properties.ShortProperty;
import nz.bradcampbell.paperparcel.internal.properties.SizeFProperty;
import nz.bradcampbell.paperparcel.internal.properties.SizeProperty;
import nz.bradcampbell.paperparcel.internal.properties.SparseArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.SparseBooleanArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.StringArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.StringProperty;
import nz.bradcampbell.paperparcel.internal.properties.TypeAdapterProperty;
import nz.bradcampbell.paperparcel.internal.utils.AnnotationUtils;
import nz.bradcampbell.paperparcel.internal.utils.PropertyUtils;
import nz.bradcampbell.paperparcel.internal.utils.TypeUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * An annotation processor that creates Parcelable wrappers for all Kotlin data classes annotated with @PaperParcel
 */
@AutoService(Processor.class)
public class PaperParcelProcessor extends AbstractProcessor {
  public static final String DATA_VARIABLE_NAME = "data";

  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");
  private static final ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
  private static final ClassName TYPED_PARCELABLE = ClassName.get("nz.bradcampbell.paperparcel", "TypedParcelable");

  private Filer filer;
  private Types typeUtil;
  private Elements elementUtils;

  private Map<TypeName, ClassName> defaultAdapters = new HashMap<>();
  private Set<DataClass> dataClasses = new LinkedHashSet<>();

  private static void error(ProcessingEnvironment processingEnv, String message, Element element) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    types.add(PaperParcel.class.getCanonicalName());
    types.add(DefaultAdapter.class.getCanonicalName());
    types.add(TypeAdapters.class.getCanonicalName());
    return types;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    typeUtil = env.getTypeUtils();
    elementUtils = env.getElementUtils();
    filer = env.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

    // Processing is over. Generate a mapping class for every data class found
    if (roundEnvironment.processingOver()) {
      try {
        ParcelMappingGenerator.generateParcelableMapping(dataClasses).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing Lookup to filer." + e.getMessage(), e);
      }
      return true;
    }

    // Find all global-scoped TypeAdapters
    for (Element element : roundEnvironment.getElementsAnnotatedWith(DefaultAdapter.class)) {

      // Ensure we are dealing with a TypeElement
      if (!(element instanceof TypeElement)) {
        error(processingEnv, "@GlobalTypeAdapter applies to a type, " + element.getSimpleName() + " is a "
                             + element.getKind(), element);
        continue;
      }

      // Ensure we are dealing with a TypeAdapter
      TypeMirror elementMirror = element.asType();
      TypeMirror typeAdapterMirror =
          typeUtil.erasure(elementUtils.getTypeElement(TypeAdapter.class.getCanonicalName()).asType());

      if (!(typeUtil.isAssignable(elementMirror, typeAdapterMirror))) {
        error(processingEnv, element.getSimpleName() + " needs to implement TypeAdapter", element);
        continue;
      }

      DeclaredType ta = (DeclaredType) element.asType();
      TypeName typeAdapterType = PropertyUtils.getTypeAdapterType(typeUtil, ta);
      defaultAdapters.put(typeAdapterType, (ClassName) TypeName.get(ta));
    }

    // Create a DataClass models for all classes annotated with @PaperParcel
    for (Element element : roundEnvironment.getElementsAnnotatedWith(PaperParcel.class)) {

      // Ensure we are dealing with a TypeElement
      if (!(element instanceof TypeElement)) {
        error(processingEnv, "@PaperParcel applies to a type, " + element.getSimpleName() + " is a "
                             + element.getKind(), element);
        continue;
      }

      TypeMirror elementTypeMirror = element.asType();

      // Ensure the root element isn't parameterized
      if (hasTypeArguments(elementTypeMirror)) {
        error(processingEnv, "@PaperParcel cannot be used directly on generic data classes.", element);
        continue;
      }

      DataClass dataClass = createParcel(elementTypeMirror);
      dataClasses.add(dataClass);
      try {
        generateParcelableWrapper(dataClass).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing to filer." + e.getMessage(), e);
      }
    }

    return true;
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
    Set<ClassName> requiredTypeAdapters = new HashSet<>();
    boolean requiresClassLoader = false;
    Map<TypeName, ClassName> typeAdapters = new HashMap<>();
    boolean isSingleton = TypeUtils.isSingleton(typeUtil, typeElement);

    // If the class is a singleton, we don't need to read/write variables. We can just use the static instance.
    if (!isSingleton) {

      // Override the type adapters with the current element preferences
      TypeElement tempTypeElement = typeElement;
      while (tempTypeElement != null && !applyTypeAdaptersFromElement(tempTypeElement, typeAdapters)) {
        tempTypeElement = (TypeElement) typeUtil.asElement(tempTypeElement.getSuperclass());
      }

      for (VariableElement variableElement : getPropertyElements(typeElement)) {

        // Determine how we will access this property and in doing so, validate the property
        ExecutableElement accessorMethod;
        try {
          accessorMethod = getAccessorMethod(typeElement, variableElement);
        } catch (PropertyValidationException e) {
          error(processingEnv, e.getMessage(), e.source);
          continue;
        }

        Map<TypeName, ClassName> variableScopedTypeAdapters = getTypeAdapterMapForVariable(
            typeAdapters, variableElement, accessorMethod);

        String name = variableElement.getSimpleName().toString();

        // A field is considered "nullable" when it is a non-primitive and not annotated with @NonNull or @NotNull
        boolean isPrimitive = variableElement.asType().getKind().isPrimitive();
        boolean annotatedWithNonNull = accessorMethod != null ? AnnotationUtils.isFieldRequired(accessorMethod)
                                                              : AnnotationUtils.isFieldRequired(variableElement);
        boolean isNullable = !isPrimitive && !annotatedWithNonNull;

        // Parse the property type into a Property.Type object and find all recursive data class dependencies
        String accessorMethodName = accessorMethod == null ? null : accessorMethod.getSimpleName().toString();
        Property property = parseProperty(variableElement.asType(), typeMirror, isNullable, name, accessorMethodName,
                                          variableScopedTypeAdapters);

        properties.add(property);

        requiredTypeAdapters.addAll(property.requiredTypeAdapters());

        requiresClassLoader |= property.requiresClassLoader();
      }
    }

    return new DataClass(properties, classPackage, wrappedClassName, TypeName.get(typeMirror), requiresClassLoader,
                         requiredTypeAdapters, isSingleton);
  }

  private List<? extends VariableElement> getPropertyElements(TypeElement typeElement) {
    // Get all members
    List<VariableElement> fieldElements = getFields(typeUtil, typeElement);

    // Get constructor
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
    constructors = filterNonVisibleElements(constructors);
    constructors = filterExecutableElementsOfSize(constructors, fieldElements.size());
    if (constructors.size() == 0) {
      throw new IllegalStateException("Could not find an appropriate constructor on " + typeElement.toString() + ". " +
                                      "There were " + fieldElements.size() + " member variables, but no visible " +
                                      "constructors with that many elements.");
    }
    if (constructors.size() > 1) {
      throw new IllegalStateException(typeElement.toString() + " has more than one valid constructor. PaperParcel " +
                                      "requires only one constructor.");
    }
    ExecutableElement primaryConstructor = constructors.get(0);

    return getOrderedVariables(primaryConstructor, fieldElements, typeElement);
  }

  private static <T extends ExecutableElement> List<T> filterExecutableElementsOfSize(List<T> list, int expectedSize) {
    ArrayList<T> filteredList = new ArrayList<>(list.size());
    for (T e : list) {
      if (e.getParameters().size() == expectedSize) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  private static <T extends Element> List<T> filterNonVisibleElements(List<T> list) {
    ArrayList<T> filteredList = new ArrayList<>(list.size());
    for (T e : list) {
      Set<Modifier> modifiers = e.getModifiers();
      if (!modifiers.contains(PRIVATE) && !modifiers.contains(PROTECTED)) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  private boolean applyTypeAdaptersFromElement(Element element, Map<TypeName, ClassName> typeAdapters) {
    if (element != null) {
      Map<String, Object> annotation = AnnotationUtils.getAnnotation(TypeAdapters.class, element);
      if (annotation != null) {
        Object[] typeAdaptersArray = (Object[]) annotation.get("value");
        for (Object o : typeAdaptersArray) {
          DeclaredType ta = (DeclaredType) o;
          TypeName typeAdapterType = PropertyUtils.getTypeAdapterType(typeUtil, ta);
          typeAdapters.put(typeAdapterType, (ClassName) TypeName.get(ta));
        }
        return true;
      }
    }
    return false;
  }

  private List<VariableElement> getOrderedVariables(
      ExecutableElement constructor, List<VariableElement> fieldElements, TypeElement typeElement) {

    List<? extends VariableElement> params = constructor.getParameters();

    // Attempt to match constructor params by name
    Map<Name, VariableElement> fieldNamesToFieldMap = new HashMap<>(fieldElements.size());
    for (VariableElement field : fieldElements) {
      fieldNamesToFieldMap.put(field.getSimpleName(), field);
    }
    boolean canUseConstructorArguments = true;
    List<VariableElement> orderedFields = new ArrayList<>(fieldElements.size());
    for (VariableElement param : params) {
      VariableElement field = fieldNamesToFieldMap.get(param.getSimpleName());
      if (field == null) {
        canUseConstructorArguments = false;
        break;
      }
      orderedFields.add(field);
    }
    if (canUseConstructorArguments) {
      return orderedFields;
    }

    // Attempt to match constructor params by order (support for https://youtrack.jetbrains.com/issue/KT-9609)
    boolean areParametersInOrder = true;
    for (int i = 0; i < params.size(); i++) {
      VariableElement param = params.get(i);
      VariableElement field = fieldElements.get(i);
      if (!typeUtil.isAssignable(field.asType(), param.asType())) {
        areParametersInOrder = false;
        break;
      }
    }
    if (areParametersInOrder) {
      return fieldElements;
    }

    throw new NoValidConstructorFoundException(
        "No valid constructor found while processing " + typeElement.getQualifiedName() + ". The constructor parameters" +
        " and member variables need to be in the same order.");
  }

  private ExecutableElement getAccessorMethod(TypeElement typeElement, VariableElement variableElement)
      throws PropertyValidationException {

    String variableName = variableElement.getSimpleName().toString().toLowerCase();

    // If the name is custom, return this straight away
    AccessorName accessorMethod = variableElement.getAnnotation(AccessorName.class);
    if (accessorMethod != null) {
      variableName = accessorMethod.value().toLowerCase();
    }

    Set<Modifier> modifiers = variableElement.getModifiers();

    // If the property visibility is package default or public, then we don't need a "getter" method
    if (!(modifiers.contains(PRIVATE) || modifiers.contains(PROTECTED))) {
      return null;
    }

    for (ExecutableElement method : MoreElements.getLocalAndInheritedMethods(typeElement, elementUtils)) {
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

    throw new PropertyValidationException(
        "Could not find getter method for variable '" + variableName + "'.\nTry annotating your " +
        "variable with '" + AccessorName.class.getCanonicalName() + "' or renaming your variable to follow " +
        "the documented conventions.\nAlternatively your property can be have default or public visibility.",
        variableElement);
  }

  private Map<TypeName, ClassName> getTypeAdapterMapForVariable(
      Map<TypeName, ClassName> classScopedTypeAdapters, VariableElement variableElement,
      ExecutableElement accessorMethod) {

    Map<TypeName, ClassName> tempTypeAdapters = new HashMap<>(classScopedTypeAdapters);
    boolean applied = applyTypeAdaptersFromElement(variableElement, tempTypeAdapters);
    if (!applied) {
      applyTypeAdaptersFromElement(accessorMethod, tempTypeAdapters);
    }

    return tempTypeAdapters;
  }

  /**
   * Parses a TypeMirror into a Property object.
   *
   * @param variable  The member variable variable
   * @param dataClass The class that owns this property
   * @param isNullable True if the property is nullable
   * @param name The name of the property
   * @param accessorMethodName The string name of the accessor method, or null if the property is already accessible
   * @param typeAdapters All type adapters are in scope of this property
   * @return The parsed property
   */
  private Property parseProperty(TypeMirror variable, TypeMirror dataClass, boolean isNullable, String name,
                                 @Nullable String accessorMethodName, Map<TypeName, ClassName> typeAdapters) {

    variable = getActualTypeParameter(variable, dataClass);

    TypeMirror erasedType = typeUtil.erasure(variable);

    TypeName parcelableTypeName = getParcelableType(typeUtil, erasedType);

    boolean isInterface = TypeUtils.isInterface(typeUtil, erasedType);

    TypeMirror type = variable;
    if (type instanceof WildcardType) {
      type = ((WildcardType) variable).getExtendsBound();
    }

    TypeName erasedTypeName = TypeName.get(erasedType);
    ClassName typeAdapter = typeAdapters.get(erasedTypeName);
    if (typeAdapter == null) {
      typeAdapter = defaultAdapters.get(erasedTypeName);
    }
    if (typeAdapter != null) {
      parcelableTypeName = TYPE_ADAPTER;
    }

    TypeName typeName = TypeName.get(variable);

    if (STRING.equals(parcelableTypeName)) {
      return new StringProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (INT.equals(parcelableTypeName)) {
      return new IntProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_INT.equals(parcelableTypeName)) {
      return new IntProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (LONG.equals(parcelableTypeName)) {
      return new LongProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_LONG.box().equals(parcelableTypeName)) {
      return new LongProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BYTE.equals(parcelableTypeName)) {
      return new ByteProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_BYTE.equals(parcelableTypeName)) {
      return new ByteProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BOOLEAN.equals(parcelableTypeName)) {
      return new BooleanProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_BOOLEAN.equals(parcelableTypeName)) {
      return new BooleanProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (FLOAT.equals(parcelableTypeName)) {
      return new FloatProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_FLOAT.equals(parcelableTypeName)) {
      return new FloatProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (CHAR.equals(parcelableTypeName)) {
      return new CharProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if(BOXED_CHAR.equals(parcelableTypeName)) {
      return new CharProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (DOUBLE.equals(parcelableTypeName)) {
      return new DoubleProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_DOUBLE.equals(parcelableTypeName)) {
      return new DoubleProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SHORT.equals(parcelableTypeName)) {
      return new ShortProperty(false, typeName, isInterface, name, accessorMethodName);
    } else if (BOXED_SHORT.equals(parcelableTypeName)) {
      return new ShortProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (MAP.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property keyProperty = parseProperty(typeArguments.get(0), dataClass, true, name + "Key", null, typeAdapters);
      Property valueProperty = parseProperty(typeArguments.get(1), dataClass, true, name + "Value", null, typeAdapters);
      return new MapProperty(keyProperty, valueProperty, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (LIST.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Item", null, typeAdapters);
      return new ListProperty(typeArgument, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SET.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Item", null, typeAdapters);
      return new SetProperty(typeArgument, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BOOLEAN_ARRAY.equals(parcelableTypeName)) {
      return new BooleanArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BYTE_ARRAY.equals(parcelableTypeName)) {
      return new ByteArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (INT_ARRAY.equals(parcelableTypeName)) {
      return new IntArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (LONG_ARRAY.equals(parcelableTypeName)) {
      return new LongArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (STRING_ARRAY.equals(parcelableTypeName)) {
      return new StringArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SPARSE_ARRAY.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Value", null, typeAdapters);
      return new SparseArrayProperty(typeArgument, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SPARSE_BOOLEAN_ARRAY.equals(parcelableTypeName)) {
      return new SparseBooleanArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BUNDLE.equals(parcelableTypeName)) {
      return new BundleProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (PARCELABLE.equals(parcelableTypeName)) {
      return new ParcelableProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (OBJECT_ARRAY.equals(parcelableTypeName)) {
      TypeMirror componentType = ((ArrayType) type).getComponentType();
      Property componentProperty = parseProperty(componentType, dataClass, true, name + "Component", null, typeAdapters);
      return new ArrayProperty(componentProperty, typeName, isInterface, isNullable, name, accessorMethodName);
    } else if (CHAR_SEQUENCE.equals(parcelableTypeName)) {
      return new CharSequenceProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (IBINDER.equals(parcelableTypeName)) {
      return new IBinderProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SERIALIZABLE.equals(parcelableTypeName) || ENUM.equals(parcelableTypeName)) {
      return new SerializableProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableTypeName)) {
      return new PersistableBundleProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SIZE.equals(parcelableTypeName)) {
      return new SizeProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SIZEF.equals(parcelableTypeName)) {
      return new SizeFProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (TYPE_ADAPTER.equals(parcelableTypeName)) {
      return new TypeAdapterProperty(typeAdapter, isNullable, typeName, isInterface, name, accessorMethodName);
    } else {
      throw new UnknownPropertyTypeException("PaperParcel does not support type: " + typeName);
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

  private JavaFile generateParcelableWrapper(DataClass dataClass) throws IOException {
    TypeSpec.Builder wrapperBuilder = TypeSpec.classBuilder(dataClass.getWrapperClassName().simpleName())
        .addModifiers(PUBLIC, FINAL)
        .addSuperinterface(ParameterizedTypeName.get(TYPED_PARCELABLE, dataClass.getClassName()));

    FieldSpec classLoader = null;
    if (dataClass.getRequiresClassLoader()) {
      classLoader = generateClassLoaderField(dataClass.getClassName());
      wrapperBuilder.addField(classLoader);
    }

    Map<ClassName, FieldSpec> typeAdapters = new HashMap<>();
    for (ClassName typeAdapter : dataClass.getRequiredTypeAdapters()) {
      FieldSpec field = generateTypeAdapterField(typeAdapter);
      typeAdapters.put(typeAdapter, field);
      wrapperBuilder.addField(field);
    }

    wrapperBuilder.addField(generateCreator(dataClass.getClassName(), dataClass.getWrapperClassName(),
                                            dataClass.isSingleton(), dataClass.getProperties(), classLoader,
                                            typeAdapters))
        .addField(generateContentsField(dataClass.getClassName()))
        .addMethod(generateContentsConstructor(dataClass.getClassName()))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(dataClass.getProperties(), typeAdapters));

    // Build the java file
    return JavaFile.builder(dataClass.getClassPackage(), wrapperBuilder.build()).build();
  }

  private FieldSpec generateTypeAdapterField(ClassName typeAdapter) {
    String packageName = typeAdapter.packageName().replace(".", "_");
    String simpleNames = "";
    for (String name : typeAdapter.simpleNames()) {
      simpleNames += name;
    }
    String constPackageName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, packageName);
    String constSimpleNames = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, simpleNames);
    String fieldName = packageName.isEmpty() ? constSimpleNames : constPackageName + "_" + constSimpleNames;
    return FieldSpec.builder(typeAdapter, fieldName, Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
        .initializer("new $T()", typeAdapter)
        .build();
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
      FieldSpec classLoader, Map<ClassName, FieldSpec> typeAdapters) {

    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, wrapperClassName);

    ParameterSpec in = ParameterSpec.builder(PARCEL, "in").build();

    CodeBlock.Builder creatorInitializer = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, wrapperClassName))
        .beginControlFlow("@$T public $T createFromParcel($T $N)", Override.class, wrapperClassName, PARCEL, in);

    if (isSingleton) {
      creatorInitializer.addStatement("return new $T($T.INSTANCE)", wrapperClassName, typeName);
    } else {
      String dataInitializer;
      TypeName rawTypeName;

      if (typeName instanceof ParameterizedTypeName) {
        rawTypeName = ((ParameterizedTypeName) typeName).rawType;
        dataInitializer = "$T $N = new $T<>(";
      } else {
        rawTypeName = typeName;
        dataInitializer = "$T $N = new $T(";
      }

      int paramsOffset = 3;
      Object[] params = new Object[properties.size() + paramsOffset];
      params[0] = typeName;
      params[1] = DATA_VARIABLE_NAME;
      params[2] = rawTypeName;

      CodeBlock.Builder block = CodeBlock.builder();

      for (int i = 0; i < properties.size(); i++) {
        Property p = properties.get(i);
        params[i + paramsOffset] = p.readFromParcel(block, in, classLoader, typeAdapters);
        dataInitializer += "$L";
        if (i != properties.size() - 1) {
          dataInitializer += ", ";
        }
      }

      creatorInitializer.add(block.build());

      dataInitializer += ")";
      creatorInitializer.addStatement(dataInitializer, params);
      creatorInitializer.addStatement("return new $T($N)", wrapperClassName, DATA_VARIABLE_NAME);
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

  private MethodSpec generateWriteToParcel(List<Property> properties, Map<ClassName, FieldSpec> typeAdapters) {
    ParameterSpec dest = ParameterSpec.builder(PARCEL, "dest").build();
    ParameterSpec flags = ParameterSpec.builder(int.class, "flags").build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(flags);

    CodeBlock.Builder block = CodeBlock.builder();
    for (Property p : properties) {
      String getterMethodName = p.getAccessorMethodName();
      String accessorStrategy = getterMethodName == null ? p.getName() : getterMethodName + "()";
      TypeName wildCardTypeName = p.getTypeName();
      if (wildCardTypeName instanceof WildcardTypeName) {
        wildCardTypeName = ((WildcardTypeName) wildCardTypeName).upperBounds.get(0);
      }
      block.addStatement("$T $N = $N.$N", wildCardTypeName, p.getName(), DATA_VARIABLE_NAME, accessorStrategy);
      CodeBlock sourceLiteral = PropertyUtils.literal("$N", p.getName());
      p.writeToParcel(block, dest, flags, sourceLiteral, typeAdapters);
    }

    return builder.addCode(block.build()).build();
  }

  static class NoValidConstructorFoundException extends IllegalStateException {
    NoValidConstructorFoundException(String message) {
      super(message);
    }
  }

  static class PropertyValidationException extends IllegalStateException {
    final VariableElement source;

    PropertyValidationException(String message, VariableElement source) {
      super(message);
      this.source = source;
    }
  }

  static class UnknownPropertyTypeException extends IllegalStateException {
    UnknownPropertyTypeException(String message) {
      super(message);
    }
  }
}
