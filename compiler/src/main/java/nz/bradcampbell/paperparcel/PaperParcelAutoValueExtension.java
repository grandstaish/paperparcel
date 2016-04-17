package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(AutoValueExtension.class)
public class PaperParcelAutoValueExtension extends AutoValueExtension {
  private static final TypeName PARCEL = ClassName.get("android.os", "Parcel");
  private static final TypeName PAPER_PARCELS = ClassName.get("nz.bradcampbell.paperparcel", "PaperParcels");

  @Override public boolean applicable(Context context) {
    ProcessingEnvironment processingEnv = context.processingEnvironment();
    TypeMirror parcelable = processingEnv.getElementUtils().getTypeElement("android.os.Parcelable").asType();
    TypeMirror autoValueClass = context.autoValueClass().asType();
    // Disallow manual implementation of the CREATOR instance
    VariableElement creator = findCreator(context);
    if (creator != null) {
      context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
          "Manual implementation of a static Parcelable.Creator<T> CREATOR field found when processing "
          + autoValueClass.toString() + ". Remove this so PaperParcel can automatically generate the implementation "
          + "for you.", creator);
    }
    // Disallow manual implementation of writeToParcel
    ExecutableElement writeToParcel = findWriteToParcel(context);
    if (writeToParcel != null) {
      context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
          "Manual implementation of Parcelable#writeToParcel(Parcel,int) found when processing "
          + autoValueClass.toString() + ". Remove this so PaperParcel can automatically generate the implementation "
          + "for you.", writeToParcel);
    }
    return processingEnv.getTypeUtils().isAssignable(autoValueClass, parcelable);
  }

  @Override public Set<String> consumeProperties(Context context) {
    return Sets.newHashSet("describeContents", "writeToParcel");
  }

  @Override public boolean mustBeFinal(Context context) {
    return true;
  }

  @Override public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
    ClassName thisClass = ClassName.get(context.packageName(), className);
    FieldSpec classLoader = generateClassLoaderField(thisClass);
    TypeSpec.Builder subclass = TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC, FINAL)
        .superclass(TypeVariableName.get(classToExtend))
        .addMethod(generateSuperConstructor(context.properties()))
        .addAnnotation(PaperParcel.class)
        .addField(classLoader)
        .addField(generateCreator(thisClass, classLoader))
        .addMethod(generateWriteToParcel());
    if (needsContentDescriptor(context)) {
      subclass.addMethod(generateDescribeContents());
    }
    JavaFile javaFile = JavaFile.builder(context.packageName(), subclass.build()).build();
    return javaFile.toString();
  }

  private MethodSpec generateWriteToParcel() {
    ParameterSpec dest = ParameterSpec.builder(PARCEL, "dest").build();
    ParameterSpec flags = ParameterSpec.builder(int.class, "flags").build();
    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(flags);
    CodeBlock code = CodeBlock.builder()
        .addStatement("$N.writeParcelable($T.wrap(this), $N)", dest, PAPER_PARCELS, flags)
        .build();
    builder.addCode(code);
    return builder.build();
  }

  private FieldSpec generateCreator(TypeName thisClass, FieldSpec classLoader) {
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, thisClass);
    CodeBlock.Builder initializer = CodeBlock.builder()
        .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, thisClass))
        .beginControlFlow("@$T public $T createFromParcel($T in)", ClassName.get(Override.class), thisClass, PARCEL);
    initializer.addStatement("return $T.unwrap(in.readParcelable($N))", PAPER_PARCELS, classLoader);
    initializer.endControlFlow()
        .beginControlFlow("@$T public $T[] newArray($T size)", ClassName.get(Override.class), thisClass, int.class)
        .addStatement("return new $T[size]", thisClass)
        .endControlFlow()
        .unindent()
        .add("}");
    return FieldSpec.builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, FINAL, Modifier.STATIC)
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

  private FieldSpec generateClassLoaderField(TypeName className) {
    if (className instanceof ParameterizedTypeName) {
      className = ((ParameterizedTypeName) className).rawType;
    }
    return FieldSpec.builder(ClassLoader.class, "CLASS_LOADER", Modifier.PRIVATE, FINAL, Modifier.STATIC)
        .initializer("$T.class.getClassLoader()", className)
        .build();
  }

  private MethodSpec generateDescribeContents() {
    return MethodSpec.methodBuilder("describeContents")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("return 0")
        .build();
  }

  private static boolean needsContentDescriptor(Context context) {
    ProcessingEnvironment env = context.processingEnvironment();
    for (ExecutableElement element : MoreElements.getLocalAndInheritedMethods(
        context.autoValueClass(), env.getElementUtils())) {
      if (element.getSimpleName().contentEquals("describeContents")
          && MoreTypes.isTypeOf(int.class, element.getReturnType())
          && element.getParameters().isEmpty()
          && !element.getModifiers().contains(Modifier.ABSTRACT)) {
        return false;
      }
    }
    return true;
  }

  private static ExecutableElement findWriteToParcel(Context context) {
    ProcessingEnvironment env = context.processingEnvironment();
    TypeMirror parcel = env.getElementUtils().getTypeElement("android.os.Parcel").asType();
    for (ExecutableElement element : MoreElements.getLocalAndInheritedMethods(
        context.autoValueClass(), env.getElementUtils())) {
      if (element.getSimpleName().contentEquals("writeToParcel")
          && MoreTypes.isTypeOf(void.class, element.getReturnType())
          && !element.getModifiers().contains(Modifier.ABSTRACT)) {
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.size() == 2
            && env.getTypeUtils().isSameType(parcel, parameters.get(0).asType())
            && MoreTypes.isTypeOf(int.class, parameters.get(1).asType())) {
          return element;
        }
      }
    }
    return null;
  }

  private static VariableElement findCreator(Context context) {
    ProcessingEnvironment env = context.processingEnvironment();
    Types typeUtils = env.getTypeUtils();
    Elements elementUtils = env.getElementUtils();
    TypeMirror creatorType = typeUtils.erasure(elementUtils.getTypeElement("android.os.Parcelable.Creator").asType());
    List<? extends Element> members = env.getElementUtils().getAllMembers(context.autoValueClass());
    for (VariableElement field : ElementFilter.fieldsIn(members)) {
      if (field.getSimpleName().contentEquals("CREATOR")
          && typeUtils.isSameType(creatorType, typeUtils.erasure(field.asType()))
          && field.getModifiers().contains(Modifier.STATIC)) {
        return field;
      }
    }
    return null;
  }
}
