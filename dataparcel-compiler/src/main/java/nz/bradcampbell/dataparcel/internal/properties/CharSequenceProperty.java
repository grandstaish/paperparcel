package nz.bradcampbell.dataparcel.internal.properties;

import android.text.TextUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class CharSequenceProperty extends Property {
  public CharSequenceProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName textUtilsTypeName = ClassName.get(TextUtils.class);
    block.addStatement("$N = $T.CHAR_SEQUENCE_CREATOR.createFromParcel($N)", getName(), textUtilsTypeName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    TypeName textUtilsTypeName = ClassName.get(TextUtils.class);
    block.addStatement("$T.writeToParcel($N, $N, 0)", textUtilsTypeName, variableName, dest);
  }
}
