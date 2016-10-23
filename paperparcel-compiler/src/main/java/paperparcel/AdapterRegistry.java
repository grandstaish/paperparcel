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
      "paperparcel.adapter.StringAdapter",
      "paperparcel.adapter.IntegerAdapter",
      "paperparcel.adapter.MapAdapter",
      "paperparcel.adapter.BundleAdapter",
      "paperparcel.adapter.PersistableBundleAdapter",
      "paperparcel.adapter.ParcelableAdapter",
      "paperparcel.adapter.ShortAdapter",
      "paperparcel.adapter.LongAdapter",
      "paperparcel.adapter.FloatAdapter",
      "paperparcel.adapter.DoubleAdapter",
      "paperparcel.adapter.BooleanAdapter",
      "paperparcel.adapter.CharSequenceAdapter",
      "paperparcel.adapter.ListAdapter",
      "paperparcel.adapter.SparseArrayAdapter",
      "paperparcel.adapter.BooleanArrayAdapter",
      "paperparcel.adapter.ByteArrayAdapter",
      "paperparcel.adapter.StringArrayAdapter",
      "paperparcel.adapter.IBinderAdapter",
      "paperparcel.adapter.IntArrayAdapter",
      "paperparcel.adapter.LongArrayAdapter",
      "paperparcel.adapter.ByteAdapter",
      "paperparcel.adapter.SizeAdapter",
      "paperparcel.adapter.SizeFAdapter",
      "paperparcel.adapter.DoubleArrayAdapter",
      "paperparcel.adapter.LongSparseArrayAdapter",
      "paperparcel.adapter.SparseBooleanArrayAdapter",
      "paperparcel.adapter.SparseIntArrayAdapter",
      "paperparcel.adapter.SparseLongArrayAdapter",
      "paperparcel.adapter.CollectionAdapter",
      "paperparcel.adapter.ArrayListAdapter",
      "paperparcel.adapter.ArrayMapAdapter",
      "paperparcel.adapter.ArrayDequeAdapter",
      "paperparcel.adapter.ArraySetAdapter",
      "paperparcel.adapter.SetAdapter",
      "paperparcel.adapter.QueueAdapter",
      "paperparcel.adapter.DequeAdapter",
      "paperparcel.adapter.LinkedHashMapAdapter",
      "paperparcel.adapter.LinkedHashSetAdapter",
      "paperparcel.adapter.LinkedListAdapter",
      "paperparcel.adapter.HashMapAdapter",
      "paperparcel.adapter.HashSetAdapter",
      "paperparcel.adapter.SortedMapAdapter",
      "paperparcel.adapter.SortedSetAdapter",
      "paperparcel.adapter.TreeMapAdapter",
      "paperparcel.adapter.TreeSetAdapter",
      "paperparcel.adapter.CharArrayAdapter",
      "paperparcel.adapter.FloatArrayAdapter",
      "paperparcel.adapter.ShortArrayAdapter",
      "paperparcel.adapter.BigDecimalAdapter",
      "paperparcel.adapter.BigIntegerAdapter",
      "paperparcel.adapter.DateAdapter",
      "paperparcel.adapter.CharacterAdapter",
      "paperparcel.adapter.EnumAdapter");

  private final List<String> adapterNames = Lists.newArrayList(BUILT_IN_ADAPTER_CLASS_NAMES);
  private final Map<TypeName, Adapter> adapters = Maps.newLinkedHashMap();

  void registerAdapter(String qualifiedName) {
    adapterNames.add(0, qualifiedName);
  }

  ImmutableList<String> getAdapterNames() {
    return ImmutableList.copyOf(adapterNames);
  }

  void registerAdapterFor(TypeName normalizedType, Adapter adapter) {
    adapters.put(normalizedType, adapter);
  }

  Optional<Adapter> getAdapterFor(TypeName normalizedType) {
    return Optional.fromNullable(adapters.get(normalizedType));
  }
}
