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

import com.google.common.base.Optional;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static paperparcel.Constants.TYPE_ADAPTER_CLASS_NAME;

/** A validator for custom adapters annotated with {@link RegisterAdapter} */
final class RegisterAdapterValidator {
  private final Elements elements;
  private final Types types;

  RegisterAdapterValidator(
      Elements elements,
      Types types) {
    this.elements = elements;
    this.types = types;
  }

  ValidationReport<TypeElement> validate(TypeElement element) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(element);
    TypeMirror erasedTypeAdapterType =
        types.erasure(elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME).asType());
    if (!types.isAssignable(element.asType(), erasedTypeAdapterType)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_NON_TYPE_ADAPTER);
    }
    if (element.getKind() == ElementKind.INTERFACE) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_INTERFACE);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_ABSTRACT_CLASS);
    }
    List<? extends TypeMirror> typeArguments = Utils.getTypeArgumentsOfTypeFromType(
        types, element.asType(), erasedTypeAdapterType);
    if (typeArguments == null || typeArguments.size() == 0) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_RAW_TYPE_ADAPTER);
    }
    Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
    if (mainConstructor.isPresent()) {
      builder.addSubreport(validateConstructor(
          erasedTypeAdapterType, types, mainConstructor.get()));
    } else if (!Utils.isSingleton(types, element)) {
      builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
    }
    return builder.build();
  }

  private ValidationReport<ExecutableElement> validateConstructor(
      TypeMirror adapterInterfaceType, Types types, ExecutableElement constructor) {
    ValidationReport.Builder<ExecutableElement> constructorReport = ValidationReport.about(constructor);
    for (VariableElement parameter : constructor.getParameters()) {
      TypeMirror parameterType = parameter.asType();
      if (!types.isAssignable(parameterType, adapterInterfaceType)) {
        constructorReport.addError(ErrorMessages.INVALID_TYPE_ADAPTER_CONSTRUCTOR);
      }
      List<? extends TypeMirror> typeArguments = Utils.getTypeArgumentsOfTypeFromType(
          types, parameterType, adapterInterfaceType);
      if (typeArguments == null || typeArguments.size() == 0) {
        constructorReport.addError(ErrorMessages.RAW_TYPE_ADAPTER_IN_CONSTRUCTOR, parameter);
      }
    }
    return constructorReport.build();
  }
}
