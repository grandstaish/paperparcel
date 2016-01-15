package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class NonParcelableProperty extends Property {
  public NonParcelableProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$T $N = ($T) $N.readParcelable($N)", wrappedTypeName, getWrappedName(), wrappedTypeName, in,
        classLoader);
    unparcelVariable(block);
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    block.addStatement("$N = $N.getContents()", getName(), getWrappedName());
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelable($N, 0)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source, boolean wildcard) {
    String variableName = getName();
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$T $N = $T.wrap($N)", wrappedTypeName, variableName, wrappedTypeName, source);
    return variableName;
  }

  @Override public boolean requiresClassLoader() {
    return true;
  }
}
