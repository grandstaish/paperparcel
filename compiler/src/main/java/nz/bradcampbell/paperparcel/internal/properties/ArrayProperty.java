package nz.bradcampbell.paperparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.paperparcel.internal.Utils.createProperty;
import static nz.bradcampbell.paperparcel.internal.Utils.getRawTypeName;
import static nz.bradcampbell.paperparcel.internal.Utils.literal;

public class ArrayProperty extends Property {
  public ArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    Property.Type propertyType = getPropertyType();

    // Read size
    String arraySize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, arraySize, in);

    // Create array to read into
    String arrayName = getName();
    block.add("$T ", propertyType.getTypeName());
    block.add(generateArrayInitializer(arrayName, false, arraySize));

    // Write a loop to iterate through each component
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    Property.Type componentPropertyType = propertyType.getChildType(0);
    String componentName = getName() + "Item";

    // Read in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock componentLiteral = createProperty(componentPropertyType, true, componentName)
        .readFromParcel(block, in, classLoader);

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

    Property.Type propertyType = getPropertyType();
    Property.Type componentPropertyType = propertyType.getChildType(0);
    TypeName componentTypeName = componentPropertyType.getTypeName();
    String componentItemName = getName() + "Item";

    block.addStatement("$T $N = $L[$N]", componentTypeName, componentItemName, sourceLiteral, indexName);

    String componentName = getName() + "Component";
    CodeBlock componentSource = literal("$N", componentItemName);

    // Write in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    createProperty(componentPropertyType, true, componentName).writeToParcel(block, dest, componentSource);

    block.endControlFlow();
  }

  private CodeBlock generateArrayInitializer(String variableName, boolean wrapped, String size) {
    String initializer = "$N = new $T[$N]";

    Property.Type propertyType = getPropertyType();
    Property.Type componentType = propertyType.getChildType(0);

    TypeName componentTypeName = getRawTypeName(componentType, wrapped);
    while (componentTypeName instanceof ArrayTypeName) {
      componentType = componentType.getChildType(0);
      componentTypeName = getRawTypeName(componentType, wrapped);
      initializer += "[]";
    }

    return CodeBlock.builder()
        .addStatement(initializer, variableName, componentTypeName, size)
        .build();
  }
}
