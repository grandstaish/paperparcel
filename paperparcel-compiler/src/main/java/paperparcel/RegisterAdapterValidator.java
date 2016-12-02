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

import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

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

    if (!Utils.isAdapterType(element, elements, types)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_NON_TYPE_ADAPTER);
    }
    if (element.getKind() != ElementKind.CLASS) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_NON_CLASS);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_ABSTRACT_CLASS);
    }
    if (Visibility.ofElement(element) != Visibility.PUBLIC) {
      builder.addError(ErrorMessages.REGISTER_ADAPTER_ON_NON_PUBLIC_CLASS);
    } else if (Visibility.effectiveVisibilityOfElement(element) != Visibility.PUBLIC) {
      builder.addError(ErrorMessages.REGISTER_ADAPTER_NOT_VISIBLE);
    }
    ElementKind enclosingKind = element.getEnclosingElement().getKind();
    if (enclosingKind.isClass() || enclosingKind.isInterface()) {
      if (!element.getModifiers().contains(Modifier.STATIC)) {
        builder.addError(ErrorMessages.REGISTER_ADAPTER_ON_NON_STATIC_INNER_CLASS);
      }
    }

    Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
    if (mainConstructor.isPresent()) {
      builder.addSubreport(validateConstructor(mainConstructor.get()));
    } else if (!Utils.isSingleton(types, element)) {
      builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
    }

    TypeMirror adaptedType =
        Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(element.asType()));
    if (adaptedType != null) {
      if (Utils.isJavaLangObject(adaptedType)) {
        builder.addError(ErrorMessages.REGISTERADAPTER_ON_RAW_TYPE_ADAPTER);
      } else if (containsWildcards(adaptedType)) {
        builder.addError(String.format(ErrorMessages.WILDCARD_IN_ADAPTED_TYPE,
            element.getSimpleName(), adaptedType));
      } else if (!hasValidTypeParameters(element, adaptedType)) {
        builder.addError(ErrorMessages.INCOMPATIBLE_TYPE_PARAMETERS);
      }
    }

    return builder.build();
  }

  private ValidationReport<ExecutableElement> validateConstructor(ExecutableElement constructor) {
    ValidationReport.Builder<ExecutableElement> constructorReport =
        ValidationReport.about(constructor);
    for (VariableElement parameter : constructor.getParameters()) {
      boolean isAdapter = Utils.isAdapterType(parameter, elements, types);
      boolean isClass = Utils.isClassType(parameter, elements, types);
      if (!isClass && !isAdapter) {
        constructorReport.addError(ErrorMessages.INVALID_TYPE_ADAPTER_CONSTRUCTOR);
      }
      TypeMirror parameterType = parameter.asType();
      if (isAdapter) {
        TypeMirror adaptedType = Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(parameterType));
        if (Utils.isJavaLangObject(adaptedType)) {
          constructorReport.addError(ErrorMessages.RAW_TYPE_ADAPTER_IN_CONSTRUCTOR, parameter);
        }
      }
      if (isClass) {
        TypeMirror classType = Utils.getClassType(elements, types, MoreTypes.asDeclared(parameterType));
        if (Utils.isJavaLangObject(classType)) {
          constructorReport.addError(ErrorMessages.RAW_CLASS_TYPE_IN_CONSTRUCTOR, parameter);
        }
      }
    }
    return constructorReport.build();
  }

  /** Returns true if {@code typeMirror} contains any wildcards. */
  private boolean containsWildcards(TypeMirror typeMirror) {
    return typeMirror.accept(new SimpleTypeVisitor6<Boolean, Void>(false) {
      @Override public Boolean visitArray(ArrayType type, Void p) {
        return type.getComponentType().accept(this, p);
      }

      @Override public Boolean visitDeclared(DeclaredType type, Void p) {
        for (TypeMirror arg : type.getTypeArguments()) {
          if (arg.accept(this, p)) return true;
        }
        return false;
      }

      @Override public Boolean visitWildcard(WildcardType type, Void p) {
        return true;
      }
    }, null);
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
    ImmutableSet.Builder<String> adaptedTypeArguments = ImmutableSet.builder();
    adaptedType.accept(new SimpleTypeVisitor6<Void, ImmutableSet.Builder<String>>() {
      @Override
      public Void visitTypeVariable(TypeVariable type, ImmutableSet.Builder<String> set) {
        set.add(type.toString());
        return null;
      }

      @Override
      public Void visitArray(ArrayType type, ImmutableSet.Builder<String> set) {
        type.getComponentType().accept(this, set);
        return null;
      }

      @Override
      public Void visitDeclared(DeclaredType type, ImmutableSet.Builder<String> set) {
        for (TypeMirror arg : type.getTypeArguments()) {
          arg.accept(this, set);
        }
        return null;
      }

      @Override
      public Void visitWildcard(WildcardType type, ImmutableSet.Builder<String> set) {
        if (type.getSuperBound() != null) {
          type.getSuperBound().accept(this, set);
        }
        if (type.getExtendsBound() != null) {
          type.getExtendsBound().accept(this, set);
        }
        return null;
      }
    }, adaptedTypeArguments);

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
    return Sets.difference(adapterParameters, adaptedTypeArguments.build()).size() == 0;
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
