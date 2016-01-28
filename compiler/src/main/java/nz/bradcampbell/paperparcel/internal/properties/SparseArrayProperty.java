package nz.bradcampbell.paperparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import static nz.bradcampbell.paperparcel.internal.Utils.createProperty;
import static nz.bradcampbell.paperparcel.internal.Utils.literal;

public class SparseArrayProperty extends Property {
  public SparseArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    Property.Type propertyType = getPropertyType();
    Property.Type parameterPropertyType = propertyType.getChildType(0);

    // Read size
    String sparseArraySize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, sparseArraySize, in);

    // Create SparseArray to read into
    String sparseArrayName = getName();
    TypeName typeName = propertyType.getTypeName();
    block.addStatement("$T $N = new $T($N)", typeName, sparseArrayName, typeName, sparseArraySize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    String keyName = getName() + "Key";
    String valueName = getName() + "Value";

    // Read the key
    block.addStatement("$T $N = $N.readInt()", int.class, keyName, in);

    // Read in the value. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock parameterLiteral = createProperty(parameterPropertyType, true, valueName)
        .readFromParcel(block, in, classLoader);

    // Add the parameter to the output list
    block.addStatement("$N.put($N, $L)", sparseArrayName, keyName, parameterLiteral);

    block.endControlFlow();

    return literal("$N", sparseArrayName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    // Write size
    String sparseArraySize = getName() + "Size";
    block.addStatement("$T $N = $L.size()", int.class, sparseArraySize, sourceLiteral);
    block.addStatement("$N.writeInt($N)", dest, sparseArraySize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    Property.Type propertyType = getPropertyType();
    Property.Type parameterPropertyType = propertyType.getChildType(0);
    TypeName parameterTypeName = parameterPropertyType.getWildcardTypeName();

    // Handle wildcard types
    if (parameterTypeName instanceof ParameterizedTypeName) {
      parameterTypeName = parameterPropertyType.getWildcardTypeName();
    }
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    String keyName = getName() + "Key";
    block.addStatement("$T $N = $L.keyAt($N)", int.class, keyName, sourceLiteral, indexName);
    block.addStatement("$N.writeInt($N)", dest, keyName);

    String valueName = getName() + "Value";
    block.addStatement("$T $N = $L.get($N)", parameterTypeName, valueName, sourceLiteral, keyName);

    String parameterName = getName() + "Param";
    CodeBlock parameterSource = literal("$N", valueName);

    // Write in the parameter. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    createProperty(parameterPropertyType, true, parameterName).writeToParcel(block, dest, parameterSource);

    block.endControlFlow();
  }
}
