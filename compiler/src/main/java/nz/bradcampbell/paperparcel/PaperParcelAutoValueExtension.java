package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@AutoService(AutoValueExtension.class)
public class PaperParcelAutoValueExtension extends AutoValueExtension {
  private static final TypeName PARCEL = ClassName.get("android.os", "Parcel");

  @Override public boolean applicable(Context context) {
    ProcessingEnvironment processingEnv = context.processingEnvironment();
    TypeMirror parcelable = processingEnv.getElementUtils().getTypeElement("android.os.Parcelable").asType();
    TypeMirror autoValueClass = context.autoValueClass().asType();
    return processingEnv.getTypeUtils().isAssignable(autoValueClass, parcelable);
  }

  @Override public Set<String> consumeProperties(Context context) {
    return Sets.newHashSet("describeContents", "writeToParcel");
  }

  @Override public boolean mustBeFinal(Context context) {
    return true;
  }

  @Override public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
    TypeSpec.Builder subclass = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.FINAL)
        .superclass(TypeVariableName.get(classToExtend))
        .addMethod(generateSuperConstructor(context.properties()))
        .addAnnotation(PaperParcel.class)
        .addField(generateCreator(context.packageName(), TypeName.get(context.autoValueClass().asType()), className,
            context.properties()))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(context.packageName(), className));

    JavaFile javaFile = JavaFile.builder(context.packageName(), subclass.build()).build();
    return javaFile.toString();
  }

  private MethodSpec generateWriteToParcel(String packageName, String className) {
    ParameterSpec dest = ParameterSpec
        .builder(PARCEL, "dest")
        .build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(int.class, "flags");

    TypeName wrappedTypeName = ClassName.get(packageName, className + "Parcel");

    String varName = "wrapped";
    CodeBlock code = CodeBlock.builder()
        .addStatement("$T $N = $T.wrap(this)", wrappedTypeName, varName, wrappedTypeName)
        .addStatement("$N.writeToParcel($N, 0)", varName, dest)
        .build();

    builder.addCode(code);

    return builder.build();
  }

  private FieldSpec generateCreator(String packageName, TypeName autoValueClass, String className, Map<String, ExecutableElement> properties) {
    ClassName thisClass = ClassName.get(packageName, className);

    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, autoValueClass);

    CodeBlock.Builder initializer = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, autoValueClass))
        .beginControlFlow("@$T public $T createFromParcel($T in)", ClassName.get(Override.class), autoValueClass, PARCEL);

    TypeName wrappedTypeName = ClassName.get(packageName, className + "Parcel");
    String name = "value";
    initializer.addStatement("$T $N = $T.CREATOR.createFromParcel(in).getContents()", autoValueClass, name, wrappedTypeName);

    List<String> params = new ArrayList<>(properties.size());
    for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
      String propertyName = entry.getKey();
      params.add(name + "." + propertyName + "()");
    }

    initializer.addStatement("return new $T(" + Joiner.on(", ").join(params) + ")", thisClass);

    initializer.endControlFlow()
        .beginControlFlow("@$T public $T[] newArray($T size)", ClassName.get(Override.class), autoValueClass, int.class)
        .addStatement("return new $T[size]", thisClass)
        .endControlFlow()
        .unindent()
        .add("}");

    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(initializer.build())
        .build();
  }

  private MethodSpec generateSuperConstructor(Map<String, ExecutableElement> properties) {
    List<ParameterSpec> params = Lists.newArrayListWithCapacity(properties.size());

    for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
      TypeName propertyType = TypeName.get(entry.getValue().getReturnType());
      String propertyName = entry.getKey();
      params.add(ParameterSpec.builder(propertyType, propertyName).build());
    }

    MethodSpec.Builder builder = MethodSpec.constructorBuilder()
        .addParameters(params);

    StringBuilder superFormat = new StringBuilder("super(");
    List<ParameterSpec> args = new ArrayList<>();
    for (int i = 0, n = params.size(); i < n; i++) {
      args.add(params.get(i));
      superFormat.append("$N");
      if (i < n - 1) superFormat.append(", ");
    }
    superFormat.append(")");
    builder.addStatement(superFormat.toString(), args.toArray());

    return builder.build();
  }

  private MethodSpec generateDescribeContents() {
    return MethodSpec.methodBuilder("describeContents")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("return 0")
        .build();
  }
}
