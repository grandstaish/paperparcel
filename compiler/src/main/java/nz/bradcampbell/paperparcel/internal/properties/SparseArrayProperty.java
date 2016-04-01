package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;
import static nz.bradcampbell.paperparcel.internal.utils.StringUtils.getUniqueName;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Adapter;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SparseArrayProperty extends Property {
  private Property typeArgument;

  public SparseArrayProperty(Property typeArgument, boolean isNullable, TypeName typeName, boolean isInterface,
                             String name, @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.typeArgument = typeArgument;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    // Read size
    String sparseArraySize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, sparseArraySize, in);

    // Add sparseArraySize to scoped names
    scopedVariableNames.add(sparseArraySize);

    // Create SparseArray to read into
    String sparseArrayName = getUniqueName(getName(), scopedVariableNames);

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

    block.addStatement("$T $N = new $T($N)", typeName, sparseArrayName, typeName, sparseArraySize);

    // Add sparseArrayName to scoped names
    scopedVariableNames.add(sparseArrayName);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    String keyName = getUniqueName(getName() + "Key", loopScopedVariableNames);

    // Read the key
    block.addStatement("$T $N = $N.readInt()", int.class, keyName, in);

    // Add the key to scoped names
    loopScopedVariableNames.add(keyName);

    // Read in the value
    CodeBlock valueLiteral = typeArgument.readFromParcel(block, in, classLoader, typeAdaptersMap,
                                                         loopScopedVariableNames);

    // Add the value to the output list
    block.addStatement("$N.put($N, $L)", sparseArrayName, keyName, valueLiteral);

    block.endControlFlow();

    return literal("$N", sparseArrayName);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {

    String sparseArraySize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $L.size()", int.class, sparseArraySize, sourceLiteral);

    // Add sparseArraySize to scoped names
    scopedVariableNames.add(sparseArraySize);

    // Write size
    block.addStatement("$N.writeInt($N)", dest, sparseArraySize);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    TypeName parameterTypeName = typeArgument.getTypeName();

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    String keyName = getUniqueName(getName() + "Key", loopScopedVariableNames);
    block.addStatement("$T $N = $L.keyAt($N)", int.class, keyName, sourceLiteral, indexName);

    // Add keyName to scoped names
    loopScopedVariableNames.add(keyName);

    block.addStatement("$N.writeInt($N)", dest, keyName);

    String valueName = getUniqueName(getName() + "Value", loopScopedVariableNames);
    block.addStatement("$T $N = $L.get($N)", parameterTypeName, valueName, sourceLiteral, keyName);

    // Add valueName to scoped names
    loopScopedVariableNames.add(valueName);

    CodeBlock parameterSource = literal("$N", valueName);

    // Write in the value
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
