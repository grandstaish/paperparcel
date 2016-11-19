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
      "paperparcel.internal.adapter.StringAdapter",
      "paperparcel.internal.adapter.IntegerAdapter",
      "paperparcel.internal.adapter.MapAdapter",
      "paperparcel.internal.adapter.BundleAdapter",
      "paperparcel.internal.adapter.PersistableBundleAdapter",
      "paperparcel.internal.adapter.ParcelableAdapter",
      "paperparcel.internal.adapter.ShortAdapter",
      "paperparcel.internal.adapter.LongAdapter",
      "paperparcel.internal.adapter.FloatAdapter",
      "paperparcel.internal.adapter.DoubleAdapter",
      "paperparcel.internal.adapter.BooleanAdapter",
      "paperparcel.internal.adapter.CharSequenceAdapter",
      "paperparcel.internal.adapter.ListAdapter",
      "paperparcel.internal.adapter.SparseArrayAdapter",
      "paperparcel.internal.adapter.BooleanArrayAdapter",
      "paperparcel.internal.adapter.ByteArrayAdapter",
      "paperparcel.internal.adapter.StringArrayAdapter",
      "paperparcel.internal.adapter.IBinderAdapter",
      "paperparcel.internal.adapter.IntArrayAdapter",
      "paperparcel.internal.adapter.LongArrayAdapter",
      "paperparcel.internal.adapter.ByteAdapter",
      "paperparcel.internal.adapter.SizeAdapter",
      "paperparcel.internal.adapter.SizeFAdapter",
      "paperparcel.internal.adapter.DoubleArrayAdapter",
      "paperparcel.internal.adapter.LongSparseArrayAdapter",
      "paperparcel.internal.adapter.SparseBooleanArrayAdapter",
      "paperparcel.internal.adapter.SparseIntArrayAdapter",
      "paperparcel.internal.adapter.SparseLongArrayAdapter",
      "paperparcel.internal.adapter.CollectionAdapter",
      "paperparcel.internal.adapter.ArrayListAdapter",
      "paperparcel.internal.adapter.ArrayMapAdapter",
      "paperparcel.internal.adapter.ArrayDequeAdapter",
      "paperparcel.internal.adapter.ArraySetAdapter",
      "paperparcel.internal.adapter.SetAdapter",
      "paperparcel.internal.adapter.QueueAdapter",
      "paperparcel.internal.adapter.DequeAdapter",
      "paperparcel.internal.adapter.LinkedHashMapAdapter",
      "paperparcel.internal.adapter.LinkedHashSetAdapter",
      "paperparcel.internal.adapter.LinkedListAdapter",
      "paperparcel.internal.adapter.HashMapAdapter",
      "paperparcel.internal.adapter.HashSetAdapter",
      "paperparcel.internal.adapter.SortedMapAdapter",
      "paperparcel.internal.adapter.SortedSetAdapter",
      "paperparcel.internal.adapter.TreeMapAdapter",
      "paperparcel.internal.adapter.TreeSetAdapter",
      "paperparcel.internal.adapter.CharArrayAdapter",
      "paperparcel.internal.adapter.FloatArrayAdapter",
      "paperparcel.internal.adapter.ShortArrayAdapter",
      "paperparcel.internal.adapter.BigDecimalAdapter",
      "paperparcel.internal.adapter.BigIntegerAdapter",
      "paperparcel.internal.adapter.DateAdapter",
      "paperparcel.internal.adapter.CharacterAdapter",
      "paperparcel.internal.adapter.EnumAdapter");

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
