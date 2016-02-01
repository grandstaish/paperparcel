package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

public class CharSequenceProperty extends Property {
  private static final TypeName TEXT_UTILS = ClassName.get("android.text", "TextUtils");

  public CharSequenceProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    return literal("$T.CHAR_SEQUENCE_CREATOR.createFromParcel($N)", TEXT_UTILS, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    block.addStatement("$T.writeToParcel($L, $N, 0)", TEXT_UTILS, sourceLiteral, dest);
  }
}
