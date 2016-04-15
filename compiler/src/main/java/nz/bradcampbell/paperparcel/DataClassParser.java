package nz.bradcampbell.paperparcel;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.OBJECT;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static nz.bradcampbell.paperparcel.utils.TypeUtils.isSingleton;

import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.DataClassInitializer.InitializationStrategy;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.DataClass;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.model.properties.ArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.BooleanArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.BooleanProperty;
import nz.bradcampbell.paperparcel.model.properties.BundleProperty;
import nz.bradcampbell.paperparcel.model.properties.ByteArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.ByteProperty;
import nz.bradcampbell.paperparcel.model.properties.CharProperty;
import nz.bradcampbell.paperparcel.model.properties.CharSequenceProperty;
import nz.bradcampbell.paperparcel.model.properties.DoubleProperty;
import nz.bradcampbell.paperparcel.model.properties.EnumProperty;
import nz.bradcampbell.paperparcel.model.properties.FloatProperty;
import nz.bradcampbell.paperparcel.model.properties.IBinderProperty;
import nz.bradcampbell.paperparcel.model.properties.IntArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.IntProperty;
import nz.bradcampbell.paperparcel.model.properties.ListProperty;
import nz.bradcampbell.paperparcel.model.properties.LongArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.LongProperty;
import nz.bradcampbell.paperparcel.model.properties.MapProperty;
import nz.bradcampbell.paperparcel.model.properties.ParcelableProperty;
import nz.bradcampbell.paperparcel.model.properties.PersistableBundleProperty;
import nz.bradcampbell.paperparcel.model.properties.SetProperty;
import nz.bradcampbell.paperparcel.model.properties.ShortProperty;
import nz.bradcampbell.paperparcel.model.properties.SizeFProperty;
import nz.bradcampbell.paperparcel.model.properties.SizeProperty;
import nz.bradcampbell.paperparcel.model.properties.SparseArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.SparseBooleanArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.StringArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.StringProperty;
import nz.bradcampbell.paperparcel.model.properties.TypeAdapterProperty;
import nz.bradcampbell.paperparcel.model.properties.WrapperProperty;
import nz.bradcampbell.paperparcel.utils.AnnotationUtils;
import nz.bradcampbell.paperparcel.utils.TypeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class DataClassParser {
  public static final TypeName STRING = ClassName.get("java.lang", "String");
  public static final TypeName MAP = ClassName.get("java.util", "Map");
  public static final TypeName LIST = ClassName.get("java.util", "List");
  public static final TypeName SET = ClassName.get("java.util", "Set");
  public static final TypeName BOOLEAN_ARRAY = ArrayTypeName.of(boolean.class);
  public static final TypeName BYTE_ARRAY = ArrayTypeName.of(byte.class);
  public static final TypeName INT_ARRAY = ArrayTypeName.of(int.class);
  public static final TypeName LONG_ARRAY = ArrayTypeName.of(long.class);
  public static final TypeName STRING_ARRAY = ArrayTypeName.of(String.class);
  public static final TypeName SPARSE_ARRAY = ClassName.get("android.util", "SparseArray");
  public static final TypeName SPARSE_BOOLEAN_ARRAY = ClassName.get("android.util", "SparseBooleanArray");
  public static final TypeName BUNDLE = ClassName.get("android.os", "Bundle");
  public static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable");
  public static final TypeName PARCELABLE_ARRAY = ArrayTypeName.of(PARCELABLE);
  public static final TypeName CHAR_SEQUENCE = ClassName.get("java.lang", "CharSequence");
  public static final TypeName IBINDER = ClassName.get("android.os", "IBinder");
  public static final TypeName OBJECT_ARRAY = ArrayTypeName.of(OBJECT);
  public static final TypeName PERSISTABLE_BUNDLE = ClassName.get("android.os", "PersistableBundle");
  public static final TypeName SIZE = ClassName.get("android.util", "Size");
  public static final TypeName SIZEF = ClassName.get("android.util", "SizeF");
  public static final TypeName ENUM = ClassName.get("java.lang", "Enum");
  public static final TypeName BOXED_INT = INT.box();
  public static final TypeName BOXED_LONG = LONG.box();
  public static final TypeName BOXED_BYTE = BYTE.box();
  public static final TypeName BOXED_BOOLEAN = BOOLEAN.box();
  public static final TypeName BOXED_FLOAT = FLOAT.box();
  public static final TypeName BOXED_CHAR = CHAR.box();
  public static final TypeName BOXED_DOUBLE = DOUBLE.box();
  public static final TypeName BOXED_SHORT = SHORT.box();
  public static final TypeName TYPE_ADAPTER = TypeName.get(TypeAdapter.class);

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(
      STRING, MAP, LIST, SET, BOOLEAN_ARRAY, BYTE_ARRAY, INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY,
      SPARSE_BOOLEAN_ARRAY, BUNDLE, PARCELABLE, PARCELABLE_ARRAY, CHAR_SEQUENCE, IBINDER, OBJECT_ARRAY,
      PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, BOXED_INT, LONG, BOXED_LONG, BYTE, BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN,
      FLOAT, BOXED_FLOAT, CHAR, BOXED_CHAR, DOUBLE, BOXED_DOUBLE, SHORT, BOXED_SHORT, TYPE_ADAPTER);

  private final ProcessingEnvironment processingEnv;
  private final Types typeUtil;
  private final Elements elementUtils;

  private final Map<TypeName, Adapter> defaultAdapters;
  private final Map<ClassName, ClassName> wrappers;
  private final Map<ClassName, ClassName> delegates;

  public DataClassParser(ProcessingEnvironment processingEnv, Map<TypeName, Adapter> defaultAdapters,
                         Map<ClassName, ClassName> wrappers, Map<ClassName, ClassName> delegates) {
    this.processingEnv = processingEnv;
    this.delegates = delegates;
    this.typeUtil = processingEnv.getTypeUtils();
    this.elementUtils = processingEnv.getElementUtils();
    this.defaultAdapters = defaultAdapters;
    this.wrappers = wrappers;
  }

  public Set<DataClass> parseDataClasses(Set<TypeElement> unprocessedTypes) {
    Set<DataClass> dataClasses = new LinkedHashSet<>();
    for (Iterator<TypeElement> iterator = unprocessedTypes.iterator(); iterator.hasNext();) {
      TypeElement element = iterator.next();
      DataClass dataClass;
      try {
        dataClass = createParcel(element);
        dataClasses.add(dataClass);
        iterator.remove();
      } catch (UnknownPropertyTypeException e) {
        // Some other @PaperParcel class needs to be processed first.
        // Try to process on another pass.
      }
    }
    return dataClasses;
  }

  /**
   * Create a Parcel wrapper for the given data class
   *
   * @param typeElement The data class
   */
  private DataClass createParcel(TypeElement typeElement) throws UnknownPropertyTypeException {
    ClassName className = ClassName.get(typeElement);
    ClassName wrappedClassName = wrappers.get(className);
    ClassName delegateClassName = delegates.get(className);

    List<Property> properties = new ArrayList<>();
    Set<Adapter> requiredTypeAdapters = new HashSet<>();
    Map<TypeName, Adapter> typeAdapters = new HashMap<>();

    boolean requiresClassLoader = false;
    boolean isSingleton = isSingleton(typeUtil, typeElement);

    InitializationStrategy initializationStrategy = null;

    // If the class is a singleton, we don't need to read/write variables. We can just use the static instance.
    if (!isSingleton) {

      // Override the type adapters with the current element preferences
      TypeElement tempTypeElement = typeElement;
      while (tempTypeElement != null && !applyTypeAdaptersFromElement(tempTypeElement, typeAdapters)) {
        tempTypeElement = (TypeElement) typeUtil.asElement(tempTypeElement.getSuperclass());
      }

      DataClassValidator dataClassValidator = new DataClassValidator(processingEnv, typeUtil);
      try {
        dataClassValidator.validate(typeElement);
      } catch (DataClassValidator.IncompatibleTypeException e) {
        throw new RuntimeException(e);
      }

      initializationStrategy = dataClassValidator.getInitializationStrategy();

      for (VariableElement variableElement : dataClassValidator.getFields()) {

        // Determine how we will access this property and in doing so, validate the property
        ExecutableElement accessorMethod;
        try {
          accessorMethod = getAccessorMethod(typeElement, variableElement);
        } catch (PropertyValidationException e) {
          error(processingEnv, e.getMessage(), e.source);
          continue;
        }

        Map<TypeName, Adapter> variableScopedTypeAdapters = getTypeAdapterMapForVariable(
            typeAdapters, variableElement, accessorMethod);

        String name = variableElement.getSimpleName().toString();

        // A field is considered "nullable" when it is a non-primitive and not annotated with @NonNull or @NotNull
        boolean isPrimitive = variableElement.asType().getKind().isPrimitive();
        boolean annotatedWithNonNull = accessorMethod != null ? AnnotationUtils.isFieldRequired(accessorMethod)
                                                              : AnnotationUtils.isFieldRequired(variableElement);
        boolean isNullable = !isPrimitive && !annotatedWithNonNull;

        // Parse the property type into a Property.Type object and find all recursive data class dependencies
        String accessorMethodName = accessorMethod == null ? null : accessorMethod.getSimpleName().toString();
        Property property = parseProperty(variableElement.asType(), typeElement.asType(), isNullable, name,
                                          accessorMethodName, variableScopedTypeAdapters);

        properties.add(property);

        requiredTypeAdapters.addAll(property.requiredTypeAdapters());

        requiresClassLoader |= property.requiresClassLoader();
      }
    }

    return new DataClass(properties, className.packageName(), wrappedClassName, className, delegateClassName,
                         requiresClassLoader, requiredTypeAdapters, isSingleton, initializationStrategy);
  }

  private boolean applyTypeAdaptersFromElement(Element element, Map<TypeName, Adapter> typeAdapters) {
    if (element != null) {
      Map<String, Object> annotation = AnnotationUtils.getAnnotation(TypeAdapters.class, element);
      if (annotation != null) {
        Object[] typeAdaptersArray = (Object[]) annotation.get("value");
        for (Object o : typeAdaptersArray) {
          DeclaredType ta = (DeclaredType) o;
          TypeElement typeElement = (TypeElement) typeUtil.asElement(ta);
          TypeName typeAdapterType = TypeUtils.getTypeAdapterType(typeUtil, ta);
          boolean singleton = isSingleton(typeUtil, typeElement);
          typeAdapters.put(typeAdapterType, new Adapter(singleton, ClassName.get(typeElement)));
        }
        return true;
      }
    }
    return false;
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

  private Map<TypeName, Adapter> getTypeAdapterMapForVariable(
      Map<TypeName, Adapter> classScopedTypeAdapters, VariableElement variableElement,
      ExecutableElement accessorMethod) {

    Map<TypeName, Adapter> tempTypeAdapters = new HashMap<>(classScopedTypeAdapters);
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
   * @param typeAdapterMap All type adapters are in scope of this property
   * @return The parsed property
   */
  private Property parseProperty(TypeMirror variable, TypeMirror dataClass, boolean isNullable, String name,
                                 @Nullable String accessorMethodName, Map<TypeName, Adapter> typeAdapterMap)
      throws UnknownPropertyTypeException {

    variable = getActualTypeParameter(variable, dataClass);

    TypeMirror erasedType = typeUtil.erasure(variable);

    TypeName parcelableTypeName = getParcelableType(typeUtil, erasedType);

    boolean isInterface = TypeUtils.isInterface(typeUtil, erasedType);

    TypeMirror type = variable;
    if (type instanceof WildcardType) {
      type = ((WildcardType) variable).getExtendsBound();
    }

    TypeName erasedTypeName = TypeName.get(erasedType);
    Adapter typeAdapter = typeAdapterMap.get(erasedTypeName);
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
      Property keyProperty = parseProperty(typeArguments.get(0), dataClass, true, name + "Key", null, typeAdapterMap);
      Property valueProperty = parseProperty(typeArguments.get(1), dataClass, true, name + "Value", null, typeAdapterMap);
      return new MapProperty(keyProperty, valueProperty, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (LIST.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Item", null, typeAdapterMap);
      return new ListProperty(typeArgument, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SET.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Item", null, typeAdapterMap);
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
      Property typeArgument = parseProperty(typeArguments.get(0), dataClass, true, name + "Value", null, typeAdapterMap);
      return new SparseArrayProperty(typeArgument, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SPARSE_BOOLEAN_ARRAY.equals(parcelableTypeName)) {
      return new SparseBooleanArrayProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (BUNDLE.equals(parcelableTypeName)) {
      return new BundleProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (PARCELABLE.equals(parcelableTypeName)) {
      return new ParcelableProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (OBJECT_ARRAY.equals(parcelableTypeName)) {
      TypeMirror componentType = ((ArrayType) type).getComponentType();
      Property componentProperty = parseProperty(componentType, dataClass, true, name + "Component", null, typeAdapterMap);
      return new ArrayProperty(componentProperty, typeName, isInterface, isNullable, name, accessorMethodName);
    } else if (CHAR_SEQUENCE.equals(parcelableTypeName)) {
      return new CharSequenceProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (IBINDER.equals(parcelableTypeName)) {
      return new IBinderProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (ENUM.equals(parcelableTypeName)) {
      return new EnumProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableTypeName)) {
      return new PersistableBundleProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SIZE.equals(parcelableTypeName)) {
      return new SizeProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (SIZEF.equals(parcelableTypeName)) {
      return new SizeFProperty(isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (TYPE_ADAPTER.equals(parcelableTypeName)) {
      return new TypeAdapterProperty(typeAdapter, isNullable, typeName, isInterface, name, accessorMethodName);
    } else if (typeName instanceof ClassName && wrappers.containsKey(typeName)) {
      return new WrapperProperty(wrappers.get(typeName), isNullable, typeName, isInterface, name, accessorMethodName);
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

  /**
   * Gets the type that allows the given type mirror to be written to a Parcel, or null if it is not parcelable
   *
   * @param types The type utilities class
   * @param typeMirror The type
   * @return The parcelable type, or null
   */
  private static TypeName getParcelableType(Types types, TypeMirror typeMirror) {
    TypeElement type = (TypeElement) types.asElement(typeMirror);

    while (typeMirror.getKind() != TypeKind.NONE) {

      // first, check if the class is valid.
      TypeName typeName = TypeName.get(typeMirror);
      if (typeName instanceof ParameterizedTypeName) {
        typeName = ((ParameterizedTypeName) typeName).rawType;
      }

      if (typeName.isPrimitive() || VALID_TYPES.contains(typeName)) {
        return typeName;
      }

      if (typeName instanceof ArrayTypeName) {
        return OBJECT_ARRAY;
      }

      // then check if it implements valid interfaces
      for (TypeMirror iface : type.getInterfaces()) {
        TypeName inherited = getParcelableType(types, iface);
        if (inherited != null) {
          return inherited;
        }
      }

      // then move on
      type = (TypeElement) types.asElement(typeMirror);
      typeMirror = type.getSuperclass();
    }

    return null;
  }

  private static void error(ProcessingEnvironment processingEnv, String message, Element element) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  static class PropertyValidationException extends IllegalStateException {
    final VariableElement source;

    PropertyValidationException(String message, VariableElement source) {
      super(message);
      this.source = source;
    }
  }

  static class UnknownPropertyTypeException extends Exception {
    UnknownPropertyTypeException(String message) {
      super(message);
    }
  }
}
