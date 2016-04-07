package nz.bradcampbell.paperparcel;

import static nz.bradcampbell.paperparcel.utils.PropertyUtils.literal;
import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

public class ClassInitializer {
  public static class Field {
    private final String name;
    private final CodeBlock value;

    public Field(String name, CodeBlock value) {
      this.name = name;
      this.value = value;
    }

    public String name() {
      return name;
    }

    public CodeBlock value() {
      return value;
    }
  }

  public CodeBlock initialize(TypeName typeName, CodeBlock.Builder block, List<Field> fields,
                              Set<String> scopedVariableNames) {
    String initializer;
    TypeName rawTypeName;

    if (typeName instanceof ParameterizedTypeName) {
      rawTypeName = ((ParameterizedTypeName) typeName).rawType;
      initializer = "$T $N = new $T<>(";
    } else {
      rawTypeName = typeName;
      initializer = "$T $N = new $T(";
    }

    String fieldName = getUniqueName(PaperParcelProcessor.DATA_VARIABLE_NAME, scopedVariableNames);

    int paramsOffset = 3;
    Object[] params = new Object[fields.size() + paramsOffset];
    params[0] = typeName;
    params[1] = fieldName;
    params[2] = rawTypeName;

    for (int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i);
      params[i + paramsOffset] = field.value();
      initializer += "$L";
      if (i != fields.size() - 1) {
        initializer += ", ";
      }
    }

    initializer += ")";
    block.addStatement(initializer, params);
    return literal("$N", fieldName);
  }
}
