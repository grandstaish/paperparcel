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
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Contains all of the information on how to re-construct an object and populate all of its
 * data via either constructor arguments, setter methods, or directly setting fields.
 */
@AutoValue
abstract class WriteInfo {

  /** All fields that should be passed to the constructor, in order */
  abstract ImmutableList<FieldDescriptor> constructorFields();

  /** Returns true if the constructor is non-private */
  abstract boolean isConstructorVisible();

  /** The fields that should be written directly, or an empty list if none */
  abstract ImmutableList<FieldDescriptor> writableFields();

  /**
   * The fields that should be written via setter methods, mapped to the setter method itself.
   * Returns an empty map if there are no fields to be set via setter methods.
   */
  abstract ImmutableMap<FieldDescriptor, ExecutableElement> setterMethodMap();

  @AutoValue
  static abstract class NonWritableFieldsException extends Exception {

    /**
     * Contains all of the valid constructor elements, mapped to all of the fields that couldn't be
     * written when using that constructor.
     *
     * Valid constructors are found if all parameters had a corresponding field (same name and
     * the types are assignable). Private constructors are not included.
     *
     * If there were no valid constructors, this returns an empty map.
     */
    abstract ImmutableMap<ExecutableElement, ImmutableList<VariableElement>> allNonWritableFieldsMap();

    /**
     * Contains all of the invalid constructor elements, mapped to the list of unassignable fields
     * found when parsing that constructor.
     *
     * Invalid constructors are found if not all parameters had a corresponding field (same name
     * and the types are assignable). Private constructors are not included.
     *
     * If there were no invalid constructors, this returns an empty map.
     */
    abstract ImmutableMap<ExecutableElement, ImmutableList<VariableElement>> unassignableConstructorParameterMap();

    static NonWritableFieldsException create(
        ImmutableMap<ExecutableElement, ImmutableList<VariableElement>> allNonWritableFieldsMap,
        ImmutableMap<ExecutableElement, ImmutableList<VariableElement>> unassignableConstructorParameterMap) {
      return new AutoValue_WriteInfo_NonWritableFieldsException(
          allNonWritableFieldsMap, unassignableConstructorParameterMap);
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

    WriteInfo create(
        ImmutableList<VariableElement> fields,
        ImmutableSet<ExecutableElement> methods,
        ImmutableList<ExecutableElement> constructors,
        ImmutableList<String> reflectAnnotations)
        throws NonWritableFieldsException {

      ImmutableMap<String, VariableElement> fieldNamesToField = fieldNamesToField(fields);
      ImmutableMap.Builder<ExecutableElement, ImmutableList<VariableElement>>
          allNonWritableFieldsMapBuilder = ImmutableMap.builder();
      ImmutableMap.Builder<ExecutableElement, ImmutableList<VariableElement>>
          unassignableConstructorParameterMapBuilder = ImmutableMap.builder();

      for (ExecutableElement constructor : constructors) {
        // Create a mutable copy of fieldNamesToField so we can remove elements from it as we iterate
        // to keep track of which elements we have seen
        Map<String, VariableElement> nonConstructorFieldsMap = new LinkedHashMap<>(fieldNamesToField);
        ImmutableList.Builder<VariableElement> unassignableParametersBuilder =
            ImmutableList.builder();
        ImmutableList.Builder<FieldDescriptor> constructorFieldDescriptorsBuilder =
            ImmutableList.builder();
        // Iterate over parameters and check two things:
        // 1) The parameter has a field with the same name
        // 2) The parameter type is assignable to the field
        List<? extends VariableElement> parameters = constructor.getParameters();
        for (VariableElement parameter : parameters) {
          String parameterName = parameter.getSimpleName().toString();
          // All wildcards need to be stripped from the parameter type as a work around
          // for kotlin data classes generating non-assignable constructor parameters
          // with generic types.
          TypeMirror parameterType =
              Utils.replaceTypeVariablesWithUpperBounds(types, parameter.asType());
          VariableElement fieldOrNull = fieldNamesToField.get(parameterName);
          if (fieldOrNull != null
              && types.isAssignable(parameterType, Utils.replaceTypeVariablesWithUpperBounds(
              types, fieldOrNull.asType()))) {
            nonConstructorFieldsMap.remove(parameterName);
            constructorFieldDescriptorsBuilder.add(fieldDescriptorFactory.create(fieldOrNull));
          } else {
            unassignableParametersBuilder.add(parameter);
          }
        }
        // Check if there were any unassignable parameters in the constructor. If so, skip.
        ImmutableList<VariableElement> unassignableParameters = unassignableParametersBuilder.build();
        if (unassignableParameters.size() > 0) {
          unassignableConstructorParameterMapBuilder.put(constructor, unassignableParameters);
          continue;
        }
        // Check that the remaining parameters are assignable directly, or via setters.
        ImmutableList.Builder<VariableElement> nonWritableFieldsBuilder = ImmutableList.builder();
        ImmutableList<VariableElement> nonConstructorFields =
            ImmutableList.copyOf(nonConstructorFieldsMap.values());
        ImmutableList.Builder<FieldDescriptor> writableFieldsBuilder = ImmutableList.builder();
        ImmutableMap.Builder<FieldDescriptor, ExecutableElement> setterMethodMapBuilder =
            ImmutableMap.builder();
        for (VariableElement field : nonConstructorFields) {
          if (isWritableDirectly(field)) {
            writableFieldsBuilder.add(fieldDescriptorFactory.create(field));
          } else {
            Optional<ExecutableElement> setterMethod = getSetterMethod(field, methods);
            if (setterMethod.isPresent()) {
              setterMethodMapBuilder.put(fieldDescriptorFactory.create(field), setterMethod.get());
            } else if (Utils.usesAnyAnnotationsFrom(field, reflectAnnotations)) {
              writableFieldsBuilder.add(fieldDescriptorFactory.create(field));
            } else {
              nonWritableFieldsBuilder.add(field);
            }
          }
        }
        ImmutableList<VariableElement> nonWritableFields = nonWritableFieldsBuilder.build();
        if (nonWritableFields.size() != 0) {
          // Map all of the non-writable fields to the corresponding constructor for (potential)
          // error handling later
          allNonWritableFieldsMapBuilder.put(constructor, nonWritableFields);
        } else {
          // All fields are writable using this constructor
          return new AutoValue_WriteInfo(
              constructorFieldDescriptorsBuilder.build(),
              Visibility.ofElement(constructor) != Visibility.PRIVATE,
              writableFieldsBuilder.build(),
              setterMethodMapBuilder.build());
        }
      }

      // Throw an error if fields are not writable
      throw NonWritableFieldsException.create(
          allNonWritableFieldsMapBuilder.build(),
          unassignableConstructorParameterMapBuilder.build());
    }

    /**
     * Searches all non-private methods to find a method that matches the following conditions:
     * 1. One parameter
     * 2. Name contained in the set defined by {@link #possibleSetterNames(String)}
     * 3. Parameter type equivalent to the current fields type
     */
    private Optional<ExecutableElement> getSetterMethod(
        VariableElement field, ImmutableSet<ExecutableElement> allMethods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleSetterNames = possibleSetterNames(fieldName);
      TypeMirror fieldType = Utils.replaceTypeVariablesWithUpperBounds(types, field.asType());
      for (ExecutableElement method : allMethods) {
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() == 1
            && possibleSetterNames.contains(method.getSimpleName().toString())
            && types.isAssignable(Utils.replaceTypeVariablesWithUpperBounds(
            types, parameters.get(0).asType()), fieldType)) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }

    private ImmutableMap<String, VariableElement> fieldNamesToField(
        ImmutableList<VariableElement> fields) {
      ImmutableMap.Builder<String, VariableElement> fieldNamesToField = ImmutableMap.builder();
      for (VariableElement field : fields) {
        fieldNamesToField.put(field.getSimpleName().toString(), field);
      }
      return fieldNamesToField.build();
    }

    private boolean isWritableDirectly(VariableElement field) {
      Set<Modifier> fieldModifiers = field.getModifiers();
      return !fieldModifiers.contains(Modifier.PRIVATE)
          && !fieldModifiers.contains(Modifier.FINAL);
    }
  }

  private static ImmutableSet<String> possibleSetterNames(String name) {
    ImmutableSet.Builder<String> possibleGetterNames = new ImmutableSet.Builder<>();
    possibleGetterNames.add(name);
    possibleGetterNames.add("set" + Strings.capitalizeFirstCharacter(name));
    return possibleGetterNames.build();
  }
}
