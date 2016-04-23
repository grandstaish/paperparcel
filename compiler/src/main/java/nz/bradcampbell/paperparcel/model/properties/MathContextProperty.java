package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

public class MathContextProperty extends Property {

  public MathContextProperty(boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in,
      @Nullable FieldSpec classLoader, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    String roundingModeName = getUniqueName(getName() + "RoundingMode", scopedVariableNames);
    TypeName roundingModeTypeName = TypeName.get(RoundingMode.class);
    CodeBlock roundingMode = new EnumProperty(true, roundingModeTypeName, roundingModeName)
        .readFromParcel(block, in, classLoader, typeAdaptersMap, scopedVariableNames);
    return CodeBlock.of("new $T($N.readInt(), $L)", MathContext.class, in, roundingMode);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest,
      ParameterSpec flags, CodeBlock sourceLiteral, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    String roundingModeName = getUniqueName(getName() + "RoundingMode", scopedVariableNames);
    TypeName roundingModeTypeName = TypeName.get(RoundingMode.class);
    CodeBlock roundingModeSourceLiteral = CodeBlock.of("$L.getRoundingMode()", sourceLiteral);
    new EnumProperty(true, roundingModeTypeName, roundingModeName)
        .writeToParcel(block, dest, flags, roundingModeSourceLiteral, typeAdaptersMap,
            scopedVariableNames);
    block.addStatement("$N.writeInt($L.getPrecision())", dest, sourceLiteral);
  }
}
