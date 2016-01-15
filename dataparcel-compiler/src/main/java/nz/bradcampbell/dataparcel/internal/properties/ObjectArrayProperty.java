package nz.bradcampbell.dataparcel.internal.properties;

import android.support.annotation.Nullable;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.Properties;
import nz.bradcampbell.dataparcel.internal.Property;

public class ObjectArrayProperty extends Property {
  public ObjectArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader) {
    TypeName objectArrayClassName = ArrayTypeName.get(Object[].class);
    String objectArrayName = getWrappedName();
    block.addStatement("$T $N = $N.readArray($N)", objectArrayClassName, objectArrayName, in, classLoader);
    unparcelVariable(block);
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();

    Property.Type componentPropertyType = propertyType.getChildType(0);
    TypeName componentType = componentPropertyType.getTypeName(false);
    TypeName wrappedComponentType = componentPropertyType.getWrappedTypeName();
    TypeName rawComponentType = componentPropertyType.getRawTypeName();

    String variableName = getName();
    String wrappedVariableName = getWrappedName();

    block.addStatement("$N = new $T[$N.length]", variableName, rawComponentType, wrappedVariableName);

    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", indexName, indexName, wrappedVariableName, indexName);
    
    String innerName = "_" + variableName;
    String innerWrappedName = "_" + wrappedVariableName;
    block.addStatement("$T $N = null", componentType, innerName);
    block.addStatement("$T $N = ($T) $N[$N]", wrappedComponentType, innerWrappedName, wrappedComponentType, wrappedVariableName, indexName);
    Properties.createProperty(componentPropertyType, false, innerName).unparcelVariable(block);
    block.addStatement("$N[$N] = $N", variableName, indexName, innerName);

    block.endControlFlow();
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeArray($N)", dest, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source, boolean includeWildcards) {
    Property.Type propertyType = getPropertyType();
    String variableName = super.generateParcelableVariable(block, source, includeWildcards);

    if (!propertyType.isParcelable()) {

      TypeName wrappedTypeName = propertyType.getWrappedTypeName();
      TypeName wrappedComponentRawTypeName = propertyType.getChildType(0).getWrappedRawTypeName();
      String wrappedVariableName = getWrappedName();

      block.addStatement("$T $N = new $T[$N.length]", wrappedTypeName, wrappedVariableName,
          wrappedComponentRawTypeName, variableName);

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
