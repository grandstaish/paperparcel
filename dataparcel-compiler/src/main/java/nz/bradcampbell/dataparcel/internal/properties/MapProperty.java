package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import java.util.HashMap;

import static nz.bradcampbell.dataparcel.internal.Properties.createProperty;

public class MapProperty extends Property {
  public MapProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    Property.Type propertyType = getPropertyType();
    TypeName wrappedTypeName = propertyType.getWrappedTypeName();

    if (propertyType.isParcelable()) {
      if (propertyType.isInterface()) {
        block.addStatement("$N = ($T) $N.readHashMap(getClass().getClassLoader())", getName(), wrappedTypeName, in);
      } else {
        block.addStatement("$N = new $T()", getName(), wrappedTypeName);
        block.addStatement("$N.readMap($N, getClass().getClassLoader())", in, getName());
      }
    } else {
      if (propertyType.isInterface()) {
        block.addStatement("$T $N = ($T) $N.readHashMap(getClass().getClassLoader())", wrappedTypeName,
            getWrappedName(), wrappedTypeName, in);
      } else {
        block.addStatement("$T $N = new $T()", wrappedTypeName, getWrappedName(), wrappedTypeName);
        block.addStatement("$N.readMap($N, getClass().getClassLoader())", in, getWrappedName());
      }
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();
    if (propertyType.isParcelable()) {
      super.unparcelVariable(block);
    } else {

      Type keyParameterPropertyType = propertyType.getChildType(0);
      TypeName keyParameterType = keyParameterPropertyType.getTypeName();
      TypeName keyWrappedParameterType = keyParameterPropertyType.getWrappedTypeName();

      Type valueParameterPropertyType = propertyType.getChildType(1);
      TypeName valueParameterType = valueParameterPropertyType.getTypeName();
      TypeName valueWrappedParameterType = valueParameterPropertyType.getWrappedTypeName();

      if (propertyType.isInterface()) {
        TypeName hashMapTypeName = TypeName.get(HashMap.class);
        block.addStatement("$N = new $T<$T, $T>($N.size())", getName(), hashMapTypeName, keyParameterType,
            valueParameterType, getWrappedName());
      } else {
        block.addStatement("$N = new $T()", getName(), propertyType.getTypeName());
      }

      String innerWrappedName = "_" + getWrappedName();
      block.beginControlFlow("for ($T $N : $N.keySet())", keyWrappedParameterType, innerWrappedName, getWrappedName());
      String keyInnerName = "_" + getName();
      block.addStatement("$T $N = null", keyParameterType, keyInnerName);
      createProperty(keyParameterPropertyType, true, keyInnerName).unparcelVariable(block);

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

      Property.Type keyParameterPropertyType = propertyType.getChildType(0);
      TypeName keyParameterType = keyParameterPropertyType.getTypeName();
      TypeName keyWrappedParameterType = keyParameterPropertyType.getWrappedTypeName();

      Type valueParameterPropertyType = propertyType.getChildType(1);
      TypeName valueWrappedParameterType = valueParameterPropertyType.getWrappedTypeName();

      if (propertyType.isInterface()) {
        TypeName hashMapTypeName = TypeName.get(HashMap.class);
        TypeName wrappedTypeName = propertyType.getWrappedTypeName();
        block.addStatement("$T $N = new $T<$T, $T>($N.size())", wrappedTypeName, wrappedName, hashMapTypeName,
            keyWrappedParameterType, valueWrappedParameterType, variableName);
      } else {
        TypeName wrappedTypeName = propertyType.getWrappedTypeName();
        block.addStatement("$T $N = new $T()", wrappedTypeName, wrappedName, wrappedTypeName);
      }

      String parameterItemName = variableName + "Item";
      block.beginControlFlow("for ($T $N : $N.keySet())", keyParameterType, parameterItemName, variableName);
      String keyInnerName = "_" + variableName;
      String keyInnerVariableName = createProperty(keyParameterPropertyType, true, keyInnerName)
          .generateParcelableVariable(block, parameterItemName);

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
