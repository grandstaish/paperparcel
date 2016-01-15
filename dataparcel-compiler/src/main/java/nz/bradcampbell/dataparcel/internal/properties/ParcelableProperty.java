package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

public class ParcelableProperty extends Property {
  public ParcelableProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readParcelable($N)", getName(), wrappedTypeName, in, classLoader);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelable($N, 0)", dest, variableName);
  }

  @Override public boolean requiresClassLoader() {
    return true;
  }
}
