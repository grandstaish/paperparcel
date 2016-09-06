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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

/** A validator for custom adapters annotated with {@link RegisterAdapter} */
final class RegisterAdapterValidator {
  private static final String TYPE_ADAPTER_CLASS_NAME = "paperparcel.TypeAdapter";

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
    TypeMirror typeAdapterType = types.getDeclaredType(
        elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME),
        types.getWildcardType(null, null));
    if (!types.isAssignable(element.asType(), typeAdapterType)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_NON_TYPE_ADAPTER);
    }
    if (element.getKind() == ElementKind.INTERFACE) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_INTERFACE);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_ABSTRACT_CLASS);
    }
    Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
    if (mainConstructor.isPresent()) {
      builder.addSubreport(validateConstructor(typeAdapterType, types, mainConstructor.get()));
    } else if (!Utils.isSingleton(types, element)) {
      builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
    }
    TypeMirror adaptedType = Utils.getAdaptedType(types, element.asType());
    if (adaptedType == null) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_RAW_TYPE_ADAPTER);
    } else if (!hasValidTypeParameters(element, adaptedType)) {
      builder.addError(ErrorMessages.INCOMPATIBLE_TYPE_PARAMETERS);
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
      TypeMirror adaptedType = Utils.getAdaptedType(types, parameterType);
      if (adaptedType == null) {
        constructorReport.addError(ErrorMessages.RAW_TYPE_ADAPTER_IN_CONSTRUCTOR, parameter);
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
      if (isJavaLangObject(erasedTypeVariable)) {
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

  private boolean isJavaLangObject(TypeMirror type) {
    if (type.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) type;
    TypeElement typeElement = (TypeElement) declaredType.asElement();
    return typeElement.getQualifiedName().contentEquals("java.lang.Object");
  }
}
