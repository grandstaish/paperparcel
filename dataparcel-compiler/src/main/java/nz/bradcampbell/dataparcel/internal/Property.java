package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.*;

import javax.lang.model.type.TypeMirror;

import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;

public abstract class Property {
  private final boolean isNullable;
  private final String name;
  private final String wrappedName;
  private final TypeMirror typeMirror;
  private final TypeName originalTypeName;
  private final TypeName parcelableTypeName;
  private final boolean isParcelable;

  public Property(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    this.isNullable = isNullable;
    this.name = name;
    this.wrappedName = name + "Wrapped";
    this.typeMirror = typeMirror;
    this.originalTypeName = TypeName.get(typeMirror);
    this.parcelableTypeName = parcelableTypeName;
    this.isParcelable = this.originalTypeName.equals(parcelableTypeName);
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

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  public TypeName getOriginalTypeName() {
    return originalTypeName;
  }

  public TypeName getParcelableTypeName() {
    return parcelableTypeName;
  }

  public boolean isParcelable() {
    return isParcelable;
  }

  public CodeBlock readFromParcel(ParameterSpec in) {
    CodeBlock.Builder block = CodeBlock.builder();

    block.addStatement("$T $N = null", getOriginalTypeName(), getName());

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
    TypeName typeName = getParcelableTypeName();
    block.addStatement("$T $N = $N", typeName, variableName, source);
    return variableName;
  }
}
