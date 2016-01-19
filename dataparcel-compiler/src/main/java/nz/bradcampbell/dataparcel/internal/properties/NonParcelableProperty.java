package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import static nz.bradcampbell.dataparcel.internal.Sources.literal;

public class NonParcelableProperty extends Property {
  public NonParcelableProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    Property.Type propertyType = getPropertyType();
    TypeName wrappedTypeName = propertyType.getWrappedTypeName();
    String wrappedName = getName() + "Parcel";
    block.addStatement("$T $N = $T.CREATOR.createFromParcel($N)", wrappedTypeName, wrappedName, wrappedTypeName, in);
    return literal("$N.getContents()", wrappedName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock source) {
    String wrappedName = getName() + "Parcel";
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$T $N = $T.wrap($L)", wrappedTypeName, wrappedName, wrappedTypeName, source);
    block.addStatement("$N.writeToParcel($N, 0)", wrappedName, dest);
  }
}
