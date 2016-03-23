package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static nz.bradcampbell.paperparcel.PaperParcelProcessor.DATA_VARIABLE_NAME;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import nz.bradcampbell.paperparcel.internal.DataClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ParcelMappingGenerator {
  static final String PACKAGE_NAME = "nz.bradcampbell.paperparcel";

  public static JavaFile generateParcelableMapping(Set<DataClass> dataClasses) {
    TypeSpec.Builder wrapperBuilder =
        TypeSpec.classBuilder("PaperParcelMapping").addModifiers(PUBLIC, FINAL);

    addClassComment(wrapperBuilder);
    addMapFields(wrapperBuilder);
    addStaticInitializerForMaps(dataClasses, wrapperBuilder);

    return JavaFile.builder(PACKAGE_NAME, wrapperBuilder.build()).build();
  }

  private static void addClassComment(TypeSpec.Builder wrapperBuilder) {
    wrapperBuilder.addJavadoc("THIS CODE IS AUTO-GENERATED, DO NOT EDIT\n");
    wrapperBuilder.addJavadoc("<p>\n");
    wrapperBuilder.addJavadoc("Builds up mappings from data objects to their generated Parcel classes.\n");
    wrapperBuilder.addJavadoc("This code is used reflectively by {@link PaperParcels}.\n");
  }

  private static void addStaticInitializerForMaps(Set<DataClass> dataClasses,
      TypeSpec.Builder wrapperBuilder) {
    CodeBlock.Builder staticBlockBuilder = CodeBlock.builder();
    int index = 0;
    for (DataClass dataClass : dataClasses) {
      TypeName original = dataClass.getClassName();
      TypeName parcelable = dataClass.getWrapperClassName();
      String varName = "delegator" + index++;
      Class<PaperParcels.Delegator> delegator = PaperParcels.Delegator.class;
      staticBlockBuilder.add("$1T<$2T, $3T> $4N = new $1T<$2T, $3T>() {\n", delegator, original, parcelable, varName)
          .add("    @Override public $T unwrap($T wrapper) {\n", original, parcelable)
          .add("      return wrapper.$N;\n", DATA_VARIABLE_NAME)
          .add("    }\n")
          .add("    @Override public $T wrap($T object) {\n", parcelable, original)
          .add("      return new $T(object);\n", parcelable)
          .add("    }\n")
          .add("};\n")
          .add("FROM_ORIGINAL.put($T.class, $N);\n", original, varName)
          .add("FROM_PARCELABLE.put($T.class, $N);\n", parcelable, varName);
    }
    wrapperBuilder.addStaticBlock(staticBlockBuilder.build());
  }

  private static void addMapFields(TypeSpec.Builder wrapperBuilder) {
    FieldSpec fromOriginalClass = buildClassToDelegatorMap("FROM_ORIGINAL");
    FieldSpec fromParcelableClass = buildClassToDelegatorMap("FROM_PARCELABLE");
    wrapperBuilder.addField(fromOriginalClass).addField(fromParcelableClass);
  }

  private static FieldSpec buildClassToDelegatorMap(String name) {
    ParameterizedTypeName mapType =
        ParameterizedTypeName.get(Map.class, Class.class, PaperParcels.Delegator.class);
    return FieldSpec.builder(mapType, name, PRIVATE, STATIC, FINAL)
        .initializer("new $T<>()", LinkedHashMap.class)
        .build();
  }
}
