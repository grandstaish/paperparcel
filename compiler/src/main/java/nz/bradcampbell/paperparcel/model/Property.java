package nz.bradcampbell.paperparcel.model;

import static nz.bradcampbell.paperparcel.utils.StringUtils.capitalizeFirstCharacter;
import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

import android.os.Parcel;

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
  private final String name;

  private boolean isVisible = false;
  private int constructorPosition = -1;
  private String getterMethodName = null;
  private String setterMethodName = null;

  /**
   * Constructor.
   *
   * @param isNullable True if the property can be null, false otherwise
   * @param typeName The property TypeName
   * @param name The name of the accessor method on the data object
   */
  public Property(boolean isNullable, TypeName typeName, String name) {
    this.isNullable = isNullable;
    this.typeName = typeName;
    this.name = name;
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

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  public void setConstructorPosition(int constructorPosition) {
    this.constructorPosition = constructorPosition;
  }

  public void setGetterMethodName(String getterMethodName) {
    this.getterMethodName = getterMethodName;
  }

  public void setSetterMethodName(String setterMethodName) {
    this.setterMethodName = setterMethodName;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public int getConstructorPosition() {
    return constructorPosition;
  }

  public String getGetterMethodName() {
    return getterMethodName;
  }

  public String getSetterMethodName() {
    return setterMethodName;
  }

  /**
   * Subclasses should override this if they require a class loader, or if they have child properties
   * that might require class loaders.
   *
   * @return True if this property requires a class loader
   */
  public boolean requiresClassLoader() {
    return false;
  }

  /**
   * Subclasses should override this if they require a type adapter, or if they have child properties
   * that might require type adapters.
   *
   * @return A set of all the required type adapters for this property
   */
  public Set<Adapter> requiredTypeAdapters() {
    return Collections.emptySet();
  }

  /**
   * Generates a CodeBlock object that can read this property from the given Parcel parameter. This handles checks
   * if the property is nullable.
   *
   * @param block The code block for the read method
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data, or null if not required
   * @param typeAdaptersMap A Map of Types to TypeAdapters
   * @param scopedVariableNames All variable names that have been used in the current scope
   */
  public final CodeBlock readFromParcel(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                        Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {
    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    String defaultName = getUniqueName(name, scopedVariableNames);
    String nullableName = getUniqueName("out" + capitalizeFirstCharacter(defaultName), scopedVariableNames);

    CodeBlock defaultLiteral = CodeBlock.of("$N", defaultName);
    CodeBlock nullableLiteral = CodeBlock.of("$N", nullableName);

    if (isNullable) {
      block.addStatement("$T $L = null", typeName, nullableLiteral);
      block.beginControlFlow("if ($N.readInt() == 0)", in);
    }

    CodeBlock literal = readFromParcelInner(block, in, classLoader, typeAdaptersMap, scopedVariableNames);
    boolean alreadyDefined = defaultLiteral.toString().equals(literal.toString());

    CodeBlock result;
    if (isNullable) {
      block.addStatement("$L = $L", nullableLiteral, literal);
      result = nullableLiteral;

      // Add nullableName to scoped names
      scopedVariableNames.add(nullableName);

    } else if (!alreadyDefined) {
      block.addStatement("$T $L = $L", typeName, defaultLiteral, literal);
      result = defaultLiteral;

      // Add defaultName to scoped names
      scopedVariableNames.add(defaultName);

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
   *
   * If any manual writing to the "block" is needed, ensure to keep "scopedVariableNames" up to date
   * with any new variables added to the method body.
   *
   * This method must return the un-parcelled value, e.g.
   *
   * <pre><code>
   *   return CodeBlock.of("$N.readInt()", in);
   * </code></pre>
   *
   * or alternatively, if pre-processing is required; you have to create the instance (with the name
   * returned by {@link #getName()}), and then return a {@link CodeBlock} which is just simply the
   * instance, e.g.:
   *
   * <pre><code>
   *   block.addStatement("$1T $2N = new $1T(), SomeType.class, getName());
   *   // ... processing
   *   return CodeBlock.of("$N", getName());
   * </code></pre>
   *
   * @param block The CodeBlock builder to write the code to
   * @param in The Parcel parameter
   * @param classLoader ClassLoader to use for reading data, or null if not required
   * @param typeAdaptersMap A Map of Types to TypeAdapters
   * @param scopedVariableNames All variable names that have been used in the current scope
   */
  protected abstract CodeBlock readFromParcelInner(
      CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames);

  /**
   * Generates a CodeBlock object that can be used to write the property to the given parcel. This handles checks
   * if the property is nullable.
   *
   * @param block The code block for the write method
   * @param dest The Parcel parameter
   * @param flags The flags passed in from {@link android.os.Parcelable#writeToParcel(Parcel, int)}
   * @param sourceLiteral The source to be written
   * @param typeAdaptersMap A Map of Types to TypeAdapters
   * @param scopedVariableNames All variable names that have been used in the current scope
   */
  public final void writeToParcel(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames) {

    if (isNullable) {
      block.beginControlFlow("if ($L == null)", sourceLiteral);
      block.addStatement("$N.writeInt(1)", dest);
      block.nextControlFlow("else");
      block.addStatement("$N.writeInt(0)", dest);
    }

    writeToParcelInner(block, dest, flags, sourceLiteral, typeAdaptersMap, scopedVariableNames);

    if (isNullable) {
      block.endControlFlow();
    }
  }

  /**
   * Generates code to write the property to the given parcel.
   *
   * If any manual writing to the "block" is needed, ensure to keep "scopedVariableNames" up to date
   * with any new variables added to the method body.
   *
   * @param block The CodeBlock builder to write the code to
   * @param dest The Parcel parameter
   * @param flags The flags passed in from {@link android.os.Parcelable#writeToParcel(Parcel, int)}
   * @param sourceLiteral The source to be written
   * @param typeAdaptersMap A Map of Types to TypeAdapters
   * @param scopedVariableNames All variable names that have been used in the current scope
   */
  protected abstract void writeToParcelInner(
      CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags, CodeBlock sourceLiteral,
      Map<ClassName, CodeBlock> typeAdaptersMap, Set<String> scopedVariableNames);
}
