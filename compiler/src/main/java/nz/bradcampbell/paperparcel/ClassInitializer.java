package nz.bradcampbell.paperparcel;

import static nz.bradcampbell.paperparcel.utils.StringUtils.capitalizeFirstCharacter;
import static nz.bradcampbell.paperparcel.utils.StringUtils.getUniqueName;

import com.squareup.javapoet.CodeBlock;
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
  }

  public CodeBlock initialize(TypeName typeName, CodeBlock.Builder block, List<Field> fields,
                              Set<String> scopedVariableNames, InitializationStrategy initializationStrategy) {
    String fieldName = getUniqueName(PaperParcelProcessor.DATA_VARIABLE_NAME, scopedVariableNames);
    switch (initializationStrategy) {
      case CONSTRUCTOR:
        generateConstructorInitialization(fieldName, typeName, block, fields);
        break;
      case SETTER:
        generateSetterInitialization(fieldName, typeName, block, fields);
        break;
    }
    return CodeBlock.of("$N", fieldName);
  }

  private void generateConstructorInitialization(
      String fieldName, TypeName typeName, CodeBlock.Builder block, List<Field> fields) {
    String initializer = "$1T $2N = new $1T(";
    int paramsOffset = 2;
    Object[] params = new Object[fields.size() + paramsOffset];
    params[0] = typeName;
    params[1] = fieldName;
    for (int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i);
      params[i + paramsOffset] = field.value;
      initializer += "$" + (i + paramsOffset + 1) + "L";
      if (i != fields.size() - 1) {
        initializer += ", ";
      }
    }
    initializer += ")";
    block.addStatement(initializer, params);
  }

  private void generateSetterInitialization(
      String fieldName, TypeName typeName, CodeBlock.Builder block, List<Field> fields) {
    block.addStatement("$1T $2N = new $1T()", typeName, fieldName);
    for (Field field : fields) {
      String setterName = "set" + capitalizeFirstCharacter(field.name);
      block.addStatement("$N.$N($L)", fieldName, setterName, field.value);
    }
  }
}
