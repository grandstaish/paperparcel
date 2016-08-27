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
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javax.lang.model.type.TypeMirror;

/**
 * Represents the read/write Parcelable implementations for a {@link PaperParcel} annotated class
 */
@AutoValue
abstract class ParcelableImplDescriptor {

  /** Returns a description of the annotated class */
  abstract PaperParcelDescriptor paperParcelClass();

  /**
   * Returns all of the adapters required for each field in the annotated class, indexed by the
   * field types
   */
  abstract ImmutableMap<Equivalence.Wrapper<TypeMirror>, AdapterGraph> adapters();

  static final class Factory {
    private final AdapterGraph.Factory adapterGraphFactory;

    Factory(
        AdapterGraph.Factory adapterGraphFactory) {
      this.adapterGraphFactory = adapterGraphFactory;
    }

    ParcelableImplDescriptor create(PaperParcelDescriptor descriptor) {
      ImmutableMap<Equivalence.Wrapper<TypeMirror>, AdapterGraph> adapters =
          getAdapterMap(descriptor.fields());
      return new AutoValue_ParcelableImplDescriptor(descriptor, adapters);
    }

    private ImmutableMap<Equivalence.Wrapper<TypeMirror>, AdapterGraph> getAdapterMap(
        ImmutableList<FieldDescriptor> fields) {
      return FluentIterable.from(fields)
          .transform(new Function<FieldDescriptor, Equivalence.Wrapper<TypeMirror>>() {
            @Override public Equivalence.Wrapper<TypeMirror> apply(FieldDescriptor field) {
              return field.normalizedType();
            }
          })
          .toMap(new Function<Equivalence.Wrapper<TypeMirror>, AdapterGraph>() {
            @Override public AdapterGraph apply(Equivalence.Wrapper<TypeMirror> type) {
              return adapterGraphFactory.create(type.get());
            }
          });
    }
  }
}
