package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class ValueProperty extends Property {
  public ValueProperty(Property.Type propertyType, boolean isNullable, String name) {
    // We can ignore isNullable here because readValue/writeValue handles null internally
    super(propertyType, false, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName typeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readValue($N)", getName(), typeName, in, classLoader);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeValue($N)", dest, variableName);
  }

  @Override public boolean requiresClassLoader() {
    return true;
  }
}
