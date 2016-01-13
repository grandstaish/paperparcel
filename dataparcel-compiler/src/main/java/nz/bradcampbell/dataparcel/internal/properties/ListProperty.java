package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import java.util.ArrayList;

import static nz.bradcampbell.dataparcel.internal.PropertyCreator.createProperty;

public class ListProperty extends Property {
  public ListProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    Property.Type propertyType = getPropertyType();
    TypeName wrappedTypeName = propertyType.getWrappedTypeName();

    if (propertyType.isParcelable()) {
      block.addStatement("$N = ($T) $N.readArrayList(getClass().getClassLoader())", getName(), wrappedTypeName, in);
    } else {
      block.addStatement("$T $N = ($T) $N.readArrayList(getClass().getClassLoader())", wrappedTypeName,
          getWrappedName(), wrappedTypeName, in);
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();
    if (propertyType.isParcelable()) {
      super.unparcelVariable(block);
    } else {
      TypeName arrayListTypeName = TypeName.get(ArrayList.class);
      block.addStatement("$N = new $T<>($N.size())", getName(), arrayListTypeName, getWrappedName());
      Property.Type parameterPropertyType = propertyType.getChildType(0);
      TypeName parameterType = parameterPropertyType.getTypeName();
      TypeName wrappedParameterType = parameterPropertyType.getWrappedTypeName();
      String innerWrappedName = "_" + getWrappedName();
      block.beginControlFlow("for ($T $N : $N)", wrappedParameterType, innerWrappedName, getWrappedName());
      String innerName = "_" + getName();
      block.addStatement("$T $N = null", parameterType, innerName);
      createProperty(parameterPropertyType, true, innerName).unparcelVariable(block);
      block.addStatement("$N.add($N)", getName(), innerName);
      block.endControlFlow();
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeList($N)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    Property.Type propertyType = getPropertyType();
    String variableName = getName();
    block.addStatement("$T $N = $N", propertyType.getTypeName(), variableName, source);
    if (!propertyType.isParcelable()) {
      String wrappedName = getWrappedName();
      TypeName arrayListTypeName = TypeName.get(ArrayList.class);
      TypeName wrappedTypeName = propertyType.getWrappedTypeName();
      block.addStatement("$T $N = new $T<>($N.size())", wrappedTypeName, wrappedName, arrayListTypeName, variableName);
      Property.Type parameterPropertyType = propertyType.getChildType(0);
      TypeName parameterType = parameterPropertyType.getTypeName();
      String parameterItemName = variableName + "Item";
      block.beginControlFlow("for ($T $N : $N)", parameterType, parameterItemName, variableName);
      String innerName = "_" + variableName;
      String innerVariableName = createProperty(parameterPropertyType, true, innerName)
          .generateParcelableVariable(block, parameterItemName);
      block.addStatement("$N.add($N)", wrappedName, innerVariableName);
      block.endControlFlow();
      return wrappedName;
    }
    return variableName;
  }
}
