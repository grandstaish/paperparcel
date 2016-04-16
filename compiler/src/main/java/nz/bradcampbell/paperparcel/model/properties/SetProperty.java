package nz.bradcampbell.paperparcel.model.properties;

import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SetProperty extends Property {
  private final Property typeArgument;
  private final boolean isInterface;

  public SetProperty(Property typeArgument, boolean isInterface, boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
    this.typeArgument = typeArgument;
    this.isInterface = isInterface;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    // Read size
    String setSize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, setSize, in);

    // Add setSize to scoped names
    scopedVariableNames.add(setSize);

    // Create set to read into
    String setName = getUniqueName(getName(), scopedVariableNames);

    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    TypeName parameterTypeName = typeArgument.getTypeName();
    if (parameterTypeName instanceof WildcardTypeName) {
      ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
      typeName = ParameterizedTypeName.get(originalType.rawType, parameterTypeName);
    }

    if (isInterface) {
      block.addStatement("$T $N = new $T<$T>($N)", typeName, setName, LinkedHashSet.class, parameterTypeName, setSize);
    } else {
      block.addStatement("$T $N = new $T()", typeName, setName, typeName);
    }

    // Add setName to scoped names
    scopedVariableNames.add(setName);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, setSize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    // Read in the parameter.
    CodeBlock itemLiteral = typeArgument.readFromParcel(block, in, classLoader, typeAdaptersMap,
                                                        loopScopedVariableNames);

    // Add the parameter to the output list
    block.addStatement("$N.add($L)", setName, itemLiteral);

    block.endControlFlow();

    return CodeBlock.of("$N", setName);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {

    String setSize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $L.size()", int.class, setSize, sourceLiteral);

    // Add setSize to scoped names
    scopedVariableNames.add(setSize);

    // Write size
    block.addStatement("$N.writeInt($N)", dest, setSize);

    // Write a loop to iterate through each parameter
    String parameterItemName = getUniqueName(getName() + "Item", scopedVariableNames);

    TypeName parameterTypeName = typeArgument.getTypeName();

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    block.beginControlFlow("for ($T $N : $L)", parameterTypeName, parameterItemName, sourceLiteral);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add parameterItemName to scoped names
    loopScopedVariableNames.add(parameterItemName);

    CodeBlock parameterSource = CodeBlock.of("$N", parameterItemName);

    // Write in the item
    typeArgument.writeToParcel(block, dest, flags, parameterSource, typeAdaptersMap, loopScopedVariableNames);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return typeArgument.requiresClassLoader();
  }

  @Override public Set<Adapter> requiredTypeAdapters() {
    return typeArgument.requiredTypeAdapters();
  }
}
