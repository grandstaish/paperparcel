package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import nz.bradcampbell.paperparcel.model.DataClass;

public class DelegateGenerator {
  private static final ClassName DELEGATE = ClassName.get(PaperParcels.Delegate.class);

  public JavaFile generatePaperParcelsDelegate(DataClass dataClass) {
    AnnotationSpec suppressWarningsSpec = AnnotationSpec.builder(SuppressWarnings.class)
        .addMember("value", CodeBlock.of("$S", "unused"))
        .build();
    TypeName delegateInterface = ParameterizedTypeName.get(
        DELEGATE, dataClass.getClassName(), dataClass.getWrapperClassName());
    TypeSpec delegateSpec = TypeSpec.classBuilder(dataClass.getDelegateClassName().simpleName())
        .addAnnotation(suppressWarningsSpec)
        .addModifiers(PUBLIC, FINAL)
        .addSuperinterface(delegateInterface)
        .addMethod(generateWrapMethod(dataClass.getClassName(), dataClass.getWrapperClassName()))
        .addMethod(generateNewArrayMethod(dataClass.getClassName()))
        .build();
    return JavaFile.builder(dataClass.getClassPackage(), delegateSpec)
        .build();
  }

  private MethodSpec generateNewArrayMethod(ClassName className) {
    return MethodSpec.methodBuilder("newArray")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(ArrayTypeName.of(className))
        .addParameter(int.class, "size")
        .addStatement("return new $T[size]", className)
        .build();
  }

  private MethodSpec generateWrapMethod(ClassName className, ClassName wrapperClassName) {
    return MethodSpec.methodBuilder("wrap")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(wrapperClassName)
        .addParameter(className, "original")
        .addStatement("return new $T(original)", wrapperClassName)
        .build();
  }
}
