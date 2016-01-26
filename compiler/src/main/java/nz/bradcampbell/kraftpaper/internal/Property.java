package nz.bradcampbell.kraftpaper.internal;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.squareup.javapoet.TypeName.OBJECT;
import static nz.bradcampbell.kraftpaper.internal.Utils.capitalizeFirstCharacter;
import static nz.bradcampbell.kraftpaper.internal.Utils.literal;

/**
 * A model object that can generate a code block for both reading and writing itself to/from a Parcel
 */
public abstract class Property {
  private final static Type NO_TYPE = new Type(null, OBJECT, OBJECT, OBJECT, OBJECT, false, false, null);

  /**
   * A model object that holds all parsed information about the property type
   */
  public static final class Type {

    @Nullable private final List<Type> childTypes;

    private final TypeName parcelableTypeName;
    private final TypeName typeName;
    private final TypeName wrappedTypeName;
    private final TypeName wildcardTypeName;

    private final boolean isInterface;
    private final boolean requiresClassLoader;

    private final TypeName typeAdapter;

    public Type(@Nullable List<Type> childTypes, TypeName parcelableTypeName, TypeName typeName,
                TypeName wrappedTypeName, TypeName wildcardTypeName, boolean isInterface, boolean requiresClassLoader,
                @Nullable TypeName typeAdapter) {

      this.childTypes = childTypes;

      this.parcelableTypeName = parcelableTypeName;
      this.typeName = typeName;
      this.wrappedTypeName = wrappedTypeName;
      this.wildcardTypeName = wildcardTypeName;

      this.isInterface = isInterface;
      this.requiresClassLoader = requiresClassLoader;

      this.typeAdapter = typeAdapter;
    }

    public Type getChildType(int index) {
      if (childTypes == null || index > childTypes.size()) {
        return NO_TYPE;
      }
      return childTypes.get(index);
    }

    public TypeName getParcelableTypeName() {
      return parcelableTypeName;
    }

    public TypeName getTypeName() {
      return typeName;
    }

    public TypeName getWrappedTypeName() {
      return wrappedTypeName;
    }

    public TypeName getWildcardTypeName() {
      return wildcardTypeName;
    }

    public boolean isInterface() {
      return isInterface;
    }

    public boolean requiresClassLoader() {
      return requiresClassLoader;
    }

    @Nullable public TypeName getTypeAdapter() {
      return typeAdapter;
    }
  }

  private final boolean isNullable;
  private final String name;
  private final Type propertyType;

  /**
   * Constructor.
   *
   * @param propertyType The type information for this property
   * @param isNullable True if the property can be null, false otherwise
   * @param name The name of the accessor method on the data object
   */
  public Property(Type propertyType, boolean isNullable, String name) {
    this.propertyType = propertyType;
    this.isNullable = isNullable;
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final Type getPropertyType() {
    return propertyType;
  }

  /**
   * Generates a CodeBlock object that can read this property from the given Parcel parameter. This handles checks
   * if the property is nullable.
   * TODO:
   *
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data
   */
  public final CodeBlock readFromParcel(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName typeName = propertyType.getTypeName();
    CodeBlock defaultLiteral = literal("$N", name);
    CodeBlock nullableLiteral = literal("$N", "out" + capitalizeFirstCharacter(name));

    if (isNullable) {
      block.addStatement("$T $L = null", typeName, nullableLiteral);
      block.beginControlFlow("if ($N.readInt() == 0)", in);
    }

    CodeBlock literal = readFromParcelInner(block, in, classLoader);
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
  protected abstract CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader);

  /**
   * Generates a CodeBlock object that can be used to write the property to the given parcel. This handles checks
   * if the property is nullable.
   * TODO:
   *
   * @param dest The Parcel parameter
   */
  public final void writeToParcel(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral) {
    if (isNullable) {
      block.beginControlFlow("if ($L == null)", sourceLiteral);
      block.addStatement("$N.writeInt(1)", dest);
      block.nextControlFlow("else");
      block.addStatement("$N.writeInt(0)", dest);
    }

    writeToParcelInner(block, dest, sourceLiteral);

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
  protected abstract void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, CodeBlock sourceLiteral);

  /**
   * @return True if this property type requires a ClassLoader instance passed into
   *         {@link #readFromParcelInner(CodeBlock.Builder, ParameterSpec, FieldSpec)}
   */
  public final boolean requiresClassLoader() {
    return propertyType.requiresClassLoader();
  }
}
