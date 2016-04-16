package nz.bradcampbell.paperparcel.model.properties;

import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

import com.squareup.javapoet.ArrayTypeName;
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

public class ArrayProperty extends Property {
  private final Property componentType;
  
  public ArrayProperty(Property componentType, TypeName typeName, boolean isNullable, String name) {
    super(isNullable, typeName, name);
    this.componentType = componentType;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    // Read size
    String arraySize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $N.readInt()", int.class, arraySize, in);

    // Add arraySize name to scoped names
    scopedVariableNames.add(arraySize);

    // Create array to read into
    String arrayName = getUniqueName(getName(), scopedVariableNames);
    block.add("$T ", getTypeName());
    block.add(generateArrayInitializer(arrayName, arraySize));

    // Add arrayName to scoped names
    scopedVariableNames.add(arrayName);

    // Write a loop to iterate through each component
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    // Inside control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    // Read in the component.
    CodeBlock componentLiteral = componentType.readFromParcel(block, in, classLoader, typeAdaptersMap,
                                                              loopScopedVariableNames);

    // Add the parameter to the output array
    block.addStatement("$N[$N] = $L", arrayName, indexName, componentLiteral);

    block.endControlFlow();

    return CodeBlock.of("$N", arrayName);
  }

  @Override
  protected void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {

    String arraySize = getUniqueName(getName() + "Size", scopedVariableNames);
    block.addStatement("$T $N = $L.length", int.class, arraySize, sourceLiteral);

    // Add arraySize to scoped names
    scopedVariableNames.add(arraySize);

    // Write the size
    block.addStatement("$N.writeInt($N)", dest, arraySize);

    // Write a loop to iterate through each component
    String indexName = getUniqueName(getName() + "Index", scopedVariableNames);
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    // Inside control flow, new scope
    Set<String> loopScopedVariableNames = new LinkedHashSet<>(scopedVariableNames);

    // Add indexName to scoped names
    loopScopedVariableNames.add(indexName);

    TypeName componentTypeName = componentType.getTypeName();
    String componentItemName = getUniqueName(getName() + "Item", loopScopedVariableNames);

    // Handle wildcard types
    if (componentTypeName instanceof ParameterizedTypeName) {
      componentTypeName = componentType.getTypeName();
    }
    if (componentTypeName instanceof WildcardTypeName) {
      componentTypeName = ((WildcardTypeName) componentTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L[$N]", componentTypeName, componentItemName, sourceLiteral, indexName);

    // Add componentItemName to scopedVariables
    loopScopedVariableNames.add(componentItemName);

    CodeBlock componentSource = CodeBlock.of("$N", componentItemName);

    // Write in the component.
    componentType.writeToParcel(block, dest, flags, componentSource, typeAdaptersMap, loopScopedVariableNames);

    block.endControlFlow();
  }

  private CodeBlock generateArrayInitializer(String variableName, String size) {
    String initializer = "$N = new $T[$N]";

    Property componentTypeTemp = componentType;
    TypeName componentTypeName = getRawTypeName(componentTypeTemp.getTypeName());

    while (componentTypeName instanceof ArrayTypeName) {
      componentTypeTemp = ((ArrayProperty) componentType).componentType;
      componentTypeName = getRawTypeName(componentTypeTemp.getTypeName());
      initializer += "[]";
    }

    return CodeBlock.builder()
        .addStatement(initializer, variableName, componentTypeName, size)
        .build();
  }

  private TypeName getRawTypeName(TypeName typeName) {
    while (typeName instanceof ParameterizedTypeName) {
      typeName = ((ParameterizedTypeName) typeName).rawType;
    }
    return typeName;
  }

  @Override public boolean requiresClassLoader() {
    return componentType.requiresClassLoader();
  }

  @Override public Set<Adapter> requiredTypeAdapters() {
    return componentType.requiredTypeAdapters();
  }
}
