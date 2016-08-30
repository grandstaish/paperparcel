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
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import org.jetbrains.annotations.Nullable;

/** Represents a {@link PaperParcel} annotated object */
@AutoValue
abstract class PaperParcelDescriptor {

  /** The original {@link TypeElement} that this class is describing */
  abstract TypeElement element();

  /** A full list of all of the fields to parcel */
  abstract ImmutableList<FieldDescriptor> fields();

  /** Information on how to write each field, or null if not required (i.e. is a singleton) */
  @Nullable abstract WriteInfo writeInfo();

  /** Information on how to read each field, or null if not required (i.e. is a singleton) */
  @Nullable abstract ReadInfo readInfo();

  /**
   * Returns all of the adapters required for each field in the annotated class, indexed by the
   * field they are required for
   */
  abstract ImmutableMap<FieldDescriptor, AdapterGraph> adapters();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  static final class Factory {
    private final Types types;
    private final AdapterGraph.Factory adapterGraphFactory;

    Factory(
        Types types,
        AdapterGraph.Factory adapterGraphFactory) {
      this.types = types;
      this.adapterGraphFactory = adapterGraphFactory;
    }

    PaperParcelDescriptor create(TypeElement element, WriteInfo writeInfo, ReadInfo readInfo) {
      ImmutableList.Builder<FieldDescriptor> fieldsBuilder = ImmutableList.builder();
      if (readInfo != null) {
        fieldsBuilder.addAll(readInfo.readableFields());
        fieldsBuilder.addAll(readInfo.getterMethodMap().keySet());
      }
      ImmutableList<FieldDescriptor> fields = fieldsBuilder.build();
      ImmutableMap<FieldDescriptor, AdapterGraph> adapters = getAdapterMap(fields);
      boolean singleton = Utils.isSingleton(types, element);
      return new AutoValue_PaperParcelDescriptor(
          element, fields, writeInfo, readInfo, adapters, singleton);
    }

    private ImmutableMap<FieldDescriptor, AdapterGraph> getAdapterMap(
        ImmutableList<FieldDescriptor> fields) {
      return FluentIterable.from(fields)
          .toMap(new Function<FieldDescriptor, AdapterGraph>() {
            @Override public AdapterGraph apply(FieldDescriptor field) {
              return adapterGraphFactory.create(field.normalizedType().get());
            }
          });
    }
  }
}
