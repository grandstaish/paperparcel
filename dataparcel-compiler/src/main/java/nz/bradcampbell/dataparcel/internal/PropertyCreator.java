package nz.bradcampbell.dataparcel.internal;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.properties.*;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.util.Set;

import static com.squareup.javapoet.TypeName.*;
import static com.squareup.javapoet.TypeName.SHORT;

public class PropertyCreator {
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

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, BOOLEAN_ARRAY,
      BYTE_ARRAY, INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE,
      PARCELABLE, PARCELABLE_ARRAY, CHAR_SEQUENCE, CHAR_SEQUENCE_ARRAY, IBINDER, OBJECT_ARRAY,
      SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, BOXED_INT, LONG, BOXED_LONG, BYTE,
      BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN, FLOAT, BOXED_FLOAT, CHAR, BOXED_CHAR, DOUBLE, BOXED_DOUBLE,
      SHORT, BOXED_SHORT);

  public static Property createProperty(Property.Type propertyType, boolean isNullable, String name) {

    TypeName parcelableType = propertyType.getParcelableTypeName();

    if (STRING.equals(parcelableType)) {
      return new StringProperty(propertyType, isNullable, name);
    } else if (INT.equals(parcelableType)) {
      return new IntProperty(propertyType, isNullable, name);
    } else if (BOXED_INT.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (LONG.equals(parcelableType)) {
      return new LongProperty(propertyType, isNullable, name);
    } else if (BOXED_LONG.box().equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (BYTE.equals(parcelableType)) {
      return new ByteProperty(propertyType, isNullable, name);
    } else if (BOXED_BYTE.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (BOOLEAN.equals(parcelableType)) {
      return new BooleanProperty(propertyType, isNullable, name);
    } else if (BOXED_BOOLEAN.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (FLOAT.equals(parcelableType)) {
      return new FloatProperty(propertyType, isNullable, name);
    } else if (BOXED_FLOAT.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (CHAR.equals(parcelableType)) {
      return new CharProperty(propertyType, isNullable, name);
    } else if (BOXED_CHAR.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (DOUBLE.equals(parcelableType)) {
      return new DoubleProperty(propertyType, isNullable, name);
    } else if (BOXED_DOUBLE.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
    } else if (SHORT.equals(parcelableType)) {
      return new ShortProperty(propertyType, isNullable, name);
    } else if (BOXED_SHORT.equals(parcelableType)) {
      return new ValueProperty(propertyType, isNullable, name);
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
      return new SparseBooleanArray(propertyType, isNullable, name);
    } else if (BUNDLE.equals(parcelableType)) {
      return new BundleProperty(propertyType, isNullable, name);
    } else if (PARCELABLE.equals(parcelableType)) {
      return new ParcelableProperty(propertyType, isNullable, name);
    } else if (PARCELABLE_ARRAY.equals(parcelableType)) {
      return new ParcelableArrayProperty(propertyType, isNullable, name);
    } else if (CHAR_SEQUENCE.equals(parcelableType)) {
      return new CharSequenceProperty(propertyType, isNullable, name);
    } else if (CHAR_SEQUENCE_ARRAY.equals(parcelableType)) {
      return new CharSequenceArrayProperty(propertyType, isNullable, name);
    } else if (IBINDER.equals(parcelableType)) {
      return new IBinderProperty(propertyType, isNullable, name);
    } else if (OBJECT_ARRAY.equals(parcelableType)) {
      return new ObjectArrayProperty(propertyType, isNullable, name);
    } else if (SERIALIZABLE.equals(parcelableType)) {
      return new SerializableProperty(propertyType, isNullable, name);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableType)) {
      return new PersistableBundleProperty(propertyType, isNullable, name);
    } else if (SIZE.equals(parcelableType)) {
      return new SizeProperty(propertyType, isNullable, name);
    } else if (SIZEF.equals(parcelableType)) {
      return new SizeFProperty(propertyType, isNullable, name);
    } else if (ENUM.equals(parcelableType)) {
      return new EnumProperty(propertyType, isNullable, name);
    } else {
      return new NonParcelableProperty(propertyType, isNullable, name);
    }
  }

  public static TypeName getParcelableType(Types types, TypeMirror typeMirror) {
    TypeElement type = (TypeElement) types.asElement(typeMirror);
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
        // TODO: handle non-parcelable arrays
        return OBJECT_ARRAY;
      }

      // then check if it implements valid interfaces
      for (TypeMirror iface : type.getInterfaces()) {
        TypeName ifaceName = get(iface);
        if (VALID_TYPES.contains(ifaceName)) {
          return ifaceName;
        }
      }

      // then move on
      type = (TypeElement) types.asElement(typeMirror);
      typeMirror = type.getSuperclass();
    }
    return null;
  }
}
