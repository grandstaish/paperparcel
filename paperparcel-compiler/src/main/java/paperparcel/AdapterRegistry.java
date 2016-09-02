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
      "paperparcel.adapter.ArrayDequeAdapter",
      "paperparcel.adapter.ArrayListAdapter",
      "paperparcel.adapter.ArrayMapAdapter",
      "paperparcel.adapter.ArraySetAdapter",
      "paperparcel.adapter.BigDecimalAdapter",
      "paperparcel.adapter.BigIntegerAdapter",
      "paperparcel.adapter.BooleanAdapter",
      "paperparcel.adapter.BooleanArrayAdapter",
      "paperparcel.adapter.ByteAdapter",
      "paperparcel.adapter.ByteArrayAdapter",
      "paperparcel.adapter.CharacterAdapter",
      "paperparcel.adapter.CharArrayAdapter",
      "paperparcel.adapter.CharSequenceAdapter",
      "paperparcel.adapter.CollectionAdapter",
      "paperparcel.adapter.DateAdapter",
      "paperparcel.adapter.DequeAdapter",
      "paperparcel.adapter.DoubleAdapter",
      "paperparcel.adapter.DoubleArrayAdapter",
      "paperparcel.adapter.EnumAdapter",
      "paperparcel.adapter.FloatAdapter",
      "paperparcel.adapter.FloatArrayAdapter",
      "paperparcel.adapter.HashMapAdapter",
      "paperparcel.adapter.HashSetAdapter",
      "paperparcel.adapter.IBinderAdapter",
      "paperparcel.adapter.IntArrayAdapter",
      "paperparcel.adapter.IntegerAdapter",
      "paperparcel.adapter.LinkedHashMapAdapter",
      "paperparcel.adapter.LinkedHashSetAdapter",
      "paperparcel.adapter.LinkedListAdapter",
      "paperparcel.adapter.ListAdapter",
      "paperparcel.adapter.LongAdapter",
      "paperparcel.adapter.LongArrayAdapter",
      "paperparcel.adapter.LongSparseArrayAdapter",
      "paperparcel.adapter.MapAdapter",
      "paperparcel.adapter.ParcelableAdapter",
      "paperparcel.adapter.QueueAdapter",
      "paperparcel.adapter.SetAdapter",
      "paperparcel.adapter.ShortAdapter",
      "paperparcel.adapter.ShortArrayAdapter",
      "paperparcel.adapter.SizeAdapter",
      "paperparcel.adapter.SizeFAdapter",
      "paperparcel.adapter.SortedMapAdapter",
      "paperparcel.adapter.SortedSetAdapter",
      "paperparcel.adapter.SparseArrayAdapter",
      "paperparcel.adapter.SparseBooleanArrayAdapter",
      "paperparcel.adapter.SparseIntArrayAdapter",
      "paperparcel.adapter.SparseLongArrayAdapter",
      "paperparcel.adapter.StringAdapter",
      "paperparcel.adapter.StringArrayAdapter",
      "paperparcel.adapter.TreeMapAdapter",
      "paperparcel.adapter.TreeSetAdapter");

  private final List<String> adapterNames = Lists.newArrayList(BUILT_IN_ADAPTER_CLASS_NAMES);
  private final Map<TypeName, Adapter> adapters = Maps.newLinkedHashMap();

  void registerAdapter(String qualifiedName) {
    adapterNames.add(qualifiedName);
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
