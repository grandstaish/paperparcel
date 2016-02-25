package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import android.os.Parcelable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import nz.bradcampbell.paperparcel.internal.DataClass;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class CentralLookupGenerator {
  public static JavaFile generateParcelableLookup(Set<DataClass> dataClasses) {
    TypeSpec.Builder wrapperBuilder =
        TypeSpec.classBuilder("PaperParcels").addModifiers(PUBLIC, FINAL);
    TypeVariableName originalTypeVar = TypeVariableName.get("ORIG");
    TypeVariableName parcelableTypeVar = TypeVariableName.get("PARCELABLE", Parcelable.class);
    wrapperBuilder.addType(TypeSpec.interfaceBuilder("Delegator")
        .addTypeVariable(originalTypeVar)
        .addTypeVariable(parcelableTypeVar)
        .addMethod(MethodSpec.methodBuilder("unwrap")
            .addModifiers(ABSTRACT, PUBLIC)
            .returns(originalTypeVar)
            .addParameter(parcelableTypeVar, "parcelableObj")
            .build())
        .addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(ABSTRACT, PUBLIC)
            .returns(parcelableTypeVar)
            .addParameter(originalTypeVar, "originalObj")
            .build())
        .build());

    FieldSpec fromOriginalClass = buildClassToWrapperMap("FROM_ORIGINAL");
    FieldSpec fromParcelableClass = buildClassToWrapperMap("FROM_PARCELABLE");
    wrapperBuilder.addField(fromOriginalClass)
        .addField(fromParcelableClass);

    CodeBlock.Builder staticBlockBuilder = CodeBlock.builder();
    int index = 0;
    Set<String> packageNames = new LinkedHashSet<>();
    for (DataClass dataClass : dataClasses) {
      // Parameterized types don't need to be dynamically wrapped/unwrapped: ignore.
      TypeName original = dataClass.getClassName();
      if (original instanceof ParameterizedTypeName) {
        staticBlockBuilder.add("// Parameterized class ignored: $N\n", original.toString());
        continue;
      }
      // TODO Not sure what to do with autovalue...ignore for now.
      if (original instanceof ClassName
          && ((ClassName) original).simpleName().startsWith("AutoValue_")) {
        staticBlockBuilder.add("// AutoValue class ignored: $N\n", ((ClassName) original).simpleName());
        continue;
      }
      packageNames.add(dataClass.getClassPackage());
      TypeName parcelable = dataClass.getWrapperClassName();
      String varName = "delegator" + index++;
      staticBlockBuilder.add("Delegator<$T, $T> $N = new Delegator<$T, $T>() {\n", original, parcelable, varName, original, parcelable)
          .add("    @Override public $T unwrap($T wrapper) {\n", original, parcelable)
          .add("      return wrapper.getContents();\n")
          .add("    }\n")
          .add("    @Override public $T wrap($T object) {\n", parcelable, original)
          .add("      return $T.wrap(object);\n", parcelable)
          .add("    }\n")
          .add("};\n")
          .add("FROM_ORIGINAL.put($T.class, $N);\n", original, varName)
          .add("FROM_PARCELABLE.put($T.class, $N);\n", parcelable, varName);
    }
    wrapperBuilder.addStaticBlock(staticBlockBuilder.build());


    wrapperBuilder.addMethod(MethodSpec.methodBuilder("wrap")
        .addModifiers(PUBLIC, STATIC)
        .addTypeVariable(originalTypeVar)
        .addTypeVariable(parcelableTypeVar)
        .returns(parcelableTypeVar)
        .addParameter(originalTypeVar, "originalObj")
        .addCode("$T<?> type = originalObj.getClass();\n", Class.class)
        .addCode("$T<ORIG, PARCELABLE> delegator = FROM_ORIGINAL.get(type);\n", ClassName.bestGuess("Delegator"))
        .addCode("return delegator.wrap(originalObj);\n")
        .build());

    wrapperBuilder.addMethod(MethodSpec.methodBuilder("unwrap")
        .addModifiers(PUBLIC, STATIC)
        .addTypeVariable(originalTypeVar)
        .addTypeVariable(parcelableTypeVar)
        .returns(originalTypeVar)
        .addParameter(parcelableTypeVar, "parcelableObj")
        .addCode("$T<?> type = parcelableObj.getClass();\n", Class.class)
        .addCode("$T<ORIG, PARCELABLE> delegator = FROM_PARCELABLE.get(type);\n", ClassName.bestGuess("Delegator"))
        .addCode("return delegator.unwrap(parcelableObj);\n")
        .build());

    String packageName = findLowestCommonPackageName(packageNames);
    return JavaFile.builder(packageName, wrapperBuilder.build()).build();
  }

  static String findLowestCommonPackageName(Collection<String> packageNames) {
    String commonParts[] = null;
    for (String packageName : packageNames) {
      String[] parts = packageName.split("\\.");
      if (commonParts == null) {
        commonParts = parts;
      } else {
        commonParts = findCommonParts(commonParts, parts);
      }
    }
    if (commonParts == null) {
      return "";
    }
    StringBuilder packageName = new StringBuilder();
    for (int i = 0; i < commonParts.length; i++) {
      if (i != 0) {
        packageName.append(".");
      }
      packageName.append(commonParts[i]);
    }
    return packageName.toString();
  }

  private static String[] findCommonParts(String[] commonParts, String[] parts) {
    if (parts.length < commonParts.length) {
      commonParts = shrinkArray(commonParts, parts.length);
    }
    for (int i = 0; i < parts.length; i++) {
      if (i >= commonParts.length) {
        return commonParts;
      }
      if (!parts[i].equals(commonParts[i])) {
        commonParts = shrinkArray(commonParts, i);
      }
    }
    return commonParts;
  }

  private static String[] shrinkArray(String[] commonParts, int newSize) {
    String[] newCommonParts = new String[newSize];
    System.arraycopy(commonParts, 0, newCommonParts, 0, newSize);
    return newCommonParts;
  }

  private static FieldSpec buildClassToWrapperMap(String name) {
    ParameterizedTypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class),
        ClassName.get(Class.class), ClassName.bestGuess("Delegator"));
    return FieldSpec.builder(mapType, name, PRIVATE, STATIC, FINAL)
        .initializer("new $T<>()", LinkedHashMap.class)
        .build();
  }
}
