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
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import javax.lang.model.element.TypeElement;
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
    private final Types types;
    private final Adapter.Factory adapterFactory;

    Factory(
        Types types,
        Adapter.Factory adapterFactory) {
      this.types = types;
      this.adapterFactory = adapterFactory;
    }

    PaperParcelDescriptor create(TypeElement element, WriteInfo writeInfo, ReadInfo readInfo) {
      ImmutableMap<FieldDescriptor, Adapter> adapters = getAdapterMap(readInfo);
      boolean singleton = Utils.isSingleton(types, element);
      return new AutoValue_PaperParcelDescriptor(element, writeInfo, readInfo, adapters, singleton);
    }

    private ImmutableMap<FieldDescriptor, Adapter> getAdapterMap(ReadInfo readInfo) {
      ImmutableMap.Builder<FieldDescriptor, Adapter> fieldAdapterMap = ImmutableMap.builder();
      if (readInfo != null) {
        for (FieldDescriptor field : readInfo.readableFields()) {
          fieldAdapterMap.put(field, adapterFactory.create(field.normalizedType().get()));
        }
        for (FieldDescriptor field : readInfo.getterMethodMap().keySet()) {
          fieldAdapterMap.put(field, adapterFactory.create(field.normalizedType().get()));
        }
      }
      return fieldAdapterMap.build();
    }
  }
}
