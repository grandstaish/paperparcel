package nz.bradcampbell.dataparcel.internal;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.properties.*;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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

  public static Property createProperty(Types types, boolean isNullable, String name, VariableElement variableElement) {
    TypeName type = getParcelableType(types, variableElement.asType());
    type = type == null ? ClassName.get(variableElement.asType()) : type;

    if (STRING.equals(type)) {
      return new StringProperty(isNullable, name, variableElement);
    } else if (MAP.equals(type)) {

    } else if (LIST.equals(type)) {
      return new ListProperty(types, isNullable, name, variableElement);
    }  else if (BOOLEAN_ARRAY.equals(type)) {

    } else if (BYTE_ARRAY.equals(type)) {

    } else if (INT_ARRAY.equals(type)) {

    } else if (LONG_ARRAY.equals(type)) {

    } else if (STRING_ARRAY.equals(type)) {

    } else if (SPARSE_ARRAY.equals(type)) {

    } else if (SPARSE_BOOLEAN_ARRAY.equals(type)) {

    } else if (BUNDLE.equals(type)) {

    } else if (PARCELABLE.equals(type)) {

    } else if (PARCELABLE_ARRAY.equals(type)) {

    } else if (CHAR_SEQUENCE.equals(type)) {

    } else if (CHAR_SEQUENCE_ARRAY.equals(type)) {

    } else if (IBINDER.equals(type)) {

    } else if (OBJECT_ARRAY.equals(type)) {

    } else if (SERIALIZABLE.equals(type)) {

    } else if (PERSISTABLE_BUNDLE.equals(type)) {

    } else if (SIZE.equals(type)) {

    } else if (SIZEF.equals(type)) {

    } else if (ENUM.equals(type)) {
      return new EnumProperty(isNullable, name, variableElement);
    } else if (INT.equals(type)) {
      return new IntProperty(isNullable, name, variableElement);
    } else if (INT.box().equals(type)) {
      return new IntProperty(isNullable, name, variableElement);
    } else if (LONG.equals(type)) {
      return new LongProperty(isNullable, name, variableElement);
    } else if (LONG.box().equals(type)) {
      return new LongProperty(isNullable, name, variableElement);
    } else if (BYTE.equals(type)) {

    } else if (BYTE.box().equals(type)) {

    } else if (BOOLEAN.equals(type)) {
      return new BooleanProperty(isNullable, name, variableElement);
    } else if (BOOLEAN.box().equals(type)) {
      return new BooleanProperty(isNullable, name, variableElement);
    } else if (FLOAT.equals(type)) {

    } else if (FLOAT.box().equals(type)) {

    } else if (CHAR.equals(type)) {
      return new CharProperty(isNullable, name, variableElement);
    } else if (CHAR.box().equals(type)) {
      return new CharProperty(isNullable, name, variableElement);
    } else if (DOUBLE.equals(type)) {

    } else if (DOUBLE.box().equals(type)) {

    } else if (SHORT.equals(type)) {

    } else if (SHORT.box().equals(type)) {

    } else {
      return new NonParcelableProperty(types, isNullable, name, variableElement);
    }

    throw new RuntimeException("Unknown type: " + type);
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

  public static boolean isValidType(TypeName type) {
    return VALID_TYPES.contains(type);
  }
}
