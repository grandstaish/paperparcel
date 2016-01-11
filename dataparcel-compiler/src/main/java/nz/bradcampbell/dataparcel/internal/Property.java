package nz.bradcampbell.dataparcel.internal;

import android.support.annotation.Nullable;
import com.squareup.javapoet.*;

import java.util.List;

import static com.squareup.javapoet.TypeName.OBJECT;
import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;

public abstract class Property {
  private final static Type NO_TYPE = new Type(null, OBJECT, OBJECT, OBJECT, false);

  public static final class Type {
    private final List<Type> typeArguments;
    private final TypeName parcelableTypeName;
    private final TypeName typeName;
    private final TypeName wrappedTypeName;
    private final boolean isParcelable;

    public Type(@Nullable List<Type> typeArguments, TypeName parcelableTypeName, TypeName typeName,
                TypeName wrappedTypeName, boolean isParcelable) {
      this.typeArguments = typeArguments;
      this.parcelableTypeName = parcelableTypeName;
      this.typeName = typeName;
      this.wrappedTypeName = wrappedTypeName;
      this.isParcelable = isParcelable;
    }

    public Type getTypeArgumentAtIndex(int index) {
      if (typeArguments == null || index > typeArguments.size()) {
        return NO_TYPE;
      }
      return typeArguments.get(index);
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

    public boolean isParcelable() {
      return isParcelable;
    }
  }

  private final boolean isNullable;
  private final String name;
  private final String wrappedName;
  private final Type propertyType;

  public Property(Type propertyType, boolean isNullable, String name) {
    this.propertyType = propertyType;
    this.isNullable = isNullable;
    this.name = name;
    this.wrappedName = name + "Wrapped";
  }

  public boolean isNullable() {
    return isNullable;
  }

  public String getName() {
    return name;
  }

  public String getWrappedName() {
    return wrappedName;
  }

  public Type getPropertyType() {
    return propertyType;
  }

  public CodeBlock readFromParcel(ParameterSpec in) {
    CodeBlock.Builder block = CodeBlock.builder();

    TypeName typeName = propertyType.getTypeName();
    if (typeName.isPrimitive()) {
      block.addStatement("$T $N", typeName, getName());
    } else {
      block.addStatement("$T $N = null", typeName, getName());
    }

    if (isNullable()) {
      block.beginControlFlow("if ($N.readInt() == 0)", in);
    }

    readFromParcelInner(block, in);

    if (isNullable()) {
      block.endControlFlow();
    }

    return block.build();
  }

  protected abstract void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in);

  public void unparcelVariable(CodeBlock.Builder block) {
    block.addStatement("$N = $N", getName(), getWrappedName());
  }

  public CodeBlock writeToParcel(ParameterSpec dest) {
    CodeBlock.Builder block = CodeBlock.builder();

    String source = DATA_VARIABLE_NAME + "." + getName() + "()";

    if (isNullable()) {
      block.beginControlFlow("if ($N == null)", source);
      block.addStatement("$N.writeInt(1)", dest);
      block.nextControlFlow("else");
      block.addStatement("$N.writeInt(0)", dest);
    }

    String variableName = generateParcelableVariable(block, source);
    writeToParcelInner(block, dest, variableName);

    if (isNullable()) {
      block.endControlFlow();
    }

    return block.build();
  }

  protected abstract void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName);

  public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    String variableName = getName();
    TypeName typeName = propertyType.getWrappedTypeName();
    block.addStatement("$T $N = $N", typeName, variableName, source);
    return variableName;
  }
}
