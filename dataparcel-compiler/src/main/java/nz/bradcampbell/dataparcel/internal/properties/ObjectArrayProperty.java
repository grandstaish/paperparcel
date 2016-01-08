package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.type.TypeMirror;

public class ObjectArrayProperty extends Property {
  public ObjectArrayProperty(TypeMirror typeMirror, boolean isNullable, String name, TypeName parcelableTypeName) {
    super(typeMirror, isNullable, name, parcelableTypeName);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName objectArrayClassName = ArrayTypeName.get(Object[].class);
    TypeName componentType = ((ArrayTypeName) getOriginalTypeName()).componentType;
    String objectArrayName = getWrappedName();
    block.addStatement("$T $N = $N.readArray($T.class.getClassLoader())", objectArrayClassName, objectArrayName, in,
        componentType);
    String variableName = getName();
    block.addStatement("$N = new $T[$N.length]", variableName, componentType, objectArrayName);
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", indexName, indexName, objectArrayName, indexName);
    block.addStatement("$N[$N] = ($T) $N[$N]", variableName, indexName, componentType, objectArrayName, indexName);
    block.endControlFlow();
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeArray($N)", dest, variableName);
  }
}
