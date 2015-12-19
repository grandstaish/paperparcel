package nz.bradcampbell.dataparcel.internal.properties;

import com.google.common.base.Joiner;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.ClassNameUtils;
import nz.bradcampbell.dataparcel.internal.Property;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nz.bradcampbell.dataparcel.internal.PropertyCreator.isValidType;

public class ListProperty extends Property {
  private final String typeArgumentName;
  private final String typeArgumentParcel;
  private final boolean isValidArgument;
  private final TypeElement requiredParcel;

  public ListProperty(Types types, boolean isNullable, String name, VariableElement variableElement) {
    super(isNullable, name, variableElement);

    List<? extends TypeMirror> typeArguments = ((DeclaredType) variableElement.asType()).getTypeArguments();
    TypeMirror typeArgument = typeArguments != null ? typeArguments.get(0) : null;

    TypeName typeName = null;
    if (typeArgument != null) {
      typeName = ClassName.get(types.erasure(typeArgument));
    }

    isValidArgument = typeName == null || isValidType(typeName);

    String simpleName = typeName == null ? null : ((ClassName) typeName).simpleName();
    typeArgumentName = simpleName;
    typeArgumentParcel = simpleName == null ? null : simpleName + "Parcel";
    requiredParcel = isValidArgument ? null : (TypeElement) types.asElement(typeArgument);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    if (isValidArgument) {
      block.add("($T) $N.readArrayList(getClass().getClassLoader())", getTypeName(), in);
    } else {
      if (isNullable()) {
        block.addStatement("$T<$N> $N = null", ClassName.get(List.class), typeArgumentName, getName());
        block.beginControlFlow("if ($N.readInt() == 0)", in);
        block.add("$N = ", getName());
      } else {
        block.add("$T<$N> $N = ", ClassName.get(List.class), typeArgumentName, getName());
      }

      String wrappedName = getName() + "Wrapped";
      block.addStatement("new $T<$N>()", ClassName.get(ArrayList.class), typeArgumentName);
      block.addStatement("$T<$N> $N = $N.readArrayList(getClass().getClassLoader())", ClassName.get(List.class),
          typeArgumentParcel, wrappedName, in);
      block.beginControlFlow("for ($N val : $N)", typeArgumentParcel, wrappedName);
      block.addStatement("$N.add(val.getContents())", getName());
      block.unindent();
      block.add("}");

      block.add("\n");
      if (isNullable()) {
        block.endControlFlow();
      }
    }
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest) {
    if (isValidArgument) {
      block.add("$N.writeList(data.$N())", dest, getName());
    } else {
      String wrappedName = getName() + "Wrapped";
      block.addStatement("$T<$N> $N = data.$N()", ClassName.get(List.class), typeArgumentName, getName(),
          getName());
      block.addStatement("$T<$N> $N = new $T<$N>($N.size())", ClassName.get(List.class),
          typeArgumentParcel, wrappedName, ClassName.get(ArrayList.class), typeArgumentParcel, getName());
      block.beginControlFlow("for ($N val : $N)", typeArgumentName, getName());
      block.addStatement("$N.add($N.wrap(val))", wrappedName, typeArgumentParcel);
      block.endControlFlow();
      block.add("$N.writeList($N)", dest, wrappedName);
    }
  }

  @Override public List<TypeElement> requiredParcels() {
    return requiredParcel == null ? super.requiredParcels() : Collections.singletonList(requiredParcel);
  }

  @Override protected boolean useReadTemplate() {
    return isValidArgument;
  }
}
