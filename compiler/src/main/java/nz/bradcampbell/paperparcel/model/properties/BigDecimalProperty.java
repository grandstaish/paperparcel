package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

public class BigDecimalProperty extends Property {
  public BigDecimalProperty(boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in,
      @Nullable FieldSpec classLoader, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    String unscaled = StringUtils.getUniqueName(getName() + "Unscaled", scopedVariableNames);
    block.addStatement("$1T $2N = new $1T($3N.createByteArray())", BigInteger.class, unscaled, in);
    return CodeBlock.of("new $T($N, $N.readInt())", BigDecimal.class, unscaled, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest,
      ParameterSpec flags, CodeBlock sourceLiteral, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    block.addStatement("$N.writeByteArray($L.unscaledValue().toByteArray())", dest, sourceLiteral);
    block.addStatement("$N.writeInt($L.scale())", dest, sourceLiteral);
  }
}
