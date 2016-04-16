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

public class CharProperty extends Property {
  public CharProperty(boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    return CodeBlock.of("($T) $N.readInt()", TypeName.CHAR, in);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    block.addStatement("$N.writeInt($L)", dest, sourceLiteral);
  }
}
