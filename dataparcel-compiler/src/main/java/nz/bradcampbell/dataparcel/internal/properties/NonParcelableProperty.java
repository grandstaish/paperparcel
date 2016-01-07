package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.TypeMirror;

public class NonParcelableProperty extends Property {
  public NonParcelableProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName parcelableTypeName = getParcelableTypeName();
    block.addStatement("$T $N = ($T) $N.readParcelable(getClass().getClassLoader())", parcelableTypeName,
        getWrappedName(), parcelableTypeName, in);
    unparcelVariable(block);
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    block.addStatement("$N = $N.getContents()", getName(), getWrappedName());
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelable($N, 0)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    String variableName = getName();
    TypeName parcelableTypeName = getParcelableTypeName();
    block.addStatement("$T $N = $T.wrap($N)", parcelableTypeName, variableName, parcelableTypeName, source);
    return variableName;
  }
}
