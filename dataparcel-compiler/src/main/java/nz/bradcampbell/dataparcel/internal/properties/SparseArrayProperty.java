package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.TypeMirror;

public class SparseArrayProperty extends Property {
  public SparseArrayProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
//    TypeName integerType = ClassName.get(Integer.class);
//    TypeName mapTypeName = ClassName.get(Map.class);
//
//    String wrappedName = getName() + "Wrapped";
//
//    block.addStatement("new $T()", getTypeName());
//
//    block.addStatement("$T<$T, $N> $N = $N.readHashMap(getClass().getClassLoader())", mapTypeName,
//        integerType, typeArgumentString, wrappedName, in);
//
//    String getValue = isValidArgument ? "" : ".getContents()";
//    block.beginControlFlow("for ($T key : $N.keySet())", integerType, wrappedName);
//    block.addStatement("$N.append(key, $N.get(key)$N)", getName(), wrappedName, getValue);
//
//    block.unindent();
//    block.add("}");

    // TODO:
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
//    TypeName integerType = ClassName.get(Integer.class);
//    TypeName mapTypeName = ClassName.get(Map.class);
//    TypeName hashMapTypeName = ClassName.get(HashMap.class);
//    String wrappedName = getName() + "Wrapped";
//
//    block.addStatement("$T $N = $N.$N()", getTypeName(), getName(), DATA_VARIABLE_NAME, getName());
//    block.addStatement("$T<$T, $N> $N = new $T<$T, $N>()", mapTypeName, integerType, typeArgumentString,
//        wrappedName, hashMapTypeName, integerType, typeArgumentString);
//
//    block.beginControlFlow("for (int i = 0; i < $N.size(); i++)", getName());
//    block.addStatement("int key = $N.keyAt(i)", getName());
//
//    String putVal = isValidArgument ? getName() + ".get(key)" :
//        typeArgumentString + ".wrap(" + getName() + ".get(key))";
//    block.addStatement("$N.put(key, $N)", wrappedName, putVal);
//
//    block.endControlFlow();
//
//    block.add("$N.writeMap($N)", dest, wrappedName);

    // TODO:
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    return null;
  }
}
