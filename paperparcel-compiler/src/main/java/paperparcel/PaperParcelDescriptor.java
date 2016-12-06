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

import android.support.annotation.Nullable;
import com.google.auto.common.MoreElements;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnknownTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** Represents a {@link PaperParcel} annotated object */
@AutoValue
abstract class PaperParcelDescriptor {

  /** The original {@link TypeElement} that this class is describing */
  abstract TypeElement element();

  /** Information on how to write each field, or null if not required (i.e. is a singleton) */
  @Nullable abstract WriteInfo writeInfo();

  /** Information on how to read each field, or null if not required (i.e. is a singleton) */
  @Nullable abstract ReadInfo readInfo();

  /**
   * Returns all of the adapters required for each field in the annotated class, indexed by the
   * field they are required for
   */
  abstract ImmutableMap<FieldDescriptor, Adapter> adapters();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  static final class Factory {
    private final Elements elements;
    private final Types types;
    private final Adapter.Factory adapterFactory;
    private final WriteInfo.Factory writeInfoFactory;
    private final ReadInfo.Factory readInfoFactory;

    Factory(
        Elements elements,
        Types types,
        Adapter.Factory adapterFactory,
        WriteInfo.Factory writeInfoFactory,
        ReadInfo.Factory readInfoFactory) {
      this.elements = elements;
      this.types = types;
      this.adapterFactory = adapterFactory;
      this.writeInfoFactory = writeInfoFactory;
      this.readInfoFactory = readInfoFactory;
    }

    PaperParcelDescriptor create(TypeElement element)
        throws WriteInfo.NonWritableFieldsException, ReadInfo.NonReadableFieldsException {

      Options options = Utils.getOptions(element);

      ImmutableList<VariableElement> fields = Utils.getFieldsToParcel(types, element, options);
      ImmutableSet<ExecutableElement> methods =
          MoreElements.getLocalAndInheritedMethods(element, types, elements);
      ImmutableList<ExecutableElement> constructors =
          Utils.orderedConstructorsIn(element, options.reflectAnnotations());

      WriteInfo writeInfo = null;
      ReadInfo readInfo = null;

      boolean singleton = Utils.isSingleton(types, element);
      if (!singleton) {
        writeInfo = writeInfoFactory.create(
            fields, methods, constructors, options.reflectAnnotations());
        readInfo = readInfoFactory.create(fields, methods, options.reflectAnnotations());
      }

      ImmutableMap<FieldDescriptor, Adapter> adapters = getAdapterMap(readInfo);

      return new AutoValue_PaperParcelDescriptor(element, writeInfo, readInfo, adapters, singleton);
    }

    private ImmutableMap<FieldDescriptor, Adapter> getAdapterMap(ReadInfo readInfo) {
      ImmutableMap.Builder<FieldDescriptor, Adapter> fieldAdapterMap = ImmutableMap.builder();
      if (readInfo != null) {
        for (FieldDescriptor field : readInfo.readableFields()) {
          addAdapterForField(fieldAdapterMap, field);
        }
        for (FieldDescriptor field : readInfo.getterMethodMap().keySet()) {
          addAdapterForField(fieldAdapterMap, field);
        }
      }
      return fieldAdapterMap.build();
    }

    private void addAdapterForField(
        ImmutableMap.Builder<FieldDescriptor, Adapter> fieldAdapterMap, FieldDescriptor field) {
      TypeMirror fieldType = field.type().get();
      Preconditions.checkNotNull(fieldType);
      if (!fieldType.getKind().isPrimitive()) {
        Adapter adapter = adapterFactory.create(fieldType);
        if (adapter != null) {
          fieldAdapterMap.put(field, adapter);
        } else {
          throw new UnknownTypeException(fieldType, field.element());
        }
      }
    }
  }
}
