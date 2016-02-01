package nz.bradcampbell.paperparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.paperparcel.internal.Property;
import nz.bradcampbell.paperparcel.internal.utils.PropertyUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.createProperty;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

public class ListProperty extends Property {
  public ListProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    Property.Type propertyType = getPropertyType();
    Property.Type parameterPropertyType = propertyType.getChildType(0);

    // Read size
    String listSize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, listSize, in);

    // Create list to read into
    String listName = getName();

    TypeName typeName = propertyType.getWildcardTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    TypeName parameterTypeName = parameterPropertyType.getWildcardTypeName();
    if (parameterTypeName instanceof WildcardTypeName) {
      ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
      typeName = ParameterizedTypeName.get(originalType.rawType, parameterTypeName);
    }

    if (propertyType.isInterface()) {
      block.addStatement("$T $N = new $T<$T>($N)", typeName, listName, ArrayList.class, parameterTypeName, listSize);
    } else {
      block.addStatement("$T $N = new $T()", typeName, listName, typeName);
    }

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    String parameterName = getName() + "Item";

    // Read in the parameter. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock parameterLiteral = PropertyUtils.createProperty(parameterPropertyType, parameterName)
        .readFromParcel(block, in, classLoader);

    // Add the parameter to the output list
    block.addStatement("$N.add($L)", listName, parameterLiteral);

    block.endControlFlow();

    return literal("$N", listName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    // Write size
    String listSize = getName() + "Size";
    block.addStatement("$T $N = $L.size()", int.class, listSize, sourceLiteral);
    block.addStatement("$N.writeInt($N)", dest, listSize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    Property.Type propertyType = getPropertyType();
    Property.Type parameterPropertyType = propertyType.getChildType(0);
    TypeName parameterTypeName = parameterPropertyType.getWildcardTypeName();
    String parameterItemName = getName() + "Item";

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L.get($N)", parameterTypeName, parameterItemName, sourceLiteral, indexName);

    String parameterName = getName() + "Param";
    CodeBlock parameterSource = literal("$N", parameterItemName);

    // Write in the parameter. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    PropertyUtils.createProperty(parameterPropertyType, parameterName).writeToParcel(block, dest, parameterSource);

    block.endControlFlow();
  }
}
