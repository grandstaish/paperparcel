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

import java.util.HashMap;
import java.util.HashSet;
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
                                          Map<ClassName, FieldSpec> typeAdapters) {
    // Read size
    String mapSize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, mapSize, in);

    // Create map to read into
    String mapName = getName();

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
      block.addStatement("$T $N = new $T<$T, $T>($N)", typeName, mapName, HashMap.class, keyTypeName, valueTypeName, mapSize);
    } else {
      block.addStatement("$T $N = new $T()", typeName, mapName, typeName);
    }

    // Write a loop to iterate through each entity
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, mapSize, indexName);

    // Read in the key
    CodeBlock keyLiteral = keyProperty.readFromParcel(block, in, classLoader, typeAdapters);

    // Read in the value
    CodeBlock valueLiteral = valueProperty.readFromParcel(block, in, classLoader, typeAdapters);

    // Add the parameter to the output map
    block.addStatement("$N.put($L, $L)", mapName, keyLiteral, valueLiteral);

    block.endControlFlow();

    return literal("$N", mapName);
  }

  @Override
  protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags,
                                    CodeBlock sourceLiteral, Map<ClassName, FieldSpec> typeAdapters) {
    // Write size
    block.addStatement("$N.writeInt($L.size())", dest, sourceLiteral);

    TypeName keyTypeName = keyProperty.getTypeName();
    TypeName valueTypeName = valueProperty.getTypeName();

    // Write a loop to iterate through each entry
    String entryName = getName() + "Entry";
    block.beginControlFlow("for ($T<$T, $T> $N : $L.entrySet())", Map.Entry.class, keyTypeName, valueTypeName,
                           entryName, sourceLiteral);

    CodeBlock keySourceLiteral = literal("$N.getKey()", entryName);
    CodeBlock valueSourceLiteral = literal("$N.getValue()", entryName);

    // Write in the key.
    keyProperty.writeToParcel(block, dest, flags, keySourceLiteral, typeAdapters);

    // Write in the value.
    valueProperty.writeToParcel(block, dest, flags, valueSourceLiteral, typeAdapters);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return keyProperty.requiresClassLoader() || valueProperty.requiresClassLoader();
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    Set<ClassName> combined = new HashSet<>();
    combined.addAll(keyProperty.requiredTypeAdapters());
    combined.addAll(valueProperty.requiredTypeAdapters());
    return combined;
  }
}
