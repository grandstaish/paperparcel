package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

public class StackTraceElementProperty extends Property {
  public StackTraceElementProperty(boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in,
      @Nullable FieldSpec classLoader, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    return CodeBlock.of("new $1T($2N.readString(), $2N.readString(), $2N.readString(), "
        + "$2N.readInt())", StackTraceElement.class, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest,
      ParameterSpec flags, CodeBlock sourceLiteral, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    block.addStatement("$N.writeString($L.getClassName())", dest, sourceLiteral);
    block.addStatement("$N.writeString($L.getMethodName())", dest, sourceLiteral);
    block.addStatement("$N.writeString($L.getFileName())", dest, sourceLiteral);
    block.addStatement("$N.writeInt($L.getLineNumber())", dest, sourceLiteral);
  }
}
