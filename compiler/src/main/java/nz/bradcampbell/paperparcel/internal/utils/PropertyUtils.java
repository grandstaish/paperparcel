package nz.bradcampbell.paperparcel.internal.utils;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.OBJECT;
import static com.squareup.javapoet.TypeName.SHORT;
import static com.squareup.javapoet.TypeName.get;

import com.google.common.collect.ImmutableSet;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.TypeAdapter;
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
import nz.bradcampbell.paperparcel.internal.properties.NonParcelableProperty;
import nz.bradcampbell.paperparcel.internal.properties.ParcelableProperty;
import nz.bradcampbell.paperparcel.internal.properties.PersistableBundleProperty;
import nz.bradcampbell.paperparcel.internal.properties.SerializableProperty;
import nz.bradcampbell.paperparcel.internal.properties.ShortProperty;
import nz.bradcampbell.paperparcel.internal.properties.SizeFProperty;
import nz.bradcampbell.paperparcel.internal.properties.SizeProperty;
import nz.bradcampbell.paperparcel.internal.properties.SparseArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.SparseBooleanArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.StringArrayProperty;
import nz.bradcampbell.paperparcel.internal.properties.StringProperty;
import nz.bradcampbell.paperparcel.internal.properties.TypeAdapterProperty;

import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class PropertyUtils {

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

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, BOOLEAN_ARRAY, BYTE_ARRAY,
      INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE, PARCELABLE, PARCELABLE_ARRAY,
      CHAR_SEQUENCE, IBINDER, OBJECT_ARRAY, SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, BOXED_INT,
      LONG, BOXED_LONG, BYTE, BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN, FLOAT, BOXED_FLOAT, CHAR, BOXED_CHAR, DOUBLE,
      BOXED_DOUBLE, SHORT, BOXED_SHORT, TYPE_ADAPTER);

  private static final Set<TypeName> REQUIRES_CLASS_LOADER = ImmutableSet.of(BUNDLE, PERSISTABLE_BUNDLE);

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
    } else if (INT.equals(parcelableType) || BOXED_INT.equals(parcelableType)) {
      return new IntProperty(propertyType, isNullable, name);
    } else if (LONG.equals(parcelableType) || BOXED_LONG.box().equals(parcelableType)) {
      return new LongProperty(propertyType, isNullable, name);
    } else if (BYTE.equals(parcelableType) || BOXED_BYTE.equals(parcelableType)) {
      return new ByteProperty(propertyType, isNullable, name);
    } else if (BOOLEAN.equals(parcelableType) || BOXED_BOOLEAN.equals(parcelableType)) {
      return new BooleanProperty(propertyType, isNullable, name);
    } else if (FLOAT.equals(parcelableType) || BOXED_FLOAT.equals(parcelableType)) {
      return new FloatProperty(propertyType, isNullable, name);
    } else if (CHAR.equals(parcelableType) || BOXED_CHAR.equals(parcelableType)) {
      return new CharProperty(propertyType, isNullable, name);
    } else if (DOUBLE.equals(parcelableType) || BOXED_DOUBLE.equals(parcelableType)) {
      return new DoubleProperty(propertyType, isNullable, name);
    } else if (SHORT.equals(parcelableType) || BOXED_SHORT.equals(parcelableType)) {
      return new ShortProperty(propertyType, isNullable, name);
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

  public static TypeName getTypeAdapterType(Types typeUtil, DeclaredType typeAdapter) {
    List<? extends TypeMirror> interfaces = ((TypeElement)typeUtil.asElement(typeAdapter)).getInterfaces();
    for (TypeMirror intf : interfaces) {
      TypeName typeName = get(intf);
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
