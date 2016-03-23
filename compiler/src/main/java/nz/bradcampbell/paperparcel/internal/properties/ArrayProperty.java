package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.getRawTypeName;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

public class ArrayProperty extends Property {
  private final Property componentType;
  
  public ArrayProperty(Property componentType, TypeName typeName, boolean isInterface, boolean isNullable, String name,
                       @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.componentType = componentType;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
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

    // Read in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock componentLiteral = componentType.readFromParcel(block, in, classLoader);

    // Add the parameter to the output array
    block.addStatement("$N[$N] = $L", arrayName, indexName, componentLiteral);

    block.endControlFlow();

    return literal("$N", arrayName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
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

    // Write in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    componentType.writeToParcel(block, dest, componentSource);

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
}
