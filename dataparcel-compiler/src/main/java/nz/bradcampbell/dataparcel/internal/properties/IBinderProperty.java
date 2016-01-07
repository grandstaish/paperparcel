package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.TypeMirror;

public class IBinderProperty extends Property {
  public IBinderProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    block.addStatement("$N = ($T) $N.readStrongBinder()", getName(), getParcelableTypeName(), in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeStrongBinder($N)", dest, variableName);
  }
}
