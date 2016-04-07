package nz.bradcampbell.paperparcel.utils;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.OBJECT;
import static com.squareup.javapoet.TypeName.SHORT;

import com.google.common.collect.ImmutableSet;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.TypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class PropertyUtils {

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
  public static final TypeName SERIALIZABLE = ClassName.get("java.io", "Serializable");
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

  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, SET, BOOLEAN_ARRAY, BYTE_ARRAY,
      INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE, PARCELABLE, PARCELABLE_ARRAY,
      CHAR_SEQUENCE, IBINDER, OBJECT_ARRAY, SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, BOXED_INT,
      LONG, BOXED_LONG, BYTE, BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN, FLOAT, BOXED_FLOAT, CHAR, BOXED_CHAR, DOUBLE,
      BOXED_DOUBLE, SHORT, BOXED_SHORT, TYPE_ADAPTER);

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
      List<TypeMirror> allInterfaces = new ArrayList<>();
      findAllInterfaceTypeMirrors(types, type, allInterfaces);
      for (TypeMirror iface : allInterfaces) {
        TypeName ifaceName = TypeName.get(iface);

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

  private static void findAllInterfaceTypeMirrors(Types types, TypeElement typeElement, List<TypeMirror> outInterfaces) {
    for (TypeMirror iface : typeElement.getInterfaces()) {
      TypeElement interfaceElement = (TypeElement) types.asElement(iface);
      findAllInterfaceTypeMirrors(types, interfaceElement, outInterfaces);
      outInterfaces.add(iface);
    }
  }

  public static CodeBlock literal(String literal, Object... args) {
    CodeBlock code = CodeBlock.builder().add(literal, args).build();

    // Validation
    return CodeBlock.builder().add("$L", code).build();
  }

  public static TypeName getRawTypeName(TypeName typeName) {
    while (typeName instanceof ParameterizedTypeName) {
      typeName = ((ParameterizedTypeName) typeName).rawType;
    }
    return typeName;
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
