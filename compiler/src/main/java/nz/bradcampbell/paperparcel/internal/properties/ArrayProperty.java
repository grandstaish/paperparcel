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
import nz.bradcampbell.paperparcel.internal.utils.PropertyUtils;
import org.jetbrains.annotations.Nullable;

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
    block.add("$T ", propertyType.getWildcardTypeName());
    block.add(generateArrayInitializer(arrayName, false, arraySize));

    // Write a loop to iterate through each component
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, arraySize, indexName);

    Property.Type componentPropertyType = propertyType.getChildType(0);
    String componentName = getName() + "Item";

    // Read in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock componentLiteral = PropertyUtils.createProperty(componentPropertyType, componentName)
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
    TypeName componentTypeName = componentPropertyType.getWildcardTypeName();
    String componentItemName = getName() + "Item";

    // Handle wildcard types
    if (componentTypeName instanceof ParameterizedTypeName) {
      componentTypeName = componentPropertyType.getWildcardTypeName();
    }
    if (componentTypeName instanceof WildcardTypeName) {
      componentTypeName = ((WildcardTypeName) componentTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L[$N]", componentTypeName, componentItemName, sourceLiteral, indexName);

    String componentName = getName() + "Component";
    CodeBlock componentSource = literal("$N", componentItemName);

    // Write in the component. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    PropertyUtils.createProperty(componentPropertyType, componentName).writeToParcel(block, dest, componentSource);

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
