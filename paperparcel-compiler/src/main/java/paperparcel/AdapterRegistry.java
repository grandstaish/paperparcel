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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Map;

/**
 * Responsible for caching the qualified class names of all of the TypeAdapters discovered across
 * the various processing rounds and also cache {@link Adapter} instances as these can be
 * expensive to create.
 */
final class AdapterRegistry {
  private static final ImmutableList<String> BUILT_IN_ADAPTER_CLASS_NAMES = ImmutableList.of(
      "paperparcel.internal.StringAdapter",
      "paperparcel.internal.IntegerAdapter",
      "paperparcel.internal.MapAdapter",
      "paperparcel.internal.BundleAdapter",
      "paperparcel.internal.PersistableBundleAdapter",
      "paperparcel.internal.ParcelableAdapter",
      "paperparcel.internal.ShortAdapter",
      "paperparcel.internal.LongAdapter",
      "paperparcel.internal.FloatAdapter",
      "paperparcel.internal.DoubleAdapter",
      "paperparcel.internal.BooleanAdapter",
      "paperparcel.internal.CharSequenceAdapter",
      "paperparcel.internal.ListAdapter",
      "paperparcel.internal.SparseArrayAdapter",
      "paperparcel.internal.BooleanArrayAdapter",
      "paperparcel.internal.ByteArrayAdapter",
      "paperparcel.internal.StringArrayAdapter",
      "paperparcel.internal.IBinderAdapter",
      "paperparcel.internal.IntArrayAdapter",
      "paperparcel.internal.LongArrayAdapter",
      "paperparcel.internal.ByteAdapter",
      "paperparcel.internal.SizeAdapter",
      "paperparcel.internal.SizeFAdapter",
      "paperparcel.internal.DoubleArrayAdapter",
      "paperparcel.internal.LongSparseArrayAdapter",
      "paperparcel.internal.SparseBooleanArrayAdapter",
      "paperparcel.internal.SparseIntArrayAdapter",
      "paperparcel.internal.SparseLongArrayAdapter",
      "paperparcel.internal.CollectionAdapter",
      "paperparcel.internal.ArrayMapAdapter",
      "paperparcel.internal.ArraySetAdapter",
      "paperparcel.internal.SetAdapter",
      "paperparcel.internal.CharArrayAdapter",
      "paperparcel.internal.FloatArrayAdapter",
      "paperparcel.internal.ShortArrayAdapter",
      "paperparcel.internal.CharacterAdapter",
      "paperparcel.internal.EnumAdapter");

  private final List<String> adapterNames = Lists.newArrayList(BUILT_IN_ADAPTER_CLASS_NAMES);
  private final Map<TypeName, Adapter> adapters = Maps.newLinkedHashMap();

  void registerAdapter(String qualifiedName) {
    adapterNames.add(0, qualifiedName);
  }

  ImmutableList<String> getAdapterNames() {
    return ImmutableList.copyOf(adapterNames);
  }

  void registerAdapterFor(TypeName fieldType, Adapter adapter) {
    adapters.put(fieldType, adapter);
  }

  Optional<Adapter> getAdapterFor(TypeName fieldType) {
    return Optional.fromNullable(adapters.get(fieldType));
  }
}
