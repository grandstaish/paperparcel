package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class ParcelableArrayProperty extends Property {
  public ParcelableArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readParcelableArray(getClass().getClassLoader())", getName(), wrappedTypeName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelableArray($N)", dest, variableName);
  }
}
