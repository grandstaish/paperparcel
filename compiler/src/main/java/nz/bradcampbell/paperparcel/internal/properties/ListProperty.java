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
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ListProperty extends Property {
  private final Property typeArgument;

  public ListProperty(Property typeArgument, boolean isNullable, TypeName typeName, boolean isInterface, String name,
                      @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.typeArgument = typeArgument;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, FieldSpec> typeAdapters, Set<String> scopedVariableNames) {
    // Read size
    String listSize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, listSize, in);

    // Add listSize to scoped names
    scopedVariableNames.add(listSize);

    // Create list to read into
    String listName = getUniqueName(getName(), scopedVariableNames);

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

    if (isInterface()) {
      block.addStatement("$T $N = new $T<$T>($N)", typeName, listName, ArrayList.class, parameterTypeName, listSize);
    } else {
      block.addStatement("$T $N = new $T()", typeName, listName, typeName);
    }

    // Add listName to scoped names
    scopedVariableNames.add(listName);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    // Read in the item
    CodeBlock itemLiteral = typeArgument.readFromParcel(block, in, classLoader, typeAdapters, loopScopedVariableNames);

    // Add the item to the output list
    block.addStatement("$N.add($L)", listName, itemLiteral);

    block.endControlFlow();

    return literal("$N", listName);
  }

  @Override protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, FieldSpec> typeAdapters, Set<String> scopedVariableNames) {

    String listSize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $L.size()", int.class, listSize, sourceLiteral);

    // Add listSize to scoped names
    scopedVariableNames.add(listSize);

    // Write size
    block.addStatement("$N.writeInt($N)", dest, listSize);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    TypeName parameterTypeName = typeArgument.getTypeName();
    String parameterItemName = getUniqueName(getName() + "Item", loopScopedVariableNames);

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L.get($N)", parameterTypeName, parameterItemName, sourceLiteral, indexName);

    // Add parameterItemName to scoped names
    loopScopedVariableNames.add(parameterItemName);

    CodeBlock parameterSource = literal("$N", parameterItemName);

    // Write in the parameter
    typeArgument.writeToParcel(block, dest, flags, parameterSource, typeAdapters, loopScopedVariableNames);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return typeArgument.requiresClassLoader();
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    return typeArgument.requiredTypeAdapters();
  }
}
