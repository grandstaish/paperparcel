package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.getRawTypeName;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class ArrayProperty extends Property {
  private final Property componentType;
  
  public ArrayProperty(Property componentType, TypeName typeName, boolean isInterface, boolean isNullable, String name,
                       @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.componentType = componentType;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, FieldSpec> typeAdapters) {
    // Read size
    String arraySize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, arraySize, in);

    // Create array to read into
    String arrayName = getName();
    block.add("$T ", getTypeName());
    block.add(generateArrayInitializer(arrayName, arraySize));

    // Write a loop to iterate through each component
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    // Read in the component.
    CodeBlock componentLiteral = componentType.readFromParcel(block, in, classLoader, typeAdapters);

    // Add the parameter to the output array
    block.addStatement("$N[$N] = $L", arrayName, indexName, componentLiteral);

    block.endControlFlow();

    return literal("$N", arrayName);
  }

  @Override
  protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags,
                                    CodeBlock sourceLiteral, Map<ClassName, FieldSpec> typeAdapters) {
    // Write size
    String arraySize = getName() + "Size";
    block.addStatement("$T $N = $L.length", int.class, arraySize, sourceLiteral);
    block.addStatement("$N.writeInt($N)", dest, arraySize);

    // Write a loop to iterate through each component
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    TypeName componentTypeName = componentType.getTypeName();
    String componentItemName = getName() + "Item";

    // Handle wildcard types
    if (componentTypeName instanceof ParameterizedTypeName) {
      componentTypeName = componentType.getTypeName();
    }
    if (componentTypeName instanceof WildcardTypeName) {
      componentTypeName = ((WildcardTypeName) componentTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L[$N]", componentTypeName, componentItemName, sourceLiteral, indexName);

    CodeBlock componentSource = literal("$N", componentItemName);

    // Write in the component.
    componentType.writeToParcel(block, dest, flags, componentSource, typeAdapters);

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

  @Override public boolean requiresClassLoader() {
    return componentType.requiresClassLoader();
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    return componentType.requiredTypeAdapters();
  }
}
