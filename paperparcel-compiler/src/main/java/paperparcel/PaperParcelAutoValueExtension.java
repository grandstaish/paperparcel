/*
 * Copyright (C) 2016 Bradley Campbell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paperparcel;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(AutoValueExtension.class)
public class PaperParcelAutoValueExtension extends AutoValueExtension {
  private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";
  private static final TypeName PARCEL = ClassName.get("android.os", "Parcel");

  @Override public boolean applicable(Context context) {
    ProcessingEnvironment env = context.processingEnvironment();
    Elements elements = env.getElementUtils();
    Types types = env.getTypeUtils();
    Messager messager = env.getMessager();
    TypeMirror parcelable = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
    TypeElement autoValueTypeElement = context.autoValueClass();
    if (types.isAssignable(autoValueTypeElement.asType(), parcelable)) {
      AutoValueExtensionValidator extensionValidator =
          new AutoValueExtensionValidator(elements, types);
      ValidationReport<TypeElement> report = extensionValidator.validate(autoValueTypeElement);
      report.printMessagesTo(messager);
      return report.isClean();
    }
    return false;
  }

  @Override public Set<String> consumeProperties(Context context) {
    ImmutableSet.Builder<String> properties = new ImmutableSet.Builder<>();
    for (String property : context.properties().keySet()) {
      switch (property) {
        case "describeContents":
          properties.add(property);
          break;
      }
    }
    return properties.build();
  }

  @Override public Set<ExecutableElement> consumeMethods(Context context) {
    ImmutableSet.Builder<ExecutableElement> methods = new ImmutableSet.Builder<>();
    for (ExecutableElement element : context.abstractMethods()) {
      switch (element.getSimpleName().toString()) {
        case "writeToParcel":
          methods.add(element);
          break;
      }
    }
    return methods.build();
  }

  @Override public boolean mustBeFinal(Context context) {
    return true;
  }

  @Override public String generateClass(
      Context context, String simpleName, String classToExtend, boolean isFinal) {
    ClassName className = ClassName.get(context.packageName(), simpleName);
    TypeSpec.Builder subclass = TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC, FINAL)
        .superclass(TypeVariableName.get(classToExtend))
        .addMethod(constructor(context))
        .addAnnotation(PaperParcel.class)
        .addField(creator(className))
        .addMethod(writeToParcel(className));
    if (needsContentDescriptor(context)) {
      subclass.addMethod(describeContents());
    }
    return JavaFile.builder(context.packageName(), subclass.build())
        .build()
        .toString();
  }

  private MethodSpec writeToParcel(ClassName className) {
    ClassName parcelableImplClassName =
        ClassName.get(className.packageName(), "PaperParcel" + className.simpleName());
    return MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(PARCEL, "dest")
        .addParameter(int.class, "flags")
        .addStatement("$T.writeToParcel(this, dest, flags)", parcelableImplClassName)
        .build();
  }

  private FieldSpec creator(ClassName className) {
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, className);
    ClassName parcelableImplClassName =
        ClassName.get(className.packageName(), "PaperParcel" + className.simpleName());
    return FieldSpec.builder(creatorOfClass, "CREATOR", PUBLIC, FINAL, STATIC)
        .initializer("$T.CREATOR", parcelableImplClassName)
        .build();
  }

  private MethodSpec constructor(Context context) {
    final Types types = context.processingEnvironment().getTypeUtils();
    final DeclaredType declaredValueType = MoreTypes.asDeclared(context.autoValueClass().asType());
    ImmutableList<ParameterSpec> parameters = FluentIterable.from(context.properties().entrySet())
        .transform(new Function<Map.Entry<String, ExecutableElement>, ParameterSpec>() {
          @Override public ParameterSpec apply(Map.Entry<String, ExecutableElement> entry) {
            ExecutableType resolvedExecutableType =
                MoreTypes.asExecutable(types.asMemberOf(declaredValueType, entry.getValue()));
            TypeName typeName = TypeName.get(resolvedExecutableType.getReturnType());
            return ParameterSpec.builder(typeName, entry.getKey()).build();
          }
        })
        .toList();
    CodeBlock parameterList = CodeBlocks.join(FluentIterable.from(parameters)
        .transform(new Function<ParameterSpec, CodeBlock>() {
          @Override public CodeBlock apply(ParameterSpec parameterSpec) {
            return CodeBlock.of("$N", parameterSpec.name);
          }
        }), ", ");
    return MethodSpec.constructorBuilder()
        .addParameters(parameters)
        .addStatement("super($L)", parameterList)
        .build();
  }

  private static boolean needsContentDescriptor(Context context) {
    ProcessingEnvironment env = context.processingEnvironment();
    TypeElement autoValueTypeElement = context.autoValueClass();
    Elements elements = env.getElementUtils();
    ImmutableSet<ExecutableElement> methods =
        MoreElements.getLocalAndInheritedMethods(autoValueTypeElement, elements);
    for (ExecutableElement element : methods) {
      if (element.getSimpleName().contentEquals("describeContents")
          && MoreTypes.isTypeOf(int.class, element.getReturnType())
          && element.getParameters().isEmpty()
          && !element.getModifiers().contains(ABSTRACT)) {
        return false;
      }
    }
    return true;
  }

  private MethodSpec describeContents() {
    return MethodSpec.methodBuilder("describeContents")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("return 0")
        .build();
  }
}
