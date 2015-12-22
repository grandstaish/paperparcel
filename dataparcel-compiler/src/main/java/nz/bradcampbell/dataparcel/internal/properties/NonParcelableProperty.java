package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

import java.util.Collections;
import java.util.List;

import static nz.bradcampbell.dataparcel.DataParcelProcessor.DATA_VARIABLE_NAME;

public class NonParcelableProperty extends Property {
  private final String parcelableTypeName;
  private final TypeElement requiredParcel;

  public NonParcelableProperty(Types types, boolean isNullable, String name, VariableElement variableElement) {
    super(isNullable, name, variableElement);
    try {
      String simpleName = ((ClassName)ClassName.get(types.erasure(variableElement.asType()))).simpleName();
      parcelableTypeName = simpleName + "Parcel";
      requiredParcel = (TypeElement) types.asElement(variableElement.asType());
    } catch (Exception e) {
      throw new RuntimeException(" beep bop"+ variableElement.asType() , e);
    }
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    block.add("(($N) $N.readParcelable(getClass().getClassLoader())).getContents()", parcelableTypeName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest) {
    block.add("$N.writeParcelable($N.wrap($N.$N()), 0)", dest, parcelableTypeName, DATA_VARIABLE_NAME, getGetterMethodName());
  }

  @Override public List<TypeElement> requiredParcels() {
    return Collections.singletonList(requiredParcel);
  }
}
