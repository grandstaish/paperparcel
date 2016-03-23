package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

public class TypeAdapterProperty extends Property {
  private final TypeName typeAdapter;

  public TypeAdapterProperty(TypeName typeAdapter, boolean isNullable, TypeName typeName, boolean isInterface,
                             String name, @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.typeAdapter = typeAdapter;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    String typeAdapterName = getName() + "TypeAdapter";
    block.addStatement("$T $N = new $T()", typeAdapter, typeAdapterName, typeAdapter);
    return literal("$N.readFromParcel($N)", typeAdapterName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    String typeAdapterName = getName() + "TypeAdapter";
    block.addStatement("$T $N = new $T()", typeAdapter, typeAdapterName, typeAdapter);
    block.addStatement("$N.writeToParcel($L, $N)", typeAdapterName, sourceLiteral, dest);
  }
}
