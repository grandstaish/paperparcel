package nz.bradcampbell.kraftpaper.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.kraftpaper.internal.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.kraftpaper.internal.Utils.getRawTypeName;
import static nz.bradcampbell.kraftpaper.internal.Utils.literal;

public class ParcelableProperty extends Property {
  public ParcelableProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName wrappedTypeName = getRawTypeName(getPropertyType(), true);
    return literal("$T.CREATOR.createFromParcel($N)", wrappedTypeName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    block.addStatement("$L.writeToParcel($N, 0)", sourceLiteral, dest);
  }
}
