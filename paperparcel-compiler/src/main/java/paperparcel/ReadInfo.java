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

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Contains all of the information on how to read the data from an object, either by reading
 * fields directly, or via getter methods.
 */
@AutoValue
abstract class ReadInfo {

  /** A list of all of the non-private fields in a type element, or an empty list if none */
  abstract ImmutableList<FieldDescriptor> readableFields();

  /**
   * The fields that should be read from getter methods, mapped to the getter method itself.
   * Returns an empty map if there are no fields to be read from getter methods.
   */
  abstract ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap();

  @AutoValue
  static abstract class NonReadableFieldsException extends Exception {

    /** A list containing all of the non-readable fields. */
    abstract ImmutableList<VariableElement> nonReadableFields();

    static NonReadableFieldsException create(ImmutableList<VariableElement> nonReadableFields) {
      return new AutoValue_ReadInfo_NonReadableFieldsException(nonReadableFields);
    }
  }

  static class Factory {
    private final Types types;
    private final FieldDescriptor.Factory fieldDescriptorFactory;

    Factory(
        Types types,
        FieldDescriptor.Factory fieldDescriptorFactory) {
      this.types = types;
      this.fieldDescriptorFactory = fieldDescriptorFactory;
    }

    ReadInfo create(
        ImmutableList<VariableElement> fields,
        ImmutableSet<ExecutableElement> methods,
        ImmutableList<String> reflectAnnotations
    ) throws NonReadableFieldsException {

      ImmutableList.Builder<FieldDescriptor> readableFieldsBuilder = ImmutableList.builder();
      ImmutableMap.Builder<FieldDescriptor, ExecutableElement> getterMethodMapBuilder =
          ImmutableMap.builder();
      ImmutableList.Builder<VariableElement> nonReadableFieldsBuilder = ImmutableList.builder();

      for (VariableElement field : fields) {
        if (isReadableDirectly(field)) {
          readableFieldsBuilder.add(fieldDescriptorFactory.create(field));
        } else {
          Optional<ExecutableElement> accessorMethod = getAccessorMethod(field, methods);
          if (accessorMethod.isPresent()) {
            getterMethodMapBuilder.put(fieldDescriptorFactory.create(field), accessorMethod.get());
          } else if (Utils.usesAnyAnnotationsFrom(field, reflectAnnotations)) {
            readableFieldsBuilder.add(fieldDescriptorFactory.create(field));
          } else {
            nonReadableFieldsBuilder.add(field);
          }
        }
      }

      ImmutableList<VariableElement> nonReadableFields = nonReadableFieldsBuilder.build();
      if (nonReadableFields.size() > 0) {
        throw NonReadableFieldsException.create(nonReadableFields);
      }

      return new AutoValue_ReadInfo(
          readableFieldsBuilder.build(),
          getterMethodMapBuilder.build());
    }

    /**
     * Searches all non-private methods to find a method that matches the following conditions:
     * 1. No parameters
     * 2. Name contained in the set defined by {@link #possibleGetterNames(String)}
     * 3. Return type equivalent to the current fields type
     */
    private Optional<ExecutableElement> getAccessorMethod(
        VariableElement field, ImmutableSet<ExecutableElement> methods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleGetterNames = possibleGetterNames(fieldName);
      TypeMirror fieldType = field.asType();
      for (ExecutableElement method : methods) {
        if (method.getParameters().size() == 0
            && possibleGetterNames.contains(method.getSimpleName().toString())
            && types.isAssignable(method.getReturnType(), fieldType)) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }

    private boolean isReadableDirectly(VariableElement field) {
      Set<Modifier> fieldModifiers = field.getModifiers();
      return !fieldModifiers.contains(Modifier.PRIVATE);
    }
  }

  private static ImmutableSet<String> possibleGetterNames(String name) {
    ImmutableSet.Builder<String> possibleGetterNames = new ImmutableSet.Builder<>();
    possibleGetterNames.add(name);
    possibleGetterNames.add("is" + Strings.capitalizeFirstCharacter(name));
    possibleGetterNames.add("has" + Strings.capitalizeFirstCharacter(name));
    possibleGetterNames.add("get" + Strings.capitalizeFirstCharacter(name));
    return possibleGetterNames.build();
  }
}
