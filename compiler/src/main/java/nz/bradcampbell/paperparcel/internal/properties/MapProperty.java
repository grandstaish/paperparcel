package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MapProperty extends Property {
  private final Property keyProperty;
  private final Property valueProperty;

  public MapProperty(Property keyProperty, Property valueProperty, boolean isNullable, TypeName typeName,
                     boolean isInterface, String name, @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.keyProperty = keyProperty;
    this.valueProperty = valueProperty;
  }

  @Override protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
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

    // Read in the key. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock keyLiteral = keyProperty.readFromParcel(block, in, classLoader);

    // Read in the value. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    CodeBlock valueLiteral = valueProperty.readFromParcel(block, in, classLoader);

    // Add the parameter to the output map
    block.addStatement("$N.put($L, $L)", mapName, keyLiteral, valueLiteral);

    block.endControlFlow();

    return literal("$N", mapName);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
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

    // Write in the key. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    keyProperty.writeToParcel(block, dest, keySourceLiteral);

    // Write in the value. Set isNullable to true as I don't know how to tell if a parameter is
    // nullable or not. Kotlin can do this, Java can't.
    valueProperty.writeToParcel(block, dest, valueSourceLiteral);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return keyProperty.requiresClassLoader() || valueProperty.requiresClassLoader();
  }
}
