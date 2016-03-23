package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

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
                                          Map<ClassName, FieldSpec> typeAdapters) {
    // Read size
    String listSize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, listSize, in);

    // Create list to read into
    String listName = getName();

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

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    // Read in the parameter.
    CodeBlock parameterLiteral = typeArgument.readFromParcel(block, in, classLoader, typeAdapters);

    // Add the parameter to the output list
    block.addStatement("$N.add($L)", listName, parameterLiteral);

    block.endControlFlow();

    return literal("$N", listName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral,
                                              Map<ClassName, FieldSpec> typeAdapters) {
    // Write size
    String listSize = getName() + "Size";
    block.addStatement("$T $N = $L.size()", int.class, listSize, sourceLiteral);
    block.addStatement("$N.writeInt($N)", dest, listSize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, listSize, indexName);

    TypeName parameterTypeName = typeArgument.getTypeName();
    String parameterItemName = getName() + "Item";

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    block.addStatement("$T $N = $L.get($N)", parameterTypeName, parameterItemName, sourceLiteral, indexName);

    CodeBlock parameterSource = literal("$N", parameterItemName);

    // Write in the parameter
    typeArgument.writeToParcel(block, dest, parameterSource, typeAdapters);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return typeArgument.requiresClassLoader();
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    return typeArgument.requiredTypeAdapters();
  }
}
