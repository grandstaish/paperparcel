package nz.bradcampbell.dataparcel.internal;

import android.support.annotation.Nullable;
import com.squareup.javapoet.*;

import java.util.List;

import static com.squareup.javapoet.TypeName.OBJECT;
import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;

/**
 * A model object that can generate a code block for both reading and writing itself to/from a Parcel
 */
public abstract class Property {
  private final static Type NO_TYPE = new Type(null, OBJECT, OBJECT, OBJECT, OBJECT, OBJECT, OBJECT, false, false);

  /**
   * A model object that holds all parsed information about the property type
   */
  public static final class Type {
    @Nullable private final List<Type> childTypes;
    private final TypeName parcelableTypeName;
    private final TypeName typeName;
    private final TypeName wrappedTypeName;
    private final TypeName wildcardTypeName;
    private final TypeName rawTypeName;
    private final TypeName wrappedRawTypeName;
    private final boolean isParcelable;
    private final boolean isInterface;

    public Type(@Nullable List<Type> childTypes, TypeName parcelableTypeName, TypeName typeName,
                TypeName wrappedTypeName, TypeName wildcardTypeName, TypeName rawTypeName, TypeName wrappedRawTypeName,
                boolean isParcelable, boolean isInterface) {

      this.childTypes = childTypes;
      this.parcelableTypeName = parcelableTypeName;
      this.typeName = typeName;
      this.wrappedTypeName = wrappedTypeName;
      this.wildcardTypeName = wildcardTypeName;
      this.rawTypeName = rawTypeName;
      this.wrappedRawTypeName = wrappedRawTypeName;
      this.isParcelable = isParcelable;
      this.isInterface = isInterface;
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

    public TypeName getTypeName(boolean includeWildcards) {
      return includeWildcards ? wildcardTypeName : typeName;
    }

    public TypeName getWrappedTypeName() {
      return wrappedTypeName;
    }

    public TypeName getRawTypeName() {
      return rawTypeName;
    }

    public TypeName getWrappedRawTypeName() {
      return wrappedRawTypeName;
    }

    public boolean isParcelable() {
      return isParcelable;
    }

    public boolean isInterface() {
      return isInterface;
    }
  }

  private final boolean isNullable;
  private final String name;
  private final String wrappedName;
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
    this.wrappedName = name + "Wrapped";
  }

  public final boolean isNullable() {
    return isNullable;
  }

  public final String getName() {
    return name;
  }

  public final String getWrappedName() {
    return wrappedName;
  }

  public final Type getPropertyType() {
    return propertyType;
  }

  /**
   * Generates a CodeBlock object that can read this property from the given Parcel parameter. This handles checks
   * if the property is nullable.
   *
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data
   * @return Code to read this property from the parcel
   */
  public final CodeBlock readFromParcel(ParameterSpec in, @Nullable FieldSpec classLoader) {
    CodeBlock.Builder block = CodeBlock.builder();

    TypeName typeName = propertyType.getTypeName(false);
    if (typeName.isPrimitive()) {
      block.addStatement("$T $N", typeName, getName());
    } else {
      block.addStatement("$T $N = null", typeName, getName());
    }

    if (isNullable()) {
      block.beginControlFlow("if ($N.readInt() == 0)", in);
    }

    readFromParcelInner(block, in, classLoader);

    if (isNullable()) {
      block.endControlFlow();
    }

    return block.build();
  }

  /**
   * Generates code to read the property from the given parcel. This method is convention based and assumes that
   * the variable with name returned by {@link #getName()} is already declared as null. Therefore, reading a variable
   * would only involve setting that variable to a value and not re-declaring it. e.g.: the code for reading a String
   * would be:
   *
   * block.addStatement("$N = $N.readString()", getName(), in);
   *
   * @param block The CodeBlock builder to write the code to
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data
   */
  protected abstract void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader);

  /**
   * May be called from within other properties to write code to translate the parceled version of this property into
   * the un-parceled version. By default, this method assumes that the parceled version and the un-parceled version
   * are the same, so it just assigns un-parceled to parceled. Properties that could include a data class as a "child
   * type" should override this method. This includes anything that has type parameters, or any array types.
   *
   * @param block The CodeBlock builder to write the code to
   */
  public void unparcelVariable(CodeBlock.Builder block) {
    block.addStatement("$N = $N", getName(), getWrappedName());
  }

  /**
   * Generates a CodeBlock object that can be used to write the property to the given parcel. This handles checks
   * if the property is nullable. This method will always call
   * {@link #generateParcelableVariable(CodeBlock.Builder, String, boolean)} before calling
   * {@link #writeToParcelInner(CodeBlock.Builder, ParameterSpec, String)} so that a variable will always be declared
   * for use in {@link #writeToParcelInner(CodeBlock.Builder, ParameterSpec, String)}.
   *
   * @param dest The Parcel parameter
   * @return The CodeBlock for writing the property
   */
  public final CodeBlock writeToParcel(ParameterSpec dest) {
    CodeBlock.Builder block = CodeBlock.builder();

    String source = DATA_VARIABLE_NAME + "." + getName() + "()";

    if (isNullable()) {
      block.beginControlFlow("if ($N == null)", source);
      block.addStatement("$N.writeInt(1)", dest);
      block.nextControlFlow("else");
      block.addStatement("$N.writeInt(0)", dest);
    }

    String variableName = generateParcelableVariable(block, source, true);
    writeToParcelInner(block, dest, variableName);

    if (isNullable()) {
      block.endControlFlow();
    }

    return block.build();
  }

  /**
   * Generates code to write the property to the given parcel. This method is convention based and assumes that
   * the variable with name variableName is already declared and populated, ready for the author
   * to use it for writing to the given Parcel.
   *
   * @param block The CodeBlock builder to write the code to
   * @param dest The Parcel parameter
   * @param variableName The name of the property
   */
  protected abstract void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName);

  /**
   * Always called from within {@link #writeToParcel(ParameterSpec)}, but may be called from within other properties
   * too. Writes code to translate the un-parceled version of this property into the parceled version. By default, this
   * method assumes that the parceled version and the given source are the same, so it just assigns parceled to source.
   * Properties that could include a data class as a "child type" should override this method. This includes anything
   * that has type parameters, or any array types.
   *
   * @param block The CodeBlock builder to write the code to
   * @param source code to access the non-parcelable variable
   * @return The generated parcelable variable name
   */
  public String generateParcelableVariable(CodeBlock.Builder block, String source, boolean includeWildcards) {
    String variableName = getName();
    TypeName typeName = propertyType.getTypeName(includeWildcards);
    block.addStatement("$T $N = $N", typeName, variableName, source);
    return variableName;
  }

  /**
   * @return True if this property type requires a ClassLoader instance passed into
   *         {@link #readFromParcelInner(CodeBlock.Builder, ParameterSpec, FieldSpec)}
   */
  public boolean requiresClassLoader() {
    return false;
  }
}
