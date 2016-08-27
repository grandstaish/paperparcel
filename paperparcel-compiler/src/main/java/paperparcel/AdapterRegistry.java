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
import com.google.common.collect.Maps;
import com.squareup.javapoet.TypeName;
import java.util.Arrays;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Responsible for caching {@link AdapterDescriptor} and {@link AdapterGraph} instances as
 * these can be expensive to create.
 *
 * This class also initializes and caches all of the default {@link AdapterDescriptor}s that ship
 * with PaperParcel.
 */
final class AdapterRegistry {
  private static final ImmutableList<String> BUILT_IN_ADAPTER_CLASS_NAMES =
      ImmutableList.copyOf(Arrays.asList(
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
          "paperparcel.adapter.TreeSetAdapter"));

  private final Elements elements;
  private final AdapterDescriptor.Factory adapterDescriptorFactory;
  private final Map<TypeName, AdapterDescriptor> adapters = Maps.newLinkedHashMap();
  private final Map<TypeName, AdapterGraph> graphs = Maps.newLinkedHashMap();

  AdapterRegistry(
      Elements elements,
      AdapterDescriptor.Factory adapterDescriptorFactory) {
    this.elements = elements;
    this.adapterDescriptorFactory = adapterDescriptorFactory;
    addBuiltInAdapters();
  }

  void registerAdapter(AdapterDescriptor descriptor) {
    adapters.put(descriptor.adaptedType(), descriptor);
  }

  void registerGraph(TypeName normalizedType, AdapterGraph adapterGraph) {
    graphs.put(normalizedType, adapterGraph);
  }

  Optional<AdapterDescriptor> getAdapter(TypeName type) {
    return Optional.fromNullable(adapters.get(type));
  }

  Optional<AdapterGraph> getGraph(TypeName normalizedType) {
    return Optional.fromNullable(graphs.get(normalizedType));
  }

  private void addBuiltInAdapters() {
    for (String adapterClassName : BUILT_IN_ADAPTER_CLASS_NAMES) {
      TypeElement element = elements.getTypeElement(adapterClassName);
      if (element == null) throw new TypeNotPresentException(adapterClassName, null);
      registerAdapter(adapterDescriptorFactory.fromAdapterElement(element));
    }
  }
}
