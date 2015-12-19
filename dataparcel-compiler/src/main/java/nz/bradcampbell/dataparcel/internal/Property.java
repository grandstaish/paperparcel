package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.*;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.List;

public abstract class Property {
  private final boolean isNullable;
  private final String name;
  private final TypeName typeName;
  private final VariableElement element;

  public Property(boolean isNullable, String name, VariableElement element) {
    this.isNullable = isNullable;
    this.name = name;
    this.typeName = ClassName.get(element.asType());
    this.element = element;
  }

  public boolean isNullable() {
    return isNullable;
  }

  public String getName() {
    return name;
  }

  public TypeName getTypeName() {
    return typeName;
  }

  public VariableElement getElement() {
    return element;
  }

  public CodeBlock readFromParcel(ParameterSpec in) {
    CodeBlock.Builder block = CodeBlock.builder();

    if (useReadTemplate()) {
      if (isNullable) {
        block.addStatement("$T $N = null", typeName, name);
        block.beginControlFlow("if ($N.readInt() == 0)", in);
        block.add("$N = ", name);
      } else {
        block.add("$T $N = ", typeName, name);
      }
    }

    readFromParcelInner(block, in);

    if (useReadTemplate()) {
      block.add(";\n");
      if (isNullable) {
        block.endControlFlow();
      }
    }

    return block.build();
  }

  protected abstract void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in);

  public CodeBlock writeToParcel(ParameterSpec dest) {
    CodeBlock.Builder block = CodeBlock.builder();

    if (useWriteTemplate()) {
      if (isNullable) {
        block.beginControlFlow("if (data.$N() == null)", name);
        block.addStatement("$N.writeInt(1)", dest);
        block.nextControlFlow("else");
        block.addStatement("$N.writeInt(0)", dest);
      }
    }

    writeToParcelInner(block, dest);

    if (useWriteTemplate()) {
      block.add(";\n");
      if (isNullable) {
        block.endControlFlow();
      }
    }

    return block.build();
  }

  protected abstract void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest);

  protected boolean useReadTemplate() {
    return true;
  }

  protected boolean useWriteTemplate() {
    return true;
  }

  public List<TypeElement> requiredParcels() {
    return Collections.emptyList();
  }
}
