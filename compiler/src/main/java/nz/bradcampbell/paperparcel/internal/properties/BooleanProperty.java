package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BooleanProperty extends Property {
  public BooleanProperty(boolean isNullable, TypeName typeName, boolean isInterface, String name,
                         @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, FieldSpec> typeAdapters) {
    return literal("$N.readInt() == 1", in);
  }

  @Override
  protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral,
                                    Map<ClassName, FieldSpec> typeAdapters) {
    block.addStatement("$N.writeInt($L ? 1 : 0)", dest, sourceLiteral);
  }
}
