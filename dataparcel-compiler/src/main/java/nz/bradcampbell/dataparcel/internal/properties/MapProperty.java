package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import java.util.HashMap;

import static nz.bradcampbell.dataparcel.internal.PropertyCreator.createProperty;

public class MapProperty extends Property {
  public MapProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    Property.Type propertyType = getPropertyType();
    TypeName wrappedTypeName = propertyType.getWrappedTypeName();

    if (propertyType.isParcelable()) {
      block.addStatement("$N = ($T) $N.readHashMap(getClass().getClassLoader())", getName(), wrappedTypeName, in);
    } else {
      block.addStatement("$T $N = ($T) $N.readHashMap(getClass().getClassLoader())", wrappedTypeName,
          getWrappedName(), wrappedTypeName, in);
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();
    if (propertyType.isParcelable()) {
      super.unparcelVariable(block);
    } else {
      TypeName hashMapTypeName = TypeName.get(HashMap.class);
      block.addStatement("$N = new $T<>($N.size())", getName(), hashMapTypeName, getWrappedName());

      Type keyParameterPropertyType = propertyType.getTypeArgumentAtIndex(0);
      TypeName keyParameterType = keyParameterPropertyType.getTypeName();
      TypeName keyWrappedParameterType = keyParameterPropertyType.getWrappedTypeName();
      String innerWrappedName = "_" + getWrappedName();
      block.beginControlFlow("for ($T $N : $N.keySet())", keyWrappedParameterType, innerWrappedName, getWrappedName());
      String keyInnerName = "_" + getName();
      block.addStatement("$T $N = null", keyParameterType, keyInnerName);
      createProperty(keyParameterPropertyType, true, keyInnerName).unparcelVariable(block);

      Type valueParameterPropertyType = propertyType.getTypeArgumentAtIndex(1);
      TypeName valueParameterType = valueParameterPropertyType.getTypeName();
      TypeName valueWrappedParameterType = valueParameterPropertyType.getWrappedTypeName();
      String valueInnerName = "$" + getName();
      String valueInnerWrappedName = "$" + getWrappedName();
      block.addStatement("$T $N = $N.get($N)", valueWrappedParameterType, valueInnerWrappedName, getWrappedName(), innerWrappedName);
      block.addStatement("$T $N = null", valueParameterType, valueInnerName);
      createProperty(valueParameterPropertyType, true, valueInnerName).unparcelVariable(block);

      block.addStatement("$N.put($N, $N)", getName(), keyInnerName, valueInnerName);
      block.endControlFlow();
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeMap($N)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    Property.Type propertyType = getPropertyType();
    TypeName typeName = propertyType.getTypeName();
    String variableName = getName();

    block.addStatement("$T $N = $N", typeName, variableName, source);

    if (!propertyType.isParcelable()) {
      String wrappedName = getWrappedName();
      TypeName hashMapTypeName = TypeName.get(HashMap.class);
      TypeName wrappedTypeName = propertyType.getWrappedTypeName();
      block.addStatement("$T $N = new $T<>($N.size())", wrappedTypeName, wrappedName, hashMapTypeName, variableName);

      Property.Type keyParameterPropertyType = propertyType.getTypeArgumentAtIndex(0);
      TypeName keyParameterType = keyParameterPropertyType.getTypeName();
      String parameterItemName = variableName + "Item";
      block.beginControlFlow("for ($T $N : $N.keySet())", keyParameterType, parameterItemName, variableName);
      String keyInnerName = "_" + variableName;
      String keyInnerVariableName = createProperty(keyParameterPropertyType, true, keyInnerName)
          .generateParcelableVariable(block, parameterItemName);

      Type valueParameterPropertyType = propertyType.getTypeArgumentAtIndex(1);
      String valueInnerName = "$" + variableName;
      String valueSource = variableName + ".get(" + parameterItemName + ")";
      String valueInnerVariableName = createProperty(valueParameterPropertyType, true, valueInnerName)
          .generateParcelableVariable(block, valueSource);

      block.addStatement("$N.put($N, $N)", wrappedName, keyInnerVariableName, valueInnerVariableName);
      block.endControlFlow();
      return wrappedName;
    }

    return variableName;
  }
}
