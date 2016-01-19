package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public final class Sources {

  public static CodeBlock literal(String literal, Object... args) {
    CodeBlock code = CodeBlock.builder().add(literal, args).build();

    // Validation
    return CodeBlock.builder().add("$L", code).build();
  }

  public static TypeName getRawTypeName(Property.Type type, boolean wrapped) {
    TypeName typeName = wrapped ? type.getWrappedTypeName() : type.getTypeName();
    while (typeName instanceof ParameterizedTypeName) {
      typeName = ((ParameterizedTypeName) typeName).rawType;
    }
    return typeName;
  }
}
