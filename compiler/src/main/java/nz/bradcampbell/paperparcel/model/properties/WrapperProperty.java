package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class WrapperProperty extends Property {
  private final TypeName wrapperType;

  public WrapperProperty(TypeName wrapperType, boolean isNullable, TypeName typeName, boolean isInterface, String name,
                         @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.wrapperType = wrapperType;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    return CodeBlock.of("$T.CREATOR.createFromParcel($N).get()", wrapperType, in);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    block.addStatement("new $T($L).writeToParcel($N, $N)", wrapperType, sourceLiteral, dest, flags);
  }
}
