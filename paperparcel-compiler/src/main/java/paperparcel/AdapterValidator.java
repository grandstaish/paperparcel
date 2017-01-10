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

import com.google.auto.common.Visibility;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.google.auto.common.MoreTypes.asDeclared;

/** A validator for custom adapters */
final class AdapterValidator {
  private final Elements elements;
  private final Types types;

  AdapterValidator(
      Elements elements,
      Types types) {
    this.elements = elements;
    this.types = types;
  }

  ValidationReport<TypeElement> validate(TypeElement element) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(element);

    // TODO(brad): remove this after @RegisterAdapter is deleted
    boolean isAdapter = Utils.isAdapterType(element, elements, types);
    if (!isAdapter) {
      builder.addError(String.format(
          ErrorMessages.ADAPTER_MUST_IMPLEMENT_TYPE_ADAPTER_INTERFACE,
          element.getQualifiedName().toString()));
    }
    if (element.getKind() != ElementKind.CLASS) {
      builder.addError(ErrorMessages.ADAPTER_MUST_BE_CLASS);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.ADAPTER_IS_ABSTRACT);
    }
    if (Visibility.ofElement(element) != Visibility.PUBLIC) {
      builder.addError(ErrorMessages.ADAPTER_MUST_BE_PUBLIC);
    } else if (Visibility.effectiveVisibilityOfElement(element) != Visibility.PUBLIC) {
      builder.addError(ErrorMessages.ADAPTER_VISIBILITY_RESTRICTED);
    }
    ElementKind enclosingKind = element.getEnclosingElement().getKind();
    if (enclosingKind.isClass() || enclosingKind.isInterface()) {
      if (!element.getModifiers().contains(Modifier.STATIC)) {
        builder.addError(ErrorMessages.NESTED_ADAPTER_MUST_BE_STATIC);
      }
    }

    if (isAdapter) {
      TypeMirror adaptedType = Utils.getAdaptedType(elements, types, asDeclared(element.asType()));
      if (Utils.isRawType(adaptedType)) {
        builder.addError(ErrorMessages.ADAPTER_TYPE_ARGUMENT_HAS_RAW_TYPE);
      } else if (Utils.containsWildcards(adaptedType)) {
        builder.addError(ErrorMessages.ADAPTER_TYPE_ARGUMENT_HAS_WILDCARDS);
      } else {
        List<TypeParameterElement> missingParameters = findMissingParameters(element, adaptedType);
        for (TypeParameterElement missingParameter : missingParameters) {
          builder.addError(
              String.format(
                  ErrorMessages.ADAPTER_TYPE_ARGUMENT_MISSING_PARAMETER,
                  missingParameter.getSimpleName().toString()),
              missingParameter);
        }
      }

      ExecutableElement mainConstructor = Utils.findLargestPublicConstructor(element);
      if (mainConstructor != null) {
        builder.addSubreport(validateConstructor(mainConstructor));
      } else if (adaptedType != null) {
        if (!Utils.isSingletonAdapter(elements, types, element, adaptedType)) {
          builder.addError(ErrorMessages.ADAPTER_MUST_HAVE_PUBLIC_CONSTRUCTOR);
        }
      }
    }

    return builder.build();
  }

  private ValidationReport<ExecutableElement> validateConstructor(ExecutableElement constructor) {
    ValidationReport.Builder<ExecutableElement> constructorReport =
        ValidationReport.about(constructor);
    for (VariableElement parameter : constructor.getParameters()) {
      boolean isAdapter = Utils.isAdapterType(parameter, elements, types);
      boolean isCreator = Utils.isCreatorType(parameter, elements, types);
      boolean isClass = Utils.isClassType(parameter, elements, types);
      if (!isClass && !isCreator && !isAdapter) {
        constructorReport.addError(ErrorMessages.ADAPTER_CONSTRUCTOR_INVALID);
      }
      TypeMirror parameterType = parameter.asType();
      if (Utils.containsWildcards(parameterType)) {
        constructorReport.addError(
            ErrorMessages.ADAPTER_CONSTRUCTOR_PARAMETER_HAS_WILDCARD, parameter);
      } else if (Utils.isRawType(parameterType)) {
        constructorReport.addError(
            ErrorMessages.ADAPTER_CONSTRUCTOR_PARAMETER_HAS_RAW_TYPE, parameter);
      }
    }
    return constructorReport.build();
  }

  private List<TypeParameterElement> findMissingParameters(TypeElement element, TypeMirror adapted) {
    List<TypeParameterElement> result = new ArrayList<>();
    Set<String> adaptedTypeArguments = Utils.getTypeVariableNames(adapted);
    List<? extends TypeParameterElement> parameters = element.getTypeParameters();
    for (TypeParameterElement parameter : parameters) {
      if (!adaptedTypeArguments.contains(parameter.getSimpleName().toString())) {
        result.add(parameter);
      }
    }
    return result;
  }
}
