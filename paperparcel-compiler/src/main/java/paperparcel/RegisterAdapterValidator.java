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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
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
    Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
    if (mainConstructor.isPresent()) {
      builder.addSubreport(validateConstructor(
          erasedTypeAdapterType, types, mainConstructor.get()));
    } else if (!Utils.isSingleton(types, element)) {
      builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
    }
    List<? extends TypeMirror> typeArguments = Utils.getTypeArgumentsOfTypeFromType(
        types, element.asType(), erasedTypeAdapterType);
    if (typeArguments == null || typeArguments.size() == 0) {
      builder.addError(ErrorMessages.REGISTERADAPTER_ON_RAW_TYPE_ADAPTER);
    } else if (!hasValidTypeParameters(element, typeArguments.get(0))) {
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
      List<? extends TypeMirror> typeArguments = Utils.getTypeArgumentsOfTypeFromType(
          types, parameterType, adapterInterfaceType);
      if (typeArguments == null || typeArguments.size() == 0) {
        constructorReport.addError(ErrorMessages.RAW_TYPE_ADAPTER_IN_CONSTRUCTOR, parameter);
      }
    }
    return constructorReport.build();
  }

  /**
   * Ensure that the adapter's type arguments are all passed to the adapted type so that we
   * can use a field's type to resolve the adapter's type arguments at compile time.
   */
  private boolean hasValidTypeParameters(final TypeElement element, TypeMirror adaptedType) {

    TypeVariable maybeTypeVariable = asTypeVariableSafe(adaptedType);
    if (maybeTypeVariable != null) {
      // For this type variable to have any meaning, it must have an extends bounds
      TypeMirror erasedTypeVariable = types.erasure(maybeTypeVariable);
      if (isJavaLangObject(erasedTypeVariable)) {
        return false;
      }
    }

    // Only other possible types that can be adapted are arrays and classes.
    ImmutableList<? extends TypeMirror> typesToCheck =
        adaptedType.accept(new SimpleTypeVisitor6<ImmutableList<? extends TypeMirror>, Void>() {
          @Override
          public ImmutableList<? extends TypeMirror> visitTypeVariable(TypeVariable type, Void p) {
            return ImmutableList.of(type);
          }

          @Override
          public ImmutableList<? extends TypeMirror> visitArray(ArrayType type, Void p) {
            return ImmutableList.of(type.getComponentType());
          }

          @Override
          public ImmutableList<? extends TypeMirror> visitDeclared(DeclaredType type, Void p) {
            return ImmutableList.copyOf(type.getTypeArguments());
          }

          @Override
          protected ImmutableList<? extends TypeMirror> defaultAction(TypeMirror type, Void p) {
            throw new AssertionError("Invalid type argument in " + element.toString());
          }
        }, null);

    // Collect all of the unique type variables used in types to check
    ImmutableSet.Builder<String> uniqueTypeVariableNames = ImmutableSet.builder();
    for (TypeMirror type : typesToCheck) {
      type.accept(new SimpleTypeVisitor6<Void, ImmutableSet.Builder<String>>() {
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
          type.getSuperBound().accept(this, set);
          type.getExtendsBound().accept(this, set);
          return null;
        }
      }, uniqueTypeVariableNames);
    }

    // Verify the amount of type parameters on this adapter match the number of type variables
    // used in the array component type or the declared types' type arguments.
    List<? extends TypeParameterElement> adapterTypeParameters = element.getTypeParameters();
    return adapterTypeParameters.size() == uniqueTypeVariableNames.build().size();
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
