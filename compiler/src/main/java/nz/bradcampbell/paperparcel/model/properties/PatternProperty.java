package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

public class PatternProperty extends Property {
  public PatternProperty(boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in,
      @Nullable FieldSpec classLoader, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    String name = getUniqueName(getName(), scopedVariableNames);
    block.addStatement("$T $N = null", Pattern.class, name);
    block.beginControlFlow("try");
    block.addStatement("$1N = $2T.compile($3N.readString(), $3N.readInt())", name, Pattern.class,
        in);
    block.nextControlFlow("catch($T e)", PatternSyntaxException.class);
    block.addStatement("throw new $T(e)", RuntimeException.class);
    block.endControlFlow();
    return CodeBlock.of(name);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest,
      ParameterSpec flags, CodeBlock sourceLiteral, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    block.addStatement("$N.writeString($L.pattern())", dest, sourceLiteral);
    block.addStatement("$N.writeInt($L.flags())", dest, sourceLiteral);
  }
}
