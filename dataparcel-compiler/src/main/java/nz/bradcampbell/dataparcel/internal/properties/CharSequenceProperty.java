package nz.bradcampbell.dataparcel.internal.properties;

import nz.bradcampbell.dataparcel.Nullable;
import android.text.TextUtils;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import static nz.bradcampbell.dataparcel.internal.Utils.literal;

public class CharSequenceProperty extends Property {
  public CharSequenceProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    return literal("$T.CHAR_SEQUENCE_CREATOR.createFromParcel($N)", TextUtils.class, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    block.addStatement("$T.writeToParcel($L, $N, 0)", TextUtils.class, sourceLiteral, dest);
  }
}
