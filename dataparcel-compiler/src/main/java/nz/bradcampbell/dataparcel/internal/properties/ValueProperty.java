package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class ValueProperty extends Property {
  public ValueProperty(Property.Type propertyType, boolean isNullable, String name) {
    // We can ignore isNullable here because readValue/writeValue handles null internally
    super(propertyType, false, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName typeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readValue($T.class.getClassLoader())", getName(), typeName, in, typeName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeValue($N)", dest, variableName);
  }
}
