package nz.bradcampbell.paperparcel.internal;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.*;
import nz.bradcampbell.paperparcel.TypeAdapter;
import nz.bradcampbell.paperparcel.internal.properties.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.*;

import static com.squareup.javapoet.TypeName.*;
import static com.squareup.javapoet.TypeName.get;
import static javax.lang.model.element.Modifier.STATIC;

public class Utils {
  private static final String NULLABLE_ANNOTATION_NAME = "Nullable";

  private static final TypeName STRING = ClassName.get("java.lang", "String");
  private static final TypeName MAP = ClassName.get("java.util", "Map");
  private static final TypeName LIST = ClassName.get("java.util", "List");
  private static final TypeName BOOLEAN_ARRAY = ArrayTypeName.of(boolean.class);
  private static final TypeName BYTE_ARRAY = ArrayTypeName.of(byte.class);
  private static final TypeName INT_ARRAY = ArrayTypeName.of(int.class);
  private static final TypeName LONG_ARRAY = ArrayTypeName.of(long.class);
  private static final TypeName STRING_ARRAY = ArrayTypeName.of(String.class);
  private static final TypeName SPARSE_ARRAY = ClassName.get("android.util", "SparseArray");
  private static final TypeName SPARSE_BOOLEAN_ARRAY = ClassName.get("android.util", "SparseBooleanArray");
  private static final TypeName BUNDLE = ClassName.get("android.os", "Bundle");
  private static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable");
  private static final TypeName PARCELABLE_ARRAY = ArrayTypeName.of(PARCELABLE);
  private static final TypeName CHAR_SEQUENCE = ClassName.get("java.lang", "CharSequence");
  private static final TypeName CHAR_SEQUENCE_ARRAY = ArrayTypeName.of(CHAR_SEQUENCE);
  private static final TypeName IBINDER = ClassName.get("android.os", "IBinder");
  private static final TypeName OBJECT_ARRAY = ArrayTypeName.of(OBJECT);
  private static final TypeName SERIALIZABLE = ClassName.get("java.io", "Serializable");
  private static final TypeName PERSISTABLE_BUNDLE = ClassName.get("android.os", "PersistableBundle");
  private static final TypeName SIZE = ClassName.get("android.util", "Size");
  private static final TypeName SIZEF = ClassName.get("android.util", "SizeF");
  private static final TypeName ENUM = ClassName.get("java.lang", "Enum");
  private static final TypeName BOXED_INT = INT.box();
  private static final TypeName BOXED_LONG = LONG.box();
  private static final TypeName BOXED_BYTE = BYTE.box();
  private static final TypeName BOXED_BOOLEAN = BOOLEAN.box();
  private static final TypeName BOXED_FLOAT = FLOAT.box();
  private static final TypeName BOXED_CHAR = CHAR.box();
  private static final TypeName BOXED_DOUBLE = DOUBLE.box();
  private static final TypeName BOXED_SHORT = SHORT.box();
  private static final TypeName TYPE_ADAPTER = TypeName.get(TypeAdapter.class);

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, BOOLEAN_ARRAY,
      BYTE_ARRAY, INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE,
      PARCELABLE, PARCELABLE_ARRAY, CHAR_SEQUENCE, CHAR_SEQUENCE_ARRAY, IBINDER, OBJECT_ARRAY,
      SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, BOXED_INT, LONG, BOXED_LONG, BYTE,
      BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN, FLOAT, BOXED_FLOAT, CHAR, BOXED_CHAR, DOUBLE, BOXED_DOUBLE,
      SHORT, BOXED_SHORT, TYPE_ADAPTER);

  private static final Set<TypeName> REQUIRES_CLASS_LOADER = ImmutableSet.of(BUNDLE, PERSISTABLE_BUNDLE);

  private Utils() {
    // No instances.
  }

  public static Property createProperty(Property.Type propertyType, String name) {
    return createProperty(propertyType, true, name);
  }

  /**
   * Creates a new Property object
   *
   * @param propertyType The property type
   * @param isNullable True if the property can be null, false otherwise
   * @param name The name for the getter method on the data class
   * @return A new Property object
   */
  public static Property createProperty(Property.Type propertyType, boolean isNullable, String name) {

    TypeName parcelableType = propertyType.getTypeAdapter() == null ? propertyType.getParcelableTypeName() : TYPE_ADAPTER;

    if (STRING.equals(parcelableType)) {
      return new StringProperty(propertyType, isNullable, name);
    } else if (INT.equals(parcelableType)) {
      return new IntProperty(propertyType, isNullable, name);
    } else if (BOXED_INT.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (LONG.equals(parcelableType)) {
      return new LongProperty(propertyType, isNullable, name);
    } else if (BOXED_LONG.box().equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (BYTE.equals(parcelableType)) {
      return new ByteProperty(propertyType, isNullable, name);
    } else if (BOXED_BYTE.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (BOOLEAN.equals(parcelableType)) {
      return new BooleanProperty(propertyType, isNullable, name);
    } else if (BOXED_BOOLEAN.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (FLOAT.equals(parcelableType)) {
      return new FloatProperty(propertyType, isNullable, name);
    } else if (BOXED_FLOAT.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (CHAR.equals(parcelableType)) {
      return new CharProperty(propertyType, isNullable, name);
    } else if (BOXED_CHAR.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (DOUBLE.equals(parcelableType)) {
      return new DoubleProperty(propertyType, isNullable, name);
    } else if (BOXED_DOUBLE.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (SHORT.equals(parcelableType)) {
      return new ShortProperty(propertyType, isNullable, name);
    } else if (BOXED_SHORT.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (MAP.equals(parcelableType)) {
      return new MapProperty(propertyType, isNullable, name);
    } else if (LIST.equals(parcelableType)) {
      return new ListProperty(propertyType, isNullable, name);
    }  else if (BOOLEAN_ARRAY.equals(parcelableType)) {
      return new BooleanArrayProperty(propertyType, isNullable, name);
    } else if (BYTE_ARRAY.equals(parcelableType)) {
      return new ByteArrayProperty(propertyType, isNullable, name);
    } else if (INT_ARRAY.equals(parcelableType)) {
      return new IntArrayProperty(propertyType, isNullable, name);
    } else if (LONG_ARRAY.equals(parcelableType)) {
      return new LongArrayProperty(propertyType, isNullable, name);
    } else if (STRING_ARRAY.equals(parcelableType)) {
      return new StringArrayProperty(propertyType, isNullable, name);
    } else if (SPARSE_ARRAY.equals(parcelableType)) {
      return new SparseArrayProperty(propertyType, isNullable, name);
    } else if (SPARSE_BOOLEAN_ARRAY.equals(parcelableType)) {
      return new SparseBooleanArrayProperty(propertyType, isNullable, name);
    } else if (BUNDLE.equals(parcelableType)) {
      return new BundleProperty(propertyType, isNullable, name);
    } else if (PARCELABLE.equals(parcelableType)) {
      return new ParcelableProperty(propertyType, isNullable, name);
    } else if (PARCELABLE_ARRAY.equals(parcelableType)) {
      return new ArrayProperty(propertyType, isNullable, name);
    } else if (CHAR_SEQUENCE.equals(parcelableType)) {
      return new CharSequenceProperty(propertyType, isNullable, name);
    } else if (CHAR_SEQUENCE_ARRAY.equals(parcelableType)) {
      return new ValueProperty(propertyType, name);
    } else if (IBINDER.equals(parcelableType)) {
      return new IBinderProperty(propertyType, isNullable, name);
    } else if (OBJECT_ARRAY.equals(parcelableType)) {
      return new ArrayProperty(propertyType, isNullable, name);
    } else if (SERIALIZABLE.equals(parcelableType)) {
      return new SerializableProperty(propertyType, isNullable, name);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableType)) {
      return new PersistableBundleProperty(propertyType, isNullable, name);
    } else if (SIZE.equals(parcelableType)) {
      return new SizeProperty(propertyType, isNullable, name);
    } else if (SIZEF.equals(parcelableType)) {
      return new SizeFProperty(propertyType, isNullable, name);
    } else if (ENUM.equals(parcelableType)) {
      return new SerializableProperty(propertyType, isNullable, name);
    } else if (TYPE_ADAPTER.equals(parcelableType)) {
      return new TypeAdapterProperty(propertyType, isNullable, name);
    } else {
      return new NonParcelableProperty(propertyType, isNullable, name);
    }
  }

  /**
   * Gets the type that allows the given type mirror to be written to a Parcel, or null if it is not parcelable. Always
   * prioritizes Serializable last as it is inefficient.
   *
   * @param types The type utilities class
   * @param typeMirror The type
   * @return The parcelable type, or null
   */
  public static TypeName getParcelableType(Types types, TypeMirror typeMirror) {
    TypeElement type = (TypeElement) types.asElement(typeMirror);

    boolean isSerializable = false;

    while (typeMirror.getKind() != TypeKind.NONE) {

      // first, check if the class is valid.
      TypeName typeName = get(typeMirror);
      if (typeName instanceof ParameterizedTypeName) {
        typeName = ((ParameterizedTypeName) typeName).rawType;
      }

      if (typeName.isPrimitive() || VALID_TYPES.contains(typeName)) {
        return typeName;
      }

      if (typeName instanceof ArrayTypeName) {
        TypeName arrayParcelableType = getParcelableType(types, ((ArrayType) typeMirror).getComponentType());
        if (arrayParcelableType == null || PARCELABLE.equals(arrayParcelableType)) {
          return PARCELABLE_ARRAY;
        }
        return OBJECT_ARRAY;
      }

      // then check if it implements valid interfaces
      for (TypeMirror iface : type.getInterfaces()) {
        TypeName ifaceName = get(iface);

        if (ifaceName instanceof ParameterizedTypeName) {
          ifaceName = ((ParameterizedTypeName) ifaceName).rawType;
        }

        if (SERIALIZABLE.equals(ifaceName)) {
          isSerializable = true;
        } else if (VALID_TYPES.contains(ifaceName)) {
          return ifaceName;
        }
      }

      // then move on
      type = (TypeElement) types.asElement(typeMirror);
      typeMirror = type.getSuperclass();
    }

    // Serializable should be a last resort as it is slow
    if (isSerializable) {
      return SERIALIZABLE;
    }

    return null;
  }

  public static boolean hasTypeArguments(TypeMirror type) {
    if (type instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) type;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() > 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean requiresClassLoader(TypeName parcelableTypeName) {
    return REQUIRES_CLASS_LOADER.contains(parcelableTypeName);
  }

  public static CodeBlock literal(String literal, Object... args) {
    CodeBlock code = CodeBlock.builder().add(literal, args).build();

    // Validation
    return CodeBlock.builder().add("$L", code).build();
  }

  public static TypeName getRawTypeName(Property.Type type, boolean wrapped) {
    TypeName typeName = wrapped ? type.getWrappedTypeName() : type.getTypeName();
    while (typeName instanceof ParameterizedTypeName) {
      typeName = ((ParameterizedTypeName) typeName).rawType;
    }
    return typeName;
  }

  public static String capitalizeFirstCharacter(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  /** Returns a string for the raw type of {@code type}. Primitive types are always boxed. */
  public static String rawTypeToString(TypeMirror type, char innerClassSeparator) {
    if (!(type instanceof DeclaredType)) {
      throw new IllegalArgumentException("Unexpected type: " + type);
    }
    StringBuilder result = new StringBuilder();
    DeclaredType declaredType = (DeclaredType) type;
    rawTypeToString(result, (TypeElement) declaredType.asElement(), innerClassSeparator);
    return result.toString();
  }

  public static void rawTypeToString(StringBuilder result, TypeElement type,
                                      char innerClassSeparator) {
    String packageName = getPackage(type).getQualifiedName().toString();
    String qualifiedName = type.getQualifiedName().toString();
    if (packageName.isEmpty()) {
      result.append(qualifiedName.replace('.', innerClassSeparator));
    } else {
      result.append(packageName);
      result.append('.');
      result.append(
          qualifiedName.substring(packageName.length() + 1).replace('.', innerClassSeparator));
    }
  }

  public static PackageElement getPackage(Element type) {
    while (type.getKind() != ElementKind.PACKAGE) {
      type = type.getEnclosingElement();
    }
    return (PackageElement) type;
  }

  /**
   * Print an error message to the console so that the user can see something went wrong.
   *
   * @param message The message the user will see
   * @param element The element to use as a position hint
   */
  public static void error(ProcessingEnvironment processingEnv, String message, Element element) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  public static String getPackageName(TypeElement type) {
    return getPackage(type).getQualifiedName().toString();
  }

  public static boolean hasAnnotationWithName(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isFieldRequired(Element element) {
    return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
  }

  /**
   * Gets a list of all non-static member variables of a TypeElement
   *
   * @param el The data class
   * @return A list of non-static member variables. Cannot be null.
   */
  public static List<VariableElement> getFields(TypeElement el) {
    List<? extends Element> enclosedElements = el.getEnclosedElements();
    List<VariableElement> variables = new ArrayList<>();
    for (Element e : enclosedElements) {
      if (e instanceof VariableElement && !e.getModifiers().contains(STATIC)) {
        variables.add((VariableElement) e);
      }
    }
    return variables;
  }

  /**
   * Appends a string for {@code type} to {@code result}. Primitive types are
   * always boxed.
   *
   * @param innerClassSeparator either '.' or '$', which will appear in a
   *     class name like "java.lang.Map.Entry" or "java.lang.Map$Entry".
   *     Use '.' for references to existing types in code. Use '$' to define new
   *     class names and for strings that will be used by runtime reflection.
   */
  public static void typeToString(final TypeMirror type, final StringBuilder result,
                                  final char innerClassSeparator) {
    type.accept(new SimpleTypeVisitor6<Void, Void>() {
      @Override public Void visitDeclared(DeclaredType declaredType, Void v) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        rawTypeToString(result, typeElement, innerClassSeparator);
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (!typeArguments.isEmpty()) {
          result.append("<");
          for (int i = 0; i < typeArguments.size(); i++) {
            if (i != 0) {
              result.append(", ");
            }
            typeToString(typeArguments.get(i), result, innerClassSeparator);
          }
          result.append(">");
        }
        return null;
      }
      @Override public Void visitPrimitive(PrimitiveType primitiveType, Void v) {
        result.append(box((PrimitiveType) type));
        return null;
      }
      @Override public Void visitArray(ArrayType arrayType, Void v) {
        TypeMirror type = arrayType.getComponentType();
        if (type instanceof PrimitiveType) {
          result.append(type.toString()); // Don't box, since this is an array.
        } else {
          typeToString(arrayType.getComponentType(), result, innerClassSeparator);
        }
        result.append("[]");
        return null;
      }
      @Override public Void visitTypeVariable(TypeVariable typeVariable, Void v) {
        result.append(typeVariable.asElement().getSimpleName());
        return null;
      }
      @Override public Void visitError(ErrorType errorType, Void v) {
        // Error type found, a type may not yet have been generated, but we need the type
        // so we can generate the correct code in anticipation of the type being available
        // to the compiler.

        // Paramterized types which don't exist are returned as an error type whose name is "<any>"
        if ("<any>".equals(errorType.toString())) {
          throw new RuntimeException(
              "Type reported as <any> is likely a not-yet generated parameterized type.");
        }
        result.append(errorType.toString());
        return null;
      }
      @Override protected Void defaultAction(TypeMirror typeMirror, Void v) {
        throw new UnsupportedOperationException(
            "Unexpected TypeKind " + typeMirror.getKind() + " for "  + typeMirror);
      }
    }, null);
  }

  static TypeName box(PrimitiveType primitiveType) {
    switch (primitiveType.getKind()) {
      case BYTE:
        return ClassName.get(Byte.class);
      case SHORT:
        return ClassName.get(Short.class);
      case INT:
        return ClassName.get(Integer.class);
      case LONG:
        return ClassName.get(Long.class);
      case FLOAT:
        return ClassName.get(Float.class);
      case DOUBLE:
        return ClassName.get(Double.class);
      case BOOLEAN:
        return ClassName.get(Boolean.class);
      case CHAR:
        return ClassName.get(Character.class);
      case VOID:
        return ClassName.get(Void.class);
      default:
        throw new AssertionError();
    }
  }

  private static final AnnotationValueVisitor<Object, Void> VALUE_EXTRACTOR =
      new SimpleAnnotationValueVisitor6<Object, Void>() {
        @Override public Object visitString(String s, Void p) {
          if ("<error>".equals(s)) {
            throw new RuntimeException("Unknown type returned as <error>.");
          } else if ("<any>".equals(s)) {
            throw new RuntimeException("Unknown type returned as <any>.");
          }
          return s;
        }
        @Override public Object visitType(TypeMirror t, Void p) {
          return t;
        }
        @Override protected Object defaultAction(Object o, Void v) {
          return o;
        }
        @Override public Object visitArray(List<? extends AnnotationValue> values, Void v) {
          Object[] result = new Object[values.size()];
          for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).accept(this, null);
          }
          return result;
        }
      };

  /**
   * Returns the annotation on {@code element} formatted as a Map. This returns
   * a Map rather than an instance of the annotation interface to work-around
   * the fact that Class and Class[] fields won't work at code generation time.
   * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5089128
   */
  public static Map<String, Object> getAnnotation(Class<?> annotationType, Element element) {
    for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
      if (!rawTypeToString(annotation.getAnnotationType(), '$')
          .equals(annotationType.getName())) {
        continue;
      }

      Map<String, Object> result = new LinkedHashMap<>();
      for (Method m : annotationType.getMethods()) {
        result.put(m.getName(), m.getDefaultValue());
      }
      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e
          : annotation.getElementValues().entrySet()) {
        String name = e.getKey().getSimpleName().toString();
        Object value = e.getValue().accept(VALUE_EXTRACTOR, null);
        Object defaultValue = result.get(name);
        if (!lenientIsInstance(defaultValue.getClass(), value)) {
          throw new IllegalStateException(String.format(
              "Value of %s.%s is a %s but expected a %s\n    value: %s",
              annotationType, name, value.getClass().getName(), defaultValue.getClass().getName(),
              value instanceof Object[] ? Arrays.toString((Object[]) value) : value));
        }
        result.put(name, value);
      }
      return result;
    }
    return null; // Annotation not found.
  }

  /**
   * Returns true if {@code value} can be assigned to {@code expectedClass}.
   * Like {@link Class#isInstance} but more lenient for {@code Class<?>} values.
   */
  private static boolean lenientIsInstance(Class<?> expectedClass, Object value) {
    if (expectedClass.isArray()) {
      Class<?> componentType = expectedClass.getComponentType();
      if (!(value instanceof Object[])) {
        return false;
      }
      for (Object element : (Object[]) value) {
        if (!lenientIsInstance(componentType, element)) return false;
      }
      return true;
    } else if (expectedClass == Class.class) {
      return value instanceof TypeMirror;
    } else {
      return expectedClass == value.getClass();
    }
  }

  public static TypeName getTypeAdapterType(Types typeUtil, DeclaredType typeAdapter) {
    List<? extends TypeMirror> interfaces = ((TypeElement)typeUtil.asElement(typeAdapter)).getInterfaces();
    for (TypeMirror intf : interfaces) {
      TypeName typeName = TypeName.get(intf);
      if (typeName instanceof ParameterizedTypeName) {
        ParameterizedTypeName paramTypeName = (ParameterizedTypeName) typeName;
        if (paramTypeName.rawType.equals(TYPE_ADAPTER)) {
          return paramTypeName.typeArguments.get(0);
        }
      }
    }
    return null;
  }
}
