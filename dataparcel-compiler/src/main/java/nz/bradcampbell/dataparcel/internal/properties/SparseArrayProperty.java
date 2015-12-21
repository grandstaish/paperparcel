package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;
import static nz.bradcampbell.dataparcel.internal.PropertyCreator.isValidType;

public class SparseArrayProperty extends Property {
  private final String typeArgumentString;
  private final boolean isValidArgument;
  private final TypeElement requiredParcel;

  public SparseArrayProperty(Types types, boolean isNullable, String name, VariableElement variableElement) {
    super(isNullable, name, variableElement);

    List<? extends TypeMirror> typeArguments = ((DeclaredType) variableElement.asType()).getTypeArguments();
    TypeMirror typeArgument = typeArguments != null ? typeArguments.get(0) : null;

    TypeName typeName = null;
    if (typeArgument != null) {
      typeName = ClassName.get(types.erasure(typeArgument));
    }

    isValidArgument = typeName == null || isValidType(typeName);

    String simpleName = typeName == null ? null : ((ClassName) typeName).simpleName();
    typeArgumentString = isValidArgument ? simpleName : simpleName == null ? null : simpleName + "Parcel";
    requiredParcel = isValidArgument ? null : (TypeElement) types.asElement(typeArgument);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName integerType = ClassName.get(Integer.class);
    TypeName mapTypeName = ClassName.get(Map.class);

    String wrappedName = getName() + "Wrapped";

    block.addStatement("new $T()", getTypeName());

    block.addStatement("$T<$T, $N> $N = $N.readHashMap(getClass().getClassLoader())", mapTypeName,
        integerType, typeArgumentString, wrappedName, in);

    String getValue = isValidArgument ? "" : ".getContents()";
    block.beginControlFlow("for ($T key : $N.keySet())", integerType, wrappedName);
    block.addStatement("$N.append(key, $N.get(key)$N)", getName(), wrappedName, getValue);

    block.unindent();
    block.add("}");
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest) {
    TypeName integerType = ClassName.get(Integer.class);
    TypeName mapTypeName = ClassName.get(Map.class);
    TypeName hashMapTypeName = ClassName.get(HashMap.class);
    String wrappedName = getName() + "Wrapped";

    block.addStatement("$T $N = $N.$N()", getTypeName(), getName(), DATA_VARIABLE_NAME, getName());
    block.addStatement("$T<$T, $N> $N = new $T<$T, $N>()", mapTypeName, integerType, typeArgumentString,
        wrappedName, hashMapTypeName, integerType, typeArgumentString);

    block.beginControlFlow("for (int i = 0; i < $N.size(); i++)", getName());
    block.addStatement("int key = $N.keyAt(i)", getName());

    String putVal = isValidArgument ? getName() + ".get(key)" :
        typeArgumentString + ".wrap(" + getName() + ".get(key))";
    block.addStatement("$N.put(key, $N)", wrappedName, putVal);

    block.endControlFlow();

    block.add("$N.writeMap($N)", dest, wrappedName);
  }

  @Override public List<TypeElement> requiredParcels() {
    return requiredParcel == null ? super.requiredParcels() : Collections.singletonList(requiredParcel);
  }
}
