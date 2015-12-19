package nz.bradcampbell.dataparcel;

public final class ParcelPropertyHelper {
//  private static final TypeName STRING = ClassName.get("java.lang", "String");
//  private static final TypeName MAP = ClassName.get("java.util", "Map");
//  private static final TypeName LIST = ClassName.get("java.util", "List");
//  private static final TypeName BOOLEAN_ARRAY = ArrayTypeName.of(boolean.class);
//  private static final TypeName BYTE_ARRAY = ArrayTypeName.of(byte.class);
//  private static final TypeName INT_ARRAY = ArrayTypeName.of(int.class);
//  private static final TypeName LONG_ARRAY = ArrayTypeName.of(long.class);
//  private static final TypeName STRING_ARRAY = ArrayTypeName.of(String.class);
//  private static final TypeName SPARSE_ARRAY = ClassName.get("android.util", "SparseArray");
//  private static final TypeName SPARSE_BOOLEAN_ARRAY = ClassName.get("android.util", "SparseBooleanArray");
//  private static final TypeName BUNDLE = ClassName.get("android.os", "Bundle");
//  private static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable");
//  private static final TypeName PARCELABLE_ARRAY = ArrayTypeName.of(PARCELABLE);
//  private static final TypeName CHAR_SEQUENCE = ClassName.get("java.lang", "CharSequence");
//  private static final TypeName CHAR_SEQUENCE_ARRAY = ArrayTypeName.of(CHAR_SEQUENCE);
//  private static final TypeName IBINDER = ClassName.get("android.os", "IBinder");
//  private static final TypeName OBJECT_ARRAY = ArrayTypeName.of(OBJECT);
//  private static final TypeName SERIALIZABLE = ClassName.get("java.io", "Serializable");
//  private static final TypeName PERSISTABLE_BUNDLE = ClassName.get("android.os", "PersistableBundle");
//  private static final TypeName SIZE = ClassName.get("android.util", "Size");
//  private static final TypeName SIZEF = ClassName.get("android.util", "SizeF");
//  private static final TypeName ENUM = ClassName.get("java.lang", "Enum");
//
//  private static final Set<TypeName> VALID_TYPES = ImmutableSet.of(STRING, MAP, LIST, BOOLEAN_ARRAY,
//      BYTE_ARRAY, INT_ARRAY, LONG_ARRAY, STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE,
//      PARCELABLE, PARCELABLE_ARRAY, CHAR_SEQUENCE, CHAR_SEQUENCE_ARRAY, IBINDER, OBJECT_ARRAY,
//      SERIALIZABLE, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT, INT.box(), LONG, LONG.box(), BYTE,
//      BYTE.box(), BOOLEAN, BOOLEAN.box(), FLOAT, FLOAT.box(), CHAR, CHAR.box(), DOUBLE, DOUBLE.box(),
//      SHORT, SHORT.box());
//
//  public static boolean isValidType(TypeName type) {
//    return VALID_TYPES.contains(type);
//  }
//
//  public static CodeBlock readFromParcel(TypeMirror typeMirror, ParameterSpec in, int position, TypeElement element, boolean isNullable, Types types) {
//    TypeName type = getParcelableType(types, typeMirror);
//    if (type == null) return null;
//
//    List<Type> typeArgs = ((Type.ClassType) typeMirror).getTypeArguments();
//    if (typeArgs != null) {
//      for (Type t : typeArgs) {
//        if (!VALID_TYPES.contains(ClassName.get(t))) {
//          return null;
//        }
//      }
//    }
//
//    TypeName cast = ClassName.get(typeMirror);
//    CodeBlock.Builder block = CodeBlock.builder();
//    String fieldName = "component" + position;
//
//    block.add("$T $N = ", cast, fieldName);
//
//    if (isNullable){
//      block.add("$N.readInt() == 0 ? ", in);
//    }
//
//    if (type.equals(STRING))
//      block.add("$N.readString()", in);
//    else if (type.equals(BYTE) || type.equals(BYTE.box()))
//      block.add("$N.readByte()", in);
//    else if (type.equals(INT) || type.equals(INT.box()))
//      block.add("$N.readInt()", in);
//    else if (type.equals(SHORT) || type.equals(SHORT.box()))
//      block.add("($T) $N.readInt()", cast, in);
//    else if (type.equals(LONG) || type.equals(LONG.box()))
//      block.add("$N.readLong()", in);
//    else if (type.equals(FLOAT) || type.equals(FLOAT.box()))
//      block.add("$N.readFloat()", in);
//    else if (type.equals(DOUBLE) || type.equals(DOUBLE.box()))
//      block.add("$N.readDouble()", in);
//    else if (type.equals(BOOLEAN) || type.equals(BOOLEAN.box()))
//      block.add("$N.readInt() == 1", in);
//    else if (type.equals(CHAR) || type.equals(CHAR.box()))
//      block.add("($T) $N.readInt()", cast, in);
//    else if (type.equals(PARCELABLE))
//      block.add("($T) $N.readParcelable($T.class.getClassLoader())", cast, in, element);
//    else if (type.equals(CHAR_SEQUENCE))
//      block.add("($T) $N.readCharSequence()", cast, in);
//    else if (type.equals(MAP))
//      block.add("($T) $N.readHashMap($T.class.getClassLoader())", cast, in, element);
//    else if (type.equals(LIST))
//      block.add("($T) $N.readArrayList($T.class.getClassLoader())", cast, in, element);
//    else if (type.equals(BOOLEAN_ARRAY))
//      block.add("$N.createBooleanArray()", in);
//    else if (type.equals(BYTE_ARRAY))
//      block.add("$N.createByteArray()", in);
//    else if (type.equals(STRING_ARRAY))
//      block.add("$N.readStringArray()", in);
//    else if (type.equals(CHAR_SEQUENCE_ARRAY))
//      block.add("$N.readCharSequenceArray()", in);
//    else if (type.equals(IBINDER))
//      block.add("($T) $N.readStrongBinder()", cast, in);
//    else if (type.equals(OBJECT_ARRAY))
//      block.add("$N.readArray($T.class.getClassLoader())", in, element);
//    else if (type.equals(INT_ARRAY))
//      block.add("$N.createIntArray()", in);
//    else if (type.equals(LONG_ARRAY))
//      block.add("$N.createLongArray()", in);
//    else if (type.equals(SERIALIZABLE) || type.equals(ENUM))
//      block.add("($T) $N.readSerializable()", cast, in);
//    else if (type.equals(PARCELABLE_ARRAY))
//      block.add("($T) $N.readParcelableArray($T.class.getClassLoader())", cast, in, element);
//    else if (type.equals(SPARSE_ARRAY))
//      block.add("$N.readSparseArray($T.class.getClassLoader())", in, element);
//    else if (type.equals(SPARSE_BOOLEAN_ARRAY))
//      block.add("$N.readSparseBooleanArray()", in);
//    else if (type.equals(BUNDLE))
//      block.add("$N.readBundle($T.class.getClassLoader())", in, element);
//    else if (type.equals(PERSISTABLE_BUNDLE))
//      block.add("$N.readPersistableBundle($T.class.getClassLoader())", in, element);
//    else if (type.equals(SIZE))
//      block.add("$N.readSize()", in);
//    else if (type.equals(SIZEF))
//      block.add("$N.readSizeF()", in);
//
//    if (isNullable){
//      block.add(" : null");
//    }
//
//    block.add(";\n");
//
//    return block.build();
//  }
//
//  public static CodeBlock writeToParcel(TypeMirror typeMirror, ParameterSpec dest, String fieldName, int position, boolean isNullable, Types types) {
//    TypeName type = getParcelableType(types, typeMirror);
//    if (type == null) return null;
//
////    List<? extends TypeMirror> typeArgs = ((DeclaredType) typeMirror).getTypeArguments();
////    if (typeArgs != null) {
////      for (Type t : typeArgs) {
////        if (!VALID_TYPES.contains(ClassName.get(t))) {
////          return null;
////        }
////      }
////    }
//
//    CodeBlock.Builder block = CodeBlock.builder();
//    String methodName = "component" + position;
//
//    if (isNullable) {
//      block.beginControlFlow("if ($N.$N() == null)", fieldName, methodName);
//      block.addStatement("$N.writeInt(1)", dest);
//      block.nextControlFlow("else");
//      block.addStatement("$N.writeInt(0)", dest);
//    }
//
//    if (type.equals(STRING))
//      block.add("$N.writeString($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(BYTE) || type.equals(BYTE.box()))
//      block.add("$N.writeInt($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(INT) || type.equals(INT.box()))
//      block.add("$N.writeInt($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SHORT) || type.equals(SHORT.box()))
//      block.add("$N.writeInt(((Short) $N.$N()).intValue())", dest, fieldName, methodName);
//    else if (type.equals(LONG) || type.equals(LONG.box()))
//      block.add("$N.writeLong($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(FLOAT) || type.equals(FLOAT.box()))
//      block.add("$N.writeFloat($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(DOUBLE) || type.equals(DOUBLE.box()))
//      block.add("$N.writeDouble($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(BOOLEAN) || type.equals(BOOLEAN.box()))
//      block.add("$N.writeInt($N.$N() ? 1 : 0)", dest, fieldName, methodName);
//    else if (type.equals(CHAR) || type.equals(CHAR.box()))
//      block.add("$N.writeInt($N.$N()", dest, fieldName, methodName);
//    else if (type.equals(PARCELABLE))
//      block.add("$N.writeParcelable($N.$N(), 0)", dest, fieldName, methodName);
//    else if (type.equals(CHAR_SEQUENCE))
//      block.add("$N.writeCharSequence($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(MAP))
//      block.add("$N.writeMap($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(LIST))
//      block.add("$N.writeList($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(BOOLEAN_ARRAY))
//      block.add("$N.writeBooleanArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(BYTE_ARRAY))
//      block.add("$N.writeByteArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(STRING_ARRAY))
//      block.add("$N.writeStringArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(CHAR_SEQUENCE_ARRAY))
//      block.add("$N.writeCharSequenceArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(IBINDER))
//      block.add("$N.writeStrongBinder($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(OBJECT_ARRAY))
//      block.add("$N.writeArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(INT_ARRAY))
//      block.add("$N.writeIntArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(LONG_ARRAY))
//      block.add("$N.writeLongArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SERIALIZABLE) || type.equals(ENUM))
//      block.add("$N.writeSerializable($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(PARCELABLE_ARRAY))
//      block.add("$N.writeParcelableArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SPARSE_ARRAY))
//      block.add("$N.writeSparseArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SPARSE_BOOLEAN_ARRAY))
//      block.add("$N.writeSparseBooleanArray($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(BUNDLE))
//      block.add("$N.writeBundle($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(PERSISTABLE_BUNDLE))
//      block.add("$N.writePersistableBundle($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SIZE))
//      block.add("$N.writeSize($N.$N())", dest, fieldName, methodName);
//    else if (type.equals(SIZEF))
//      block.add("$N.writeSizeF($N.$N())", dest, fieldName, methodName);
//
//    block.add(";\n");
//
//    if (isNullable) {
//      block.endControlFlow();
//    }
//
//    return block.build();
//  }
//
//  public static CodeBlock writeParcelWrapper(String wrapperName, ParameterSpec dest, String fieldName, int position, boolean isNullable) {
//    CodeBlock.Builder block = CodeBlock.builder();
//    String methodName = "component" + position;
//
//    if (isNullable) {
//      block.beginControlFlow("if ($N.$N() == null)", fieldName, methodName);
//      block.addStatement("$N.writeInt(1)", dest);
//      block.nextControlFlow("else");
//      block.addStatement("$N.writeInt(0)", dest);
//    }
//
//    block.add("$N.writeParcelable($N.wrap($N.$N()), 0)", dest, wrapperName, fieldName, methodName);
//
//    block.add(";\n");
//
//    if (isNullable) {
//      block.endControlFlow();
//    }
//
//    return block.build();
//  }
//
//  public static CodeBlock readParcelWrapper(String wrapperName, ParameterSpec in, int position, TypeElement element, boolean isNullable) {
//    CodeBlock.Builder block = CodeBlock.builder();
//    String fieldName = "component" + position;
//
//    block.add("$N $N = ", wrapperName, fieldName);
//
//    if (isNullable){
//      block.add("$N.readInt() == 0 ? ", in);
//    }
//
//    block.add("($N) $N.readParcelable($T.class.getClassLoader())", wrapperName, in, element);
//
//    if (isNullable){
//      block.add(" : null");
//    }
//
//    block.add(";\n");
//
//    return block.build();
//  }
//
//  public static TypeName getParcelableType(Types types, TypeMirror typeMirror) {
//    TypeElement type = (TypeElement) types.asElement(typeMirror);
//    while (typeMirror.getKind() != TypeKind.NONE) {
//
//      // first, check if the class is valid.
//      TypeName typeName = get(typeMirror);
//      if (typeName instanceof ParameterizedTypeName) {
//        typeName = ((ParameterizedTypeName) typeName).rawType;
//      }
//      if (typeName.isPrimitive() || VALID_TYPES.contains(typeName)) {
//        return typeName;
//      }
//
//      // then check if it implements valid interfaces
//      for (TypeMirror iface : type.getInterfaces()) {
//        TypeName ifaceName = get(iface);
//        if (VALID_TYPES.contains(ifaceName)) {
//          return ifaceName;
//        }
//      }
//
//      // then move on
//      type = (TypeElement) types.asElement(typeMirror);
//      typeMirror = type.getSuperclass();
//    }
//    return null;
//  }
}
