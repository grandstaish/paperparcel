package nz.bradcampbell.dataparcel.internal.properties;

import android.util.SparseArray;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

import static nz.bradcampbell.dataparcel.internal.Properties.createProperty;

public class SparseArrayProperty extends Property {
  public SparseArrayProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    Property.Type propertyType = getPropertyType();

    if (propertyType.isParcelable()) {

      TypeName parameterType = propertyType.getChildType(0).getTypeName();
      block.addStatement("$N = $N.readSparseArray($T.class.getClassLoader())", getName(), in, parameterType);

    } else {

      TypeName wrappedTypeName = propertyType.getWrappedTypeName();
      TypeName wrappedParameterType = propertyType.getChildType(0).getWrappedTypeName();
      String wrappedName = getWrappedName();

      block.addStatement("$T $N = $N.readSparseArray($T.class.getClassLoader())", wrappedTypeName, wrappedName, in,
          wrappedParameterType);

      unparcelVariable(block);
    }
  }

  @Override public void unparcelVariable(CodeBlock.Builder block) {
    Property.Type propertyType = getPropertyType();
    if (propertyType.isParcelable()) {

      super.unparcelVariable(block);

    } else {

      String variableName = getName();
      String wrappedName = getWrappedName();

      Property.Type parameterPropertyType = propertyType.getChildType(0);
      TypeName parameterType = parameterPropertyType.getTypeName();
      TypeName wrappedParameterType = parameterPropertyType.getWrappedTypeName();

      block.addStatement("$N = new $T()", variableName, propertyType.getTypeName());

      String innerWrappedName = "_" + wrappedName;
      String indexName = variableName + "Index";

      block.beginControlFlow("for (int $N = 0; $N < $N.size(); $N++)", indexName, indexName, wrappedName, indexName);

      String innerName = "_" + variableName;
      block.addStatement("$T $N = null", parameterType, innerName);

      String keyName = wrappedName + "Key";
      block.addStatement("int $N = $N.keyAt($N)", keyName, wrappedName, indexName);

      block.addStatement("$T $N = $N.get($N)", wrappedParameterType, innerWrappedName, wrappedName, keyName);

      createProperty(parameterPropertyType, true, innerName).unparcelVariable(block);

      block.addStatement("$N.put($N, $N)", variableName, keyName, innerName);

      block.endControlFlow();
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeSparseArray(($T) $N)", dest, SparseArray.class, variableName);
  }

  @Override public String generateParcelableVariable(CodeBlock.Builder block, String source) {
    String variableName = super.generateParcelableVariable(block, source);

    Property.Type propertyType = getPropertyType();
    if (!propertyType.isParcelable()) {
      String wrappedName = getWrappedName();

      TypeName wrappedTypeName = propertyType.getWrappedTypeName();
      block.addStatement("$T $N = new $T()", wrappedTypeName, wrappedName, wrappedTypeName);

      String indexName = variableName + "Index";

      block.beginControlFlow("for (int $N = 0; $N < $N.size(); $N++)", indexName, indexName, variableName, indexName);

      String keyName = variableName + "Key";
      block.addStatement("int $N = $N.keyAt($N)", keyName, variableName, indexName);

      String innerName = "_" + variableName;
      String innerSource = variableName + ".get(" + keyName + ")";

      Property.Type parameterPropertyType = propertyType.getChildType(0);
      String innerVariableName = createProperty(parameterPropertyType, true, innerName)
          .generateParcelableVariable(block, innerSource);

      block.addStatement("$N.put($N, $N)", wrappedName, keyName, innerVariableName);

      block.endControlFlow();

      variableName = wrappedName;
    }

    return variableName;
  }
}
