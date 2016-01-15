package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Property;
import nz.bradcampbell.dataparcel.internal.Properties;

public class ParcelableArrayProperty extends Property {
  public ParcelableArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    Property.Type propertyType = getPropertyType();
    ArrayTypeName wrappedTypeName = (ArrayTypeName) getPropertyType().getWrappedTypeName();
    if (propertyType.isParcelable()) {
      block.addStatement("$N = ($T) $N.readParcelableArray($N)", getName(), wrappedTypeName, in, classLoader);
    } else {
      block.addStatement("$T $N = ($T) $N.readParcelableArray($N)", wrappedTypeName, getWrappedName(), wrappedTypeName,
          in, classLoader);
      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();
    if (propertyType.isParcelable()) {
      super.unparcelVariable(block);
      return;
    }

    String variableName = getName();
    String wrappedVariableName = getWrappedName();

    ArrayTypeName typeName = (ArrayTypeName) getPropertyType().getTypeName(false);
    ArrayTypeName wrappedTypeName = (ArrayTypeName) getPropertyType().getWrappedTypeName();

    block.addStatement("$N = new $T[$N.length]", variableName, typeName.componentType, wrappedVariableName);

    String indexName = variableName + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", indexName, indexName, wrappedVariableName, indexName);

    Property.Type componentType = propertyType.getChildType(0);
    String innerName = "_" + variableName;
    String innerWrappedName = "_" + wrappedVariableName;
    block.addStatement("$T $N = null", componentType.getTypeName(false), innerName);
    block.addStatement("$T $N = $N[$N]", wrappedTypeName.componentType, innerWrappedName, wrappedVariableName, indexName);
    Properties.createProperty(componentType, false, innerName).unparcelVariable(block);
    block.addStatement("$N[$N] = $N", variableName, indexName, innerName);

    block.endControlFlow();
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeParcelableArray($N, 0)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source, boolean wildcard) {
    Property.Type propertyType = getPropertyType();
    String variableName = super.generateParcelableVariable(block, source, wildcard);

    if (!propertyType.isParcelable()) {

      ArrayTypeName wrappedTypeName = (ArrayTypeName) getPropertyType().getWrappedTypeName();
      String wrappedVariableName = getWrappedName();

      block.addStatement("$T $N = new $T[$N.length]", wrappedTypeName, wrappedVariableName,
          wrappedTypeName.componentType, variableName);

      String indexName = variableName + "Index";
      block.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", indexName, indexName, variableName, indexName);

      String innerName = "_" + variableName;
      String innerSource = variableName + "[" + indexName + "]";
      String wrappedInnerName = Properties.createProperty(propertyType.getChildType(0), false, innerName)
          .generateParcelableVariable(block, innerSource, false);

      block.addStatement("$N[$N] = $N", wrappedVariableName, indexName, wrappedInnerName);

      block.endControlFlow();

      variableName = wrappedVariableName;
    }

    return variableName;
  }

  @Override public boolean requiresClassLoader() {
    return true;
  }
}
