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
import com.google.auto.value.AutoValue;
import com.google.common.base.Equivalence;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.PRIVATE;

/** Represents a single field in a {@link PaperParcel} class */
@AutoValue
abstract class FieldDescriptor {

  /** The original {@link VariableElement} that this class is describing */
  abstract VariableElement element();

  /** The simple name of the field */
  abstract String name();

  /** The original type of the field */
  abstract Equivalence.Wrapper<TypeMirror> type();

  /** The normalized type of the field. Primitive types will always be boxed. */
  abstract Equivalence.Wrapper<TypeMirror> normalizedType();

  /** True if the field is private, false otherwise */
  abstract boolean isPrivate();

  /** The field getter method, if it exists */
  abstract Optional<ExecutableElement> accessorMethod();

  /** The field setter method, if it exists */
  abstract Optional<ExecutableElement> setterMethod();

  static ImmutableSet<String> possibleGetterNames(String name) {
    ImmutableSet.Builder<String> possibleGetterNames = new ImmutableSet.Builder<>();
    possibleGetterNames.add(name);
    possibleGetterNames.add("is" + Strings.capitalizeFirstCharacter(name));
    possibleGetterNames.add("has" + Strings.capitalizeFirstCharacter(name));
    possibleGetterNames.add("get" + Strings.capitalizeFirstCharacter(name));
    return possibleGetterNames.build();
  }

  static ImmutableSet<String> possibleSetterNames(String name) {
    ImmutableSet.Builder<String> possibleGetterNames = new ImmutableSet.Builder<>();
    possibleGetterNames.add(name);
    possibleGetterNames.add("set" + Strings.capitalizeFirstCharacter(name));
    return possibleGetterNames.build();
  }

  static final class Factory {
    private final Types types;

    Factory(
        Types types) {
      this.types = types;
    }

    FieldDescriptor create(
        VariableElement element, ImmutableSet<ExecutableElement> allMethods) {
      String name = element.getSimpleName().toString();
      TypeMirror type = element.asType();
      TypeMirror normalizedType = normalize(type);
      Set<Modifier> modifiers = element.getModifiers();
      boolean isPrivate = modifiers.contains(PRIVATE);
      Optional<ExecutableElement> accessor = getAccessorMethod(element, allMethods);
      Optional<ExecutableElement> setter = getSetterMethod(element, allMethods);
      Equivalence.Wrapper<TypeMirror> wrappedType = MoreTypes.equivalence().wrap(type);
      Equivalence.Wrapper<TypeMirror> wrappedNormalizedType =
          MoreTypes.equivalence().wrap(normalizedType);
      return new AutoValue_FieldDescriptor(
          element, name, wrappedType, wrappedNormalizedType, isPrivate, accessor, setter);
    }

    private TypeMirror normalize(TypeMirror type) {
      TypeKind kind = type.getKind();
      return kind.isPrimitive()
          ? types.boxedClass((PrimitiveType) type).asType()
          : type;
    }

    /**
     * Searches all non-private methods to find a method that matches the following conditions:
     * 1. No parameters
     * 2. Name contained in the set defined by {@link #possibleGetterNames(String)}
     * 3. Return type equivalent to the current fields type
     */
    private Optional<ExecutableElement> getAccessorMethod(
        final VariableElement field, Set<ExecutableElement> allMethods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleGetterNames = possibleGetterNames(fieldName);
      TypeMirror fieldType = field.asType();
      for (ExecutableElement method : allMethods) {
        if (method.getParameters().size() == 0
            && possibleGetterNames.contains(method.getSimpleName().toString())
            && MoreTypes.equivalence().equivalent(fieldType, method.getReturnType())) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }

    /**
     * Searches all non-private methods to find a method that matches the following conditions:
     * 1. One parameter
     * 2. Name contained in the set defined by {@link #possibleSetterNames(String)}
     * 3. Parameter type equivalent to the current fields type
     */
    private Optional<ExecutableElement> getSetterMethod(
        final VariableElement field, Set<ExecutableElement> allMethods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleSetterNames = possibleSetterNames(fieldName);
      TypeMirror fieldType = field.asType();
      for (ExecutableElement method : allMethods) {
        List<? extends VariableElement> methodParameters = method.getParameters();
        if (methodParameters.size() == 1
            && possibleSetterNames.contains(method.getSimpleName().toString())
            && MoreTypes.equivalence().equivalent(fieldType, methodParameters.get(0).asType())) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }
  }
}
