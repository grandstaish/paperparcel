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

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, BOOLEAN_ARRAY,
      BYTE_ARRAY, INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE,
      PARCELABLE, PARCELABLE_ARRAY, CHAR_SEQUENCE, CHAR_SEQUENCE_ARRAY, IBINDER, OBJECT_ARRAY,
      SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, INT.box(), LONG, LONG.box(), BYTE,
      BYTE.box(), BOOLEAN, BOOLEAN.box(), FLOAT, FLOAT.box(), CHAR, CHAR.box(), DOUBLE, DOUBLE.box(),
      SHORT, SHORT.box());

  private static Types sTypeUtil;

  public static void init(Types typeUtil) {
    sTypeUtil = typeUtil;
  }

  public static Property createProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {

    TypeName parcelableType = getParcelableType(sTypeUtil, typeMirror);
    parcelableType = parcelableType == null ? ClassName.get(typeMirror) : parcelableType;

    if (STRING.equals(parcelableType)) {
      return new StringProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (INT.equals(parcelableType)) {
      return new IntProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (INT.box().equals(parcelableType)) {
      return new IntProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (LONG.equals(parcelableType)) {
      return new LongProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (LONG.box().equals(parcelableType)) {
      return new LongProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BYTE.equals(parcelableType)) {
      return new ByteProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BYTE.box().equals(parcelableType)) {
      return new ByteProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BOOLEAN.equals(parcelableType)) {
      return new BooleanProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BOOLEAN.box().equals(parcelableType)) {
      return new BooleanProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (FLOAT.equals(parcelableType)) {
      return new FloatProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (FLOAT.box().equals(parcelableType)) {
      return new FloatProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (CHAR.equals(parcelableType)) {
      return new CharProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (CHAR.box().equals(parcelableType)) {
      return new CharProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (DOUBLE.equals(parcelableType)) {
      return new DoubleProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (DOUBLE.box().equals(parcelableType)) {
      return new DoubleProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SHORT.equals(parcelableType)) {
      return new ShortProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SHORT.box().equals(parcelableType)) {
      return new ShortProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (MAP.equals(parcelableType)) {
      return new MapProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (LIST.equals(parcelableType)) {
      return new ListProperty(typeMirror, isNullable, name, parcelableTypeName);
    }  else if (BOOLEAN_ARRAY.equals(parcelableType)) {
      return new BooleanArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BYTE_ARRAY.equals(parcelableType)) {
      return new ByteArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (INT_ARRAY.equals(parcelableType)) {
      return new IntArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (LONG_ARRAY.equals(parcelableType)) {
      return new LongArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (STRING_ARRAY.equals(parcelableType)) {
      return new StringArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SPARSE_ARRAY.equals(parcelableType)) {
      return new SparseArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SPARSE_BOOLEAN_ARRAY.equals(parcelableType)) {
      return new SparseBooleanArray(typeMirror, isNullable, name, parcelableTypeName);
    } else if (BUNDLE.equals(parcelableType)) {
      return new BundleProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (PARCELABLE.equals(parcelableType)) {
      return new ParcelableProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (PARCELABLE_ARRAY.equals(parcelableType)) {
      return new ParcelableArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (CHAR_SEQUENCE.equals(parcelableType)) {
      return new CharSequenceProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (CHAR_SEQUENCE_ARRAY.equals(parcelableType)) {
      return new CharSequenceArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (IBINDER.equals(parcelableType)) {
      return new IBinderProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (OBJECT_ARRAY.equals(parcelableType)) {
      return new ObjectArrayProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SERIALIZABLE.equals(parcelableType)) {
      return new SerializableProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableType)) {
      return new PersistableBundleProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SIZE.equals(parcelableType)) {
      return new SizeProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (SIZEF.equals(parcelableType)) {
      return new SizeFProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else if (ENUM.equals(parcelableType)) {
      return new EnumProperty(typeMirror, isNullable, name, parcelableTypeName);
    } else {
      return new NonParcelableProperty(typeMirror, isNullable, name, parcelableTypeName);
    }
  }

  private static TypeName getParcelableType(Types types, TypeMirror typeMirror) {
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

  public static boolean isValidType(Types types, TypeMirror typeMirror) {
    return getParcelableType(types, typeMirror) != null;
  }
}
