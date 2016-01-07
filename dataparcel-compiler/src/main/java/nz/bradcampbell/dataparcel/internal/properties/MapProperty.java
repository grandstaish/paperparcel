package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;

import static nz.bradcampbell.dataparcel.internal.PropertyCreator.createProperty;

public class MapProperty extends Property {
  public MapProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName parcelableTypeName = getParcelableTypeName();
    if (isParcelable()) {
      block.addStatement("$N = ($T) $N.readHashMap(getClass().getClassLoader())", getName(), parcelableTypeName, in);
    } else {
      block.addStatement("$T $N = ($T) $N.readHashMap(getClass().getClassLoader())", parcelableTypeName,
          getWrappedName(), parcelableTypeName, in);
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    if (isParcelable()) {
      super.unparcelVariable(block);
    } else {
      TypeName hashMapTypeName = TypeName.get(HashMap.class);
      block.addStatement("$N = new $T<>($N.size())", getName(), hashMapTypeName, getWrappedName());
      TypeName parcelableTypeName = getParcelableTypeName();
      TypeMirror keyParameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(0);
      TypeName keyParameterType = ClassName.get(keyParameterTypeMirror);
      TypeName keyParcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(0);
      String innerWrappedName = "_" + getWrappedName();
      block.beginControlFlow("for ($T $N : $N.keySet())", keyParcelableParameterType, innerWrappedName, getWrappedName());
      String keyInnerName = "_" + getName();
      block.addStatement("$T $N = null", keyParameterType, keyInnerName);
      createProperty(keyParameterTypeMirror, true, keyInnerName, keyParcelableParameterType).unparcelVariable(block);
      TypeMirror valueParameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(1);
      TypeName valueParameterType = ClassName.get(valueParameterTypeMirror);
      TypeName valueParcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(1);
      String valueInnerName = "$" + getName();
      String valueInnerWrappedName = "$" + getWrappedName();
      block.addStatement("$T $N = $N.get($N)", valueParcelableParameterType, valueInnerWrappedName, getWrappedName(), innerWrappedName);
      block.addStatement("$T $N = null", valueParameterType, valueInnerName);
      createProperty(valueParameterTypeMirror, true, valueInnerName, valueParcelableParameterType).unparcelVariable(block);
      block.addStatement("$N.put($N, $N)", getName(), keyInnerName, valueInnerName);
      block.endControlFlow();
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeMap($N)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    String variableName = getName();
    block.addStatement("$T $N = $N", getOriginalTypeName(), variableName, source);
    if (!isParcelable()) {
      String wrappedName = getWrappedName();
      TypeName hashMapTypeName = TypeName.get(HashMap.class);
      TypeName parcelableTypeName = getParcelableTypeName();
      block.addStatement("$T $N = new $T<>($N.size())", parcelableTypeName, wrappedName, hashMapTypeName, variableName);
      TypeMirror keyParameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(0);
      TypeName keyParameterType = ClassName.get(keyParameterTypeMirror);
      TypeName keyParcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(0);
      String parameterItemName = variableName + "Item";
      block.beginControlFlow("for ($T $N : $N.keySet())", keyParameterType, parameterItemName, variableName);
      String keyInnerName = "_" + variableName;
      String keyInnerVariableName = createProperty(keyParameterTypeMirror, true, keyInnerName, keyParcelableParameterType)
          .generateParcelableVariable(block, parameterItemName);
      TypeMirror valueParameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(1);
      TypeName valueParcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(1);
      String valueInnerName = "$" + variableName;
      String valueSource = variableName + ".get(" + parameterItemName + ")";
      String valueInnerVariableName = createProperty(valueParameterTypeMirror, true, valueInnerName, valueParcelableParameterType)
          .generateParcelableVariable(block, valueSource);
      block.addStatement("$N.put($N, $N)", wrappedName, keyInnerVariableName, valueInnerVariableName);
      block.endControlFlow();
      return wrappedName;
    }
    return variableName;
  }
}
