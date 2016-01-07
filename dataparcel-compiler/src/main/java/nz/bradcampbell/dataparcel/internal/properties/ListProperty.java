package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;

import static nz.bradcampbell.dataparcel.internal.PropertyCreator.createProperty;

public class ListProperty extends Property {
  public ListProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName parcelableTypeName = getParcelableTypeName();
    if (isParcelable()) {
      block.addStatement("$N = ($T) $N.readArrayList(getClass().getClassLoader())", getName(), parcelableTypeName, in);
    } else {
      block.addStatement("$T $N = ($T) $N.readArrayList(getClass().getClassLoader())", parcelableTypeName,
          getWrappedName(), parcelableTypeName, in);
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    if (isParcelable()) {
      super.unparcelVariable(block);
    } else {
      TypeName arrayListTypeName = TypeName.get(ArrayList.class);
      block.addStatement("$N = new $T<>($N.size())", getName(), arrayListTypeName, getWrappedName());
      TypeName parcelableTypeName = getParcelableTypeName();
      TypeMirror parameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(0);
      TypeName parameterType = ClassName.get(parameterTypeMirror);
      TypeName parcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(0);
      String innerWrappedName = "_" + getWrappedName();
      block.beginControlFlow("for ($T $N : $N)", parcelableParameterType, innerWrappedName, getWrappedName());
      String innerName = "_" + getName();
      block.addStatement("$T $N = null", parameterType, innerName);
      createProperty(parameterTypeMirror, true, innerName, parcelableParameterType).unparcelVariable(block);
      block.addStatement("$N.add($N)", getName(), innerName);
      block.endControlFlow();
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeList($N)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    String variableName = getName();
    block.addStatement("$T $N = $N", getOriginalTypeName(), variableName, source);
    if (!isParcelable()) {
      String wrappedName = getWrappedName();
      TypeName arrayListTypeName = TypeName.get(ArrayList.class);
      TypeName parcelableTypeName = getParcelableTypeName();
      block.addStatement("$T $N = new $T<>($N.size())", parcelableTypeName, wrappedName, arrayListTypeName, variableName);
      TypeMirror parameterTypeMirror = ((DeclaredType) getTypeMirror()).getTypeArguments().get(0);
      TypeName parameterType = ClassName.get(parameterTypeMirror);
      TypeName parcelableParameterType = ((ParameterizedTypeName) parcelableTypeName).typeArguments.get(0);
      String parameterItemName = variableName + "Item";
      block.beginControlFlow("for ($T $N : $N)", parameterType, parameterItemName, variableName);
      String innerName = "_" + variableName;
      String innerVariableName = createProperty(parameterTypeMirror, true, innerName, parcelableParameterType)
          .generateParcelableVariable(block, parameterItemName);
      block.addStatement("$N.add($N)", wrappedName, innerVariableName);
      block.endControlFlow();
      return wrappedName;
    }
    return variableName;
  }
}
