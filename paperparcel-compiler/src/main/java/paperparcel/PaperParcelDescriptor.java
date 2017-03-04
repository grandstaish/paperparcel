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
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnknownTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** Represents a {@link PaperParcel} annotated object */
@AutoValue
abstract class PaperParcelDescriptor {
  private static final String GET_PREFIX = "get";
  private static final String IS_PREFIX  = "is";
  private static final String HAS_PREFIX = "has";
  private static final String SET_PREFIX = "set";

  /** The original {@link TypeElement} that this class is describing */
  abstract TypeElement element();

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

  /** A list of all of the non-private fields, or an empty list if none */
  abstract ImmutableList<FieldDescriptor> readableFields();

  /**
   * The fields that should be read from getter methods, mapped to the getter method itself.
   * Returns an empty map if there are no fields to be read from getter methods.
   */
  abstract ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap();

  /**
   * Returns all of the adapters required for each field in the annotated class, indexed by the
   * field they are required for
   */
  abstract ImmutableMap<FieldDescriptor, AdapterDescriptor> adapters();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  private static ImmutableSet<String> possibleGetterNames(String name) {
    name = stripKaptEscaping(name);
    ImmutableSet.Builder<String> possibleGetterNames = new ImmutableSet.Builder<>();
    possibleGetterNames.add(name);
    possibleGetterNames.add(IS_PREFIX + Strings.capitalizeAsciiOnly(name));
    possibleGetterNames.add(HAS_PREFIX + Strings.capitalizeAsciiOnly(name));
    possibleGetterNames.add(GET_PREFIX + Strings.capitalizeAsciiOnly(name));
    possibleGetterNames.add(GET_PREFIX + Strings.capitalizeFirstWordAsciiOnly(name));
    return possibleGetterNames.build();
  }

  private static ImmutableSet<String> possibleSetterNames(String name) {
    name = stripKaptEscaping(name);
    ImmutableSet.Builder<String> possibleSetterNames = new ImmutableSet.Builder<>();
    if (startsWithPrefix(IS_PREFIX, name)) {
      possibleSetterNames.add(SET_PREFIX + name.substring(IS_PREFIX.length()));
    }
    if (startsWithPrefix(HAS_PREFIX, name)) {
      possibleSetterNames.add(SET_PREFIX + name.substring(HAS_PREFIX.length()));
    }
    possibleSetterNames.add(name);
    possibleSetterNames.add(SET_PREFIX + Strings.capitalizeAsciiOnly(name));
    possibleSetterNames.add(SET_PREFIX + Strings.capitalizeFirstWordAsciiOnly(name));
    return possibleSetterNames.build();
  }

  private static boolean startsWithPrefix(String prefix, String name) {
    if (!name.startsWith(prefix)) return false;
    if (name.length() == prefix.length()) return false;
    char c = name.charAt(prefix.length());
    return !('a' <= c && c <= 'z');
  }

  private static String stripKaptEscaping(String name) {
    // As of kotlin 1.1.0, kapt escapes stub property names that are java keywords with underscores
    // in order to produce valid java syntax. This function strips these underscores so we can find
    // the matching getter and setter methods.
    if (!name.startsWith("_")) return name;
    if (!name.endsWith("_")) return name;
    if (name.length() == 1) return name;
    String stripped = name.substring(1, name.length() - 1);
    if (SourceVersion.isKeyword(stripped)) return stripped;
    return name;
  }

  @AutoValue
  static abstract class NonReadableFieldsException extends Exception {

    /** A list containing all of the non-readable fields. */
    abstract ImmutableList<VariableElement> nonReadableFields();

    static NonReadableFieldsException create(ImmutableList<VariableElement> nonReadableFields) {
      return new AutoValue_PaperParcelDescriptor_NonReadableFieldsException(nonReadableFields);
    }
  }

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
      return new AutoValue_PaperParcelDescriptor_NonWritableFieldsException(
          allNonWritableFieldsMap, unassignableConstructorParameterMap);
    }
  }

  static final class Factory {
    private final Elements elements;
    private final Types types;
    private final AdapterDescriptor.Factory adapterFactory;
    private final FieldDescriptor.Factory fieldDescriptorFactory;

    Factory(
        Elements elements,
        Types types,
        AdapterDescriptor.Factory adapterFactory,
        FieldDescriptor.Factory fieldDescriptorFactory) {
      this.elements = elements;
      this.types = types;
      this.adapterFactory = adapterFactory;
      this.fieldDescriptorFactory = fieldDescriptorFactory;
    }

    PaperParcelDescriptor create(TypeElement element, OptionsDescriptor options)
        throws NonWritableFieldsException, NonReadableFieldsException {

      ImmutableList<VariableElement> fields = Utils.getFieldsToParcel(element, options);
      //noinspection deprecation: Support for kapt2
      ImmutableSet<ExecutableElement> methods =
          MoreElements.getLocalAndInheritedMethods(element, elements);
      ImmutableList<ExecutableElement> constructors =
          Utils.orderedConstructorsIn(element, options.reflectAnnotations());

      ImmutableList<FieldDescriptor> constructorFields;
      boolean isConstructorVisible;
      ImmutableList<FieldDescriptor> writableFields;
      ImmutableMap<FieldDescriptor, ExecutableElement> setterMethodMap;
      ImmutableList<FieldDescriptor> readableFields;
      ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap;
      ImmutableMap<FieldDescriptor, AdapterDescriptor> adapters;

      boolean singleton = Utils.isSingleton(types, element);
      if (!singleton) {
        WriteInfo writeInfo = WriteInfo.create(
            types, fieldDescriptorFactory, fields, methods, constructors, options.reflectAnnotations());
        constructorFields = writeInfo.constructorFields();
        isConstructorVisible = writeInfo.isConstructorVisible();
        writableFields = writeInfo.writableFields();
        setterMethodMap = writeInfo.setterMethodMap();
        ReadInfo readInfo = ReadInfo.create(
            types, fieldDescriptorFactory, fields, methods, options.reflectAnnotations());
        readableFields = readInfo.readableFields();
        getterMethodMap = readInfo.getterMethodMap();
        adapters = getAdapterMap(readInfo, options.allowSerializable());

      } else {
        constructorFields = ImmutableList.of();
        isConstructorVisible = false;
        writableFields = ImmutableList.of();
        setterMethodMap = ImmutableMap.of();
        readableFields = ImmutableList.of();
        getterMethodMap = ImmutableMap.of();
        adapters = ImmutableMap.of();
      }

      return new AutoValue_PaperParcelDescriptor(
          element,
          constructorFields,
          isConstructorVisible,
          writableFields,
          setterMethodMap,
          readableFields,
          getterMethodMap,
          adapters,
          singleton);
    }

    private ImmutableMap<FieldDescriptor, AdapterDescriptor> getAdapterMap(ReadInfo readInfo,
        boolean allowSerializable) {
      ImmutableMap.Builder<FieldDescriptor, AdapterDescriptor> fieldAdapterMap =
          ImmutableMap.builder();
      if (readInfo != null) {
        for (FieldDescriptor field : readInfo.readableFields()) {
          addAdapterForField(fieldAdapterMap, field, allowSerializable);
        }
        for (FieldDescriptor field : readInfo.getterMethodMap().keySet()) {
          addAdapterForField(fieldAdapterMap, field, allowSerializable);
        }
      }
      return fieldAdapterMap.build();
    }

    private void addAdapterForField(
        ImmutableMap.Builder<FieldDescriptor, AdapterDescriptor> fieldAdapterMap,
        FieldDescriptor field, boolean allowSerializable) {
      TypeMirror fieldType = field.type().get();
      //noinspection ConstantConditions
      if (!fieldType.getKind().isPrimitive()) {
        AdapterDescriptor adapter = adapterFactory.create(fieldType, allowSerializable);
        if (adapter != null) {
          fieldAdapterMap.put(field, adapter);
        } else {
          throw new UnknownTypeException(fieldType, field.element());
        }
      }
    }
  }

  private static class WriteInfo {
    private final ImmutableList<FieldDescriptor> constructorFields;
    private final boolean isConstructorVisible;
    private final ImmutableList<FieldDescriptor> writableFields;
    private final ImmutableMap<FieldDescriptor, ExecutableElement> setterMethodMap;

    WriteInfo(
        ImmutableList<FieldDescriptor> constructorFields,
        boolean isConstructorVisible,
        ImmutableList<FieldDescriptor> writableFields,
        ImmutableMap<FieldDescriptor, ExecutableElement> setterMethodMap) {
      this.constructorFields = constructorFields;
      this.isConstructorVisible = isConstructorVisible;
      this.writableFields = writableFields;
      this.setterMethodMap = setterMethodMap;
    }

    ImmutableList<FieldDescriptor> constructorFields() {
      return constructorFields;
    }

    boolean isConstructorVisible() {
      return isConstructorVisible;
    }

    ImmutableList<FieldDescriptor> writableFields() {
      return writableFields;
    }

    ImmutableMap<FieldDescriptor, ExecutableElement> setterMethodMap() {
      return setterMethodMap;
    }

    static WriteInfo create(
        Types types,
        FieldDescriptor.Factory fieldDescriptorFactory,
        ImmutableList<VariableElement> fields,
        ImmutableSet<ExecutableElement> methods,
        ImmutableList<ExecutableElement> constructors,
        ImmutableList<String> reflectAnnotations) throws NonWritableFieldsException {

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
          if (fieldOrNull != null && types.isAssignable(parameterType,
              Utils.replaceTypeVariablesWithUpperBounds(types, fieldOrNull.asType()))) {
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
            Optional<ExecutableElement> setterMethod = getSetterMethod(types, field, methods);
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
          return new WriteInfo(
              constructorFieldDescriptorsBuilder.build(),
              Visibility.ofElement(constructor) != Visibility.PRIVATE,
              writableFieldsBuilder.build(), setterMethodMapBuilder.build());
        }
      }

      // Throw an error if fields are not writable
      throw NonWritableFieldsException.create(
          allNonWritableFieldsMapBuilder.build(),
          unassignableConstructorParameterMapBuilder.build());
    }

    private static Optional<ExecutableElement> getSetterMethod(
        Types types, VariableElement field, ImmutableSet<ExecutableElement> allMethods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleSetterNames = possibleSetterNames(fieldName);
      TypeMirror fieldType = Utils.replaceTypeVariablesWithUpperBounds(types, field.asType());
      for (ExecutableElement method : allMethods) {
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() == 1
            && possibleSetterNames.contains(method.getSimpleName().toString())
            && method.getTypeParameters().size() == 0
            && types.isAssignable(Utils.replaceTypeVariablesWithUpperBounds(
            types, parameters.get(0).asType()), fieldType)) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }

    private static ImmutableMap<String, VariableElement> fieldNamesToField(
        ImmutableList<VariableElement> fields) {
      ImmutableMap.Builder<String, VariableElement> fieldNamesToField = ImmutableMap.builder();
      for (VariableElement field : fields) {
        fieldNamesToField.put(field.getSimpleName().toString(), field);
      }
      return fieldNamesToField.build();
    }

    private static boolean isWritableDirectly(VariableElement field) {
      Set<Modifier> fieldModifiers = field.getModifiers();
      return !fieldModifiers.contains(Modifier.PRIVATE)
          && !fieldModifiers.contains(Modifier.FINAL);
    }
  }

  private static class ReadInfo {
    private final ImmutableList<FieldDescriptor> readableFields;
    private final ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap;

    private ReadInfo(
        ImmutableList<FieldDescriptor> readableFields,
        ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap) {
      this.readableFields = readableFields;
      this.getterMethodMap = getterMethodMap;
    }

    ImmutableList<FieldDescriptor> readableFields() {
      return readableFields;
    }

    ImmutableMap<FieldDescriptor, ExecutableElement> getterMethodMap() {
      return getterMethodMap;
    }

    static ReadInfo create(
        Types types,
        FieldDescriptor.Factory fieldDescriptorFactory,
        ImmutableList<VariableElement> fields,
        ImmutableSet<ExecutableElement> methods,
        ImmutableList<String> reflectAnnotations) throws NonReadableFieldsException {

      ImmutableList.Builder<FieldDescriptor> readableFieldsBuilder = ImmutableList.builder();
      ImmutableMap.Builder<FieldDescriptor, ExecutableElement> getterMethodMapBuilder =
          ImmutableMap.builder();
      ImmutableList.Builder<VariableElement> nonReadableFieldsBuilder = ImmutableList.builder();

      for (VariableElement field : fields) {
        if (isReadableDirectly(field)) {
          readableFieldsBuilder.add(fieldDescriptorFactory.create(field));
        } else {
          Optional<ExecutableElement> accessorMethod = getAccessorMethod(types, field, methods);
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

      return new ReadInfo(readableFieldsBuilder.build(), getterMethodMapBuilder.build());
    }

    private static Optional<ExecutableElement> getAccessorMethod(
        Types types, VariableElement field, ImmutableSet<ExecutableElement> methods) {
      String fieldName = field.getSimpleName().toString();
      ImmutableSet<String> possibleGetterNames = possibleGetterNames(fieldName);
      TypeMirror fieldType = field.asType();
      for (ExecutableElement method : methods) {
        if (method.getParameters().size() == 0
            && possibleGetterNames.contains(method.getSimpleName().toString())
            && method.getTypeParameters().size() == 0
            && types.isAssignable(method.getReturnType(), fieldType)) {
          return Optional.of(method);
        }
      }
      return Optional.absent();
    }

    private static boolean isReadableDirectly(VariableElement field) {
      Set<Modifier> fieldModifiers = field.getModifiers();
      return !fieldModifiers.contains(Modifier.PRIVATE);
    }
  }
}
