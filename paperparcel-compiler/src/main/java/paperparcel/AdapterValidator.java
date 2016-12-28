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
import javax.lang.model.util.SimpleTypeVisitor6;
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

    TypeMirror adaptedType = isAdapter
        ? Utils.getAdaptedType(elements, types, asDeclared(element.asType()))
        : null;
    if (adaptedType != null) {
      if (Utils.isJavaLangObject(adaptedType)) {
        builder.addError(ErrorMessages.ADAPTER_TYPE_ARGUMENT_IS_MISSING);
      } else if (Utils.containsWildcards(adaptedType)) {
        builder.addError(String.format(ErrorMessages.ADAPTER_ADAPTED_TYPE_HAS_WILDCARDS,
            element.getSimpleName(), adaptedType));
      } else if (!hasValidTypeParameters(element, adaptedType)) {
        builder.addError(ErrorMessages.ADAPTER_INCOMPATIBLE_TYPE_PARAMETERS);
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
        constructorReport.addError(ErrorMessages.ADAPTER_INVALID_CONSTRUCTOR);
      }
      TypeMirror parameterType = parameter.asType();
      if (isAdapter) {
        TypeMirror adaptedType = Utils.getAdaptedType(elements, types, asDeclared(parameterType));
        if (Utils.isJavaLangObject(adaptedType)) {
          constructorReport.addError(
              ErrorMessages.TYPE_ADAPTER_CONSTRUCTOR_PARAMETER_TYPE_ARGUMENT_MISSING, parameter);
        }
      }
      if (isClass) {
        TypeMirror classType = Utils.getClassArg(elements, types, asDeclared(parameterType));
        if (Utils.isJavaLangObject(classType)) {
          constructorReport.addError(
              ErrorMessages.CLASS_CONSTRUCTOR_PARAMETER_TYPE_ARGUMENT_MISSING, parameter);
        }
      }
    }
    return constructorReport.build();
  }

  /**
   * Ensure that the adapter's type arguments are all passed to the adapted type so that we
   * can use a field's type to resolve the adapter's type arguments at compile time.
   */
  private boolean hasValidTypeParameters(TypeElement element, TypeMirror adaptedType) {
    TypeVariable maybeTypeVariable = asTypeVariableSafe(adaptedType);
    if (maybeTypeVariable != null) {
      // For this type variable to have any meaning, it must have an extends bounds
      TypeMirror erasedTypeVariable = types.erasure(maybeTypeVariable);
      if (Utils.isJavaLangObject(erasedTypeVariable)) {
        return false;
      }
    }

    // Collect all of the unique type variables used in the adapted type
    Set<String> adaptedTypeArguments = Utils.getTypeVariableNames(adaptedType);

    // Get a set of all of the adapter's type parameter names
    ImmutableSet<String> adapterParameters = FluentIterable.from(element.getTypeParameters())
        .transform(new Function<TypeParameterElement, String>() {
          @Override public String apply(TypeParameterElement input) {
            return input.getSimpleName().toString();
          }
        })
        .toSet();

    // Verify the type parameters on the adapter are all passed to the adapted type by getting
    // the difference between the two sets and verifying it is empty.
    return Sets.difference(adapterParameters, adaptedTypeArguments).size() == 0;
  }

  /**
   * Returns a {@link TypeVariable} if the {@link TypeMirror} represents a type variable
   * or null if not.
   */
  private static TypeVariable asTypeVariableSafe(TypeMirror maybeTypeVariable) {
    return maybeTypeVariable.accept(new SimpleTypeVisitor6<TypeVariable, Void>() {
      @Override public TypeVariable visitTypeVariable(TypeVariable type, Void p) {
        return type;
      }
    }, null);
  }
}
