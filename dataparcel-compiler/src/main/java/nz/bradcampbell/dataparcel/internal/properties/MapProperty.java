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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;
import static nz.bradcampbell.dataparcel.internal.PropertyCreator.isValidType;

public class MapProperty extends Property {
  private final String keyTypeArgumentName;
  private final String keyTypeArgumentParcel;
  private final boolean isKeyTypeArgumentValid;

  private final String valueTypeArgumentName;
  private final String valueTypeArgumentParcel;
  private final boolean isValueTypeArgumentValid;

  private final List<TypeElement> requiredParcels = new ArrayList<TypeElement>();

  public MapProperty(Types types, boolean isNullable, String name, VariableElement variableElement) {
    super(isNullable, name, variableElement);

    List<? extends TypeMirror> typeArguments = ((DeclaredType) variableElement.asType()).getTypeArguments();
    TypeMirror keyTypeArgument = typeArguments != null ? typeArguments.get(0) : null;
    TypeMirror valueTypeArgument = typeArguments != null ? typeArguments.get(1) : null;

    TypeName keyTypeName = null;
    if (keyTypeArgument != null) {
      keyTypeName = ClassName.get(types.erasure(keyTypeArgument));
    }
    isKeyTypeArgumentValid = keyTypeName == null || isValidType(keyTypeName);
    String keyTypeStr = keyTypeName == null ? null : ((ClassName) keyTypeName).simpleName();
    keyTypeArgumentName = keyTypeStr;
    keyTypeArgumentParcel = keyTypeStr == null ? null : keyTypeStr + "Parcel";
    if (!isKeyTypeArgumentValid) {
      requiredParcels.add((TypeElement) types.asElement(keyTypeArgument));
    }

    TypeName valueTypeName = null;
    if (valueTypeArgument != null) {
      valueTypeName = ClassName.get(types.erasure(valueTypeArgument));
    }
    isValueTypeArgumentValid = valueTypeName == null || isValidType(valueTypeName);
    String valueTypeStr = valueTypeName == null ? null : ((ClassName) valueTypeName).simpleName();
    valueTypeArgumentName = valueTypeStr;
    valueTypeArgumentParcel = valueTypeStr == null ? null : valueTypeStr + "Parcel";
    if (!isValueTypeArgumentValid) {
      requiredParcels.add((TypeElement) types.asElement(valueTypeArgument));
    }
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    if (isKeyTypeArgumentValid && isValueTypeArgumentValid) {
      block.add("($T) $N.readHashMap(getClass().getClassLoader())", getVariableTypeName(), in);
    } else {
      TypeName mapTypeName = ClassName.get(Map.class);
      TypeName hashMapTypeName = ClassName.get(HashMap.class);

      if (isNullable()) {
        block.addStatement("$T<$N, $N> $N = null", mapTypeName, keyTypeArgumentName, valueTypeArgumentName,
            getGetterMethodName());
        block.beginControlFlow("if ($N.readInt() == 0)", in);
        block.add("$N = ", getGetterMethodName());
      } else {
        block.add("$T<$N, $N> $N = ", mapTypeName, keyTypeArgumentName, valueTypeArgumentName, getGetterMethodName());
      }

      String wrappedName = getGetterMethodName() + "Wrapped";

      block.addStatement("new $T<$N, $N>()", hashMapTypeName, keyTypeArgumentName, valueTypeArgumentName);

      String wrappedKey = isKeyTypeArgumentValid ? keyTypeArgumentName : keyTypeArgumentParcel;
      String wrappedValue = isValueTypeArgumentValid ? valueTypeArgumentName : valueTypeArgumentParcel;
      block.addStatement("$T<$N, $N> $N = $N.readHashMap(getClass().getClassLoader())", hashMapTypeName,
          wrappedKey, wrappedValue, wrappedName, in);

      String getKey = isKeyTypeArgumentValid ? "" : ".getContents()";
      String getValue = isValueTypeArgumentValid ? "" : ".getContents()";
      block.beginControlFlow("for ($N key : $N.keySet())", wrappedKey, wrappedName);
      block.addStatement("$N.put(key$N, $N.get(key)$N)", getGetterMethodName(), getKey, wrappedName, getValue);

      block.endControlFlow();

      if (isNullable()) {
        block.endControlFlow();
      }
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest) {
    if (isKeyTypeArgumentValid && isValueTypeArgumentValid) {
      block.add("$N.writeMap($N.$N())", dest, DATA_VARIABLE_NAME, getGetterMethodName());
    } else {
      String wrappedName = getGetterMethodName() + "Wrapped";
      TypeName mapTypeName = ClassName.get(Map.class);
      TypeName hashMapTypeName = ClassName.get(HashMap.class);

      block.addStatement("$T<$N, $N> $N = $N.$N()", mapTypeName, keyTypeArgumentName,
          valueTypeArgumentName, getGetterMethodName(), DATA_VARIABLE_NAME, getGetterMethodName());

      String wrappedKey = isKeyTypeArgumentValid ? keyTypeArgumentName : keyTypeArgumentParcel;
      String wrappedValue = isValueTypeArgumentValid ? valueTypeArgumentName : valueTypeArgumentParcel;
      block.addStatement("$T<$N, $N> $N = new $T<$N, $N>()", mapTypeName, wrappedKey, wrappedValue,
          wrappedName, hashMapTypeName, wrappedKey, wrappedValue);

      block.beginControlFlow("for ($N key : $N.keySet())", keyTypeArgumentName, getGetterMethodName());

      String putKey = isKeyTypeArgumentValid ? "key" : keyTypeArgumentParcel + ".wrap(key)";
      String putVal = isValueTypeArgumentValid ? getGetterMethodName() + ".get(key)" :
          valueTypeArgumentParcel + ".wrap(" + getGetterMethodName() + ".get(key))";
      block.addStatement("$N.put($N, $N)", wrappedName, putKey, putVal);

      block.endControlFlow();

      block.add("$N.writeMap($N)", dest, wrappedName);
    }
  }

  @Override public List<TypeElement> requiredParcels() {
    return requiredParcels;
  }

  @Override protected boolean useReadTemplate() {
    return isKeyTypeArgumentValid && isValueTypeArgumentValid;
  }
}
