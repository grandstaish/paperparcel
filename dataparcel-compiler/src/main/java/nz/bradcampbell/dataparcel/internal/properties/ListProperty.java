package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import java.util.ArrayList;

import static nz.bradcampbell.dataparcel.internal.Properties.createProperty;

public class ListProperty extends Property {
  public ListProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    Property.Type propertyType = getPropertyType();
    TypeName wrappedTypeName = propertyType.getWrappedTypeName();

    if (propertyType.isParcelable()) {
      if (propertyType.isInterface()) {
        block.addStatement("$N = ($T) $N.readArrayList(getClass().getClassLoader())", getName(), wrappedTypeName, in);
      } else {
        block.addStatement("$N = new $T()", getName(), wrappedTypeName);
        block.addStatement("$N.readList($N, getClass().getClassLoader())", in, getName());
      }
    } else {
      if (propertyType.isInterface()) {
        block.addStatement("$T $N = ($T) $N.readArrayList(getClass().getClassLoader())", wrappedTypeName,
            getWrappedName(), wrappedTypeName, in);
      } else {
        block.addStatement("$T $N = new $T()", wrappedTypeName, getWrappedName(), wrappedTypeName);
        block.addStatement("$N.readList($N, getClass().getClassLoader())", in, getWrappedName());
      }
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();

    if (propertyType.isParcelable()) {
      super.unparcelVariable(block);
    } else {

      Property.Type parameterPropertyType = propertyType.getChildType(0);
      TypeName parameterType = parameterPropertyType.getTypeName();
      TypeName wrappedParameterType = parameterPropertyType.getWrappedTypeName();

      if (propertyType.isInterface()) {
        TypeName arrayListTypeName = TypeName.get(ArrayList.class);
        block.addStatement("$N = new $T<$T>($N.size())", getName(), arrayListTypeName, parameterType,
            getWrappedName());
      } else {
        block.addStatement("$N = new $T()", getName(), propertyType.getTypeName());
      }

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

      Property.Type parameterPropertyType = propertyType.getChildType(0);
      TypeName parameterType = parameterPropertyType.getTypeName();
      TypeName wrappedParameterType = parameterPropertyType.getWrappedTypeName();

      if (propertyType.isInterface()) {
        TypeName arrayListTypeName = TypeName.get(ArrayList.class);
        TypeName wrappedTypeName = propertyType.getWrappedTypeName();
        block.addStatement("$T $N = new $T<$T>($N.size())", wrappedTypeName, wrappedName, arrayListTypeName,
            wrappedParameterType, variableName);
      } else {
        TypeName wrappedTypeName = propertyType.getWrappedTypeName();
        block.addStatement("$T $N = new $T()", wrappedTypeName, wrappedName, wrappedTypeName);
      }


      String parameterItemName = variableName + "Item";

      block.beginControlFlow("for ($T $N : $N)", parameterType, parameterItemName, variableName);

      String innerName = "_" + variableName;
      String innerVariableName = createProperty(parameterPropertyType, true, innerName)
          .generateParcelableVariable(block, parameterItemName);
      block.addStatement("$N.add($N)", wrappedName, innerVariableName);

      block.endControlFlow();

      variableName = wrappedName;
    }

    return variableName;
  }
}
