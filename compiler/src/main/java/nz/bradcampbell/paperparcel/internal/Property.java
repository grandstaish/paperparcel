package nz.bradcampbell.paperparcel.internal;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;
import static nz.bradcampbell.paperparcel.internal.utils.StringUtils.capitalizeFirstCharacter;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A model object that can generate a code block for both reading and writing itself to/from a Parcel
 */
public abstract class Property {
  private final boolean isNullable;
  private final TypeName typeName;
  private final boolean isInterface;
  private final String name;
  @Nullable private final String accessorMethodName;

  /**
   * Constructor.
   *
   * todo:
   * @param isNullable True if the property can be null, false otherwise
   * @param name The name of the accessor method on the data object
   */
  public Property(boolean isNullable, TypeName typeName, boolean isInterface, String name,
                  @Nullable String accessorMethodName) {

    this.isNullable = isNullable;
    this.typeName = typeName;
    this.isInterface = isInterface;
    this.name = name;
    this.accessorMethodName = accessorMethodName;
  }

  public final String getName() {
    return name;
  }

  public final boolean isNullable() {
    return isNullable;
  }

  public final TypeName getTypeName() {
    return typeName;
  }

  public final boolean isInterface() {
    return isInterface;
  }

  @Nullable public final String getAccessorMethodName() {
    return accessorMethodName;
  }

  /**
   * TODO:
   * @return
   */
  public boolean requiresClassLoader() {
    return false;
  }

  /**
   * TODO:
   * @return
   */
  public Set<ClassName> requiredTypeAdapters() {
    return Collections.emptySet();
  }

  /**
   * Generates a CodeBlock object that can read this property from the given Parcel parameter. This handles checks
   * if the property is nullable.
   * TODO:
   *
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data
   */
  public final CodeBlock readFromParcel(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                        Map<ClassName, FieldSpec> typeAdapters) {
    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    CodeBlock defaultLiteral = literal("$N", name);
    CodeBlock nullableLiteral = literal("$N", "out" + capitalizeFirstCharacter(name));

    if (isNullable) {
      block.addStatement("$T $L = null", typeName, nullableLiteral);
      block.beginControlFlow("if ($N.readInt() == 0)", in);
    }

    CodeBlock literal = readFromParcelInner(block, in, classLoader, typeAdapters);
    boolean alreadyDefined = defaultLiteral.toString().equals(literal.toString());

    CodeBlock result;
    if (isNullable) {
      block.addStatement("$L = $L", nullableLiteral, literal);
      result = nullableLiteral;
    } else if (!alreadyDefined) {
      block.addStatement("$T $L = $L", typeName, defaultLiteral, literal);
      result = defaultLiteral;
    } else {
      result = defaultLiteral;
    }

    if (isNullable) {
      block.endControlFlow();
    }

    return result;
  }

  /**
   * Generates code to read the property from the given parcel.
   * TODO:
   *
   * @param block The CodeBlock builder to write the code to
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data
   */
  protected abstract CodeBlock readFromParcelInner(
      CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader, Map<ClassName, FieldSpec> typeAdapters);

  /**
   * Generates a CodeBlock object that can be used to write the property to the given parcel. This handles checks
   * if the property is nullable.
   * TODO:
   *
   * @param dest The Parcel parameter
   */
  public final void writeToParcel(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral,
                                  Map<ClassName, FieldSpec> typeAdapters) {
    if (isNullable) {
      block.beginControlFlow("if ($L == null)", sourceLiteral);
      block.addStatement("$N.writeInt(1)", dest);
      block.nextControlFlow("else");
      block.addStatement("$N.writeInt(0)", dest);
    }

    writeToParcelInner(block, dest, sourceLiteral, typeAdapters);

    if (isNullable) {
      block.endControlFlow();
    }
  }

  /**
   * Generates code to write the property to the given parcel.
   * TODO:
   *
   * @param block The CodeBlock builder to write the code to
   * @param dest The Parcel parameter
   */
  protected abstract void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral,
                                             Map<ClassName, FieldSpec> typeAdapters);
}
