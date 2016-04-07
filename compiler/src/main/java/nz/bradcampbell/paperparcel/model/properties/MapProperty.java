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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MapProperty extends Property {
  private final Property keyProperty;
  private final Property valueProperty;

  public MapProperty(Property keyProperty, Property valueProperty, boolean isNullable, TypeName typeName,
                     boolean isInterface, String name, @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.keyProperty = keyProperty;
    this.valueProperty = valueProperty;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    // Read size
    String mapSize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, mapSize, in);

    // Add mapSize to scoped names
    scopedVariableNames.add(mapSize);

    // Create map to read into
    String mapName = getUniqueName(getName(), scopedVariableNames);

    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    boolean patchedTypeName = false;

    TypeName keyTypeName = keyProperty.getTypeName();
    if (keyTypeName instanceof WildcardTypeName) {
      keyTypeName = ((WildcardTypeName) keyTypeName).upperBounds.get(0);
      patchedTypeName = true;
    }

    TypeName valueTypeName = valueProperty.getTypeName();
    if (valueTypeName instanceof WildcardTypeName) {
      valueTypeName = ((WildcardTypeName) valueTypeName).upperBounds.get(0);
      patchedTypeName = true;
    }

    if (patchedTypeName) {
      ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
      typeName = ParameterizedTypeName.get(originalType.rawType, keyTypeName, valueTypeName);
    }

    if (isInterface()) {
      block.addStatement("$T $N = new $T<$T, $T>($N)", typeName, mapName, LinkedHashMap.class, keyTypeName,
                         valueTypeName, mapSize);
    } else {
      block.addStatement("$T $N = new $T()", typeName, mapName, typeName);
    }

    // Add mapName to scopedNames
    scopedVariableNames.add(mapName);

    // Write a loop to iterate through each entity
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, mapSize, indexName);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    // Read in the key
    CodeBlock keyLiteral = keyProperty.readFromParcel(block, in, classLoader, typeAdaptersMap, loopScopedVariableNames);

    // Read in the value
    CodeBlock valueLiteral = valueProperty.readFromParcel(block, in, classLoader, typeAdaptersMap,
                                                          loopScopedVariableNames);

    // Add the parameter to the output map
    block.addStatement("$N.put($L, $L)", mapName, keyLiteral, valueLiteral);

    block.endControlFlow();

    return CodeBlock.of("$N", mapName);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {

    // Write size
    block.addStatement("$N.writeInt($L.size())", dest, sourceLiteral);

    TypeName keyTypeName = keyProperty.getTypeName();
    TypeName valueTypeName = valueProperty.getTypeName();

    // Write a loop to iterate through each entry
    String entryName = getUniqueName(getName() + "Entry", scopedVariableNames);
    block.beginControlFlow("for ($T<$T, $T> $N : $L.entrySet())", Map.Entry.class, keyTypeName, valueTypeName,
                           entryName, sourceLiteral);

    // Control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add entryName to scoped names
    loopScopedVariableNames.add(entryName);

    CodeBlock keySourceLiteral = CodeBlock.of("$N.getKey()", entryName);
    CodeBlock valueSourceLiteral = CodeBlock.of("$N.getValue()", entryName);

    // Write in the key.
    keyProperty.writeToParcel(block, dest, flags, keySourceLiteral, typeAdaptersMap, loopScopedVariableNames);

    // Write in the value.
    valueProperty.writeToParcel(block, dest, flags, valueSourceLiteral, typeAdaptersMap, loopScopedVariableNames);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return keyProperty.requiresClassLoader() || valueProperty.requiresClassLoader();
  }

  @Override public Set<Adapter> requiredTypeAdapters() {
    Set<Adapter> combined = new HashSet<>();
    combined.addAll(keyProperty.requiredTypeAdapters());
    combined.addAll(valueProperty.requiredTypeAdapters());
    return combined;
  }
}
