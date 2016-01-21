package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import nz.bradcampbell.dataparcel.internal.Property;

import static nz.bradcampbell.dataparcel.internal.Utils.literal;

public class DoubleProperty extends Property {
  public DoubleProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    return literal("$N.readDouble()", in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    block.addStatement("$N.writeDouble($L)", dest, sourceLiteral);
  }
}
