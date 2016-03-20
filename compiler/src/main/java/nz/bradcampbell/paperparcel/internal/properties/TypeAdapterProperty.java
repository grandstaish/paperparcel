package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

public class TypeAdapterProperty extends Property {
  public TypeAdapterProperty(Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName typeAdapterTypeName = getPropertyType().getTypeAdapter();
    String typeAdapterName = getName() + "TypeAdapter";
    block.addStatement("$T $N = new $T()", typeAdapterTypeName, typeAdapterName, typeAdapterTypeName);
    return literal("$N.readFromParcel($N)", typeAdapterName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    TypeName typeAdapterTypeName = getPropertyType().getTypeAdapter();
    String typeAdapterName = getName() + "TypeAdapter";
    block.addStatement("$T $N = new $T()", typeAdapterTypeName, typeAdapterName, typeAdapterTypeName);
    block.addStatement("$N.writeToParcel($L, $N)", typeAdapterName, sourceLiteral, dest);
  }
}
