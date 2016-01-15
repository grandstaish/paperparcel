package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class CharSequenceArrayProperty extends Property {
  public CharSequenceArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName charSequenceArrayTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readValue($N)", getName(), charSequenceArrayTypeName, in, classLoader);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeValue($N)", dest, variableName);
  }

  @Override public boolean requiresClassLoader() {
    return true;
  }
}
