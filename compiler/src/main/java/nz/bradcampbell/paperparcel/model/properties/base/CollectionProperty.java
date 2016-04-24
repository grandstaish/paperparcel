package nz.bradcampbell.paperparcel.model.properties.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

public abstract class CollectionProperty<T extends Collection> extends Property {
  private final Property type;

  public CollectionProperty(Property type, boolean isNullable, TypeName typeName, String name) {
    super(isNullable, typeName, name);
    this.type = type;
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in,
      @Nullable FieldSpec classLoader, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    // Read size
    String size = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, size, in);

    // Add size to scoped names
    scopedVariableNames.add(size);

    // Create collection to read into
    String name = getUniqueName(getName(), scopedVariableNames);

    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    TypeName parameterTypeName = type.getTypeName();
    if (parameterTypeName instanceof WildcardTypeName) {
      ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
      typeName = ParameterizedTypeName.get(originalType.rawType, parameterTypeName);
    }

    if (isDefaultType()) {
      if (defaultTypeHasDefaultCapacityConstructor()) {
        block.addStatement("$T $N = new $T<$T>($N)", typeName, name, getDefaultType(),
            parameterTypeName, size);
      } else {
        block.addStatement("$T $N = new $T<$T>()", typeName, name, getDefaultType(),
            parameterTypeName);
      }
    } else {
      block.addStatement("$T $N = new $T()", typeName, name, typeName);
    }

    // Add name to scoped names
    scopedVariableNames.add(name);

    // Write a loop to iterate through each parameter
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, size,
        indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    // Read in the item
    CodeBlock itemLiteral = type.readFromParcel(block, in, classLoader, typeAdaptersMap,
        loopScopedVariableNames);

    // Add the parameter to the output collection
    block.addStatement("$N.add($L)", name, itemLiteral);

    block.endControlFlow();

    return CodeBlock.of("$N", name);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest,
      ParameterSpec flags, CodeBlock sourceLiteral, Map<ClassName, CodeBlock> typeAdaptersMap,
      Set<String> scopedVariableNames) {
    String size = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $L.size()", int.class, size, sourceLiteral);

    // Add size to scoped names
    scopedVariableNames.add(size);

    // Write size
    block.addStatement("$N.writeInt($N)", dest, size);

    // Write a loop to iterate through each parameter
    String parameterItemName = getUniqueName(getName() + "Item", scopedVariableNames);

    TypeName parameterTypeName = type.getTypeName();

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
    type.writeToParcel(block, dest, flags, parameterSource, typeAdaptersMap,
        loopScopedVariableNames);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return type.requiresClassLoader();
  }

  @Override public Set<Adapter> requiredTypeAdapters() {
    return type.requiredTypeAdapters();
  }

  public abstract Class<? extends T> getDefaultType();

  public abstract boolean isDefaultType();

  public abstract boolean defaultTypeHasDefaultCapacityConstructor();
}
