package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class ParcelableProperty extends Property {
  public ParcelableProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    TypeName rawType = getPropertyType().getWrappedRawTypeName();
    block.addStatement("$N = ($T) $N.readParcelable($T.class.getClassLoader())", getName(), wrappedTypeName, in, rawType);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelable($N, 0)", dest, variableName);
  }
}
