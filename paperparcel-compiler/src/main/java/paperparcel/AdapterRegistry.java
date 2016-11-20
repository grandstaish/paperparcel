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
  @AutoValue
  static abstract class Entry {
    abstract String qualifiedName();
    abstract boolean nullSafe();

    static Entry create(String qualifiedName, boolean nullSafe) {
      return new AutoValue_AdapterRegistry_Entry(qualifiedName, nullSafe);
    }
  }

  private static final ImmutableList<Entry> BUILT_IN_ADAPTER_CLASS_NAMES = ImmutableList.of(
      Entry.create("paperparcel.internal.StringAdapter", true),
      Entry.create("paperparcel.internal.IntegerAdapter", false),
      Entry.create("paperparcel.internal.MapAdapter", false),
      Entry.create("paperparcel.internal.BundleAdapter", true),
      Entry.create("paperparcel.internal.PersistableBundleAdapter", true),
      Entry.create("paperparcel.internal.ParcelableAdapter", true),
      Entry.create("paperparcel.internal.ShortAdapter", false),
      Entry.create("paperparcel.internal.LongAdapter", false),
      Entry.create("paperparcel.internal.FloatAdapter", false),
      Entry.create("paperparcel.internal.DoubleAdapter", false),
      Entry.create("paperparcel.internal.BooleanAdapter", false),
      Entry.create("paperparcel.internal.CharSequenceAdapter", true),
      Entry.create("paperparcel.internal.ListAdapter", false),
      Entry.create("paperparcel.internal.SparseArrayAdapter", false),
      Entry.create("paperparcel.internal.BooleanArrayAdapter", true),
      Entry.create("paperparcel.internal.ByteArrayAdapter", true),
      Entry.create("paperparcel.internal.StringArrayAdapter", true),
      Entry.create("paperparcel.internal.IBinderAdapter", true),
      Entry.create("paperparcel.internal.IntArrayAdapter", true),
      Entry.create("paperparcel.internal.LongArrayAdapter", true),
      Entry.create("paperparcel.internal.ByteAdapter", false),
      Entry.create("paperparcel.internal.SizeAdapter", false),
      Entry.create("paperparcel.internal.SizeFAdapter", false),
      Entry.create("paperparcel.internal.DoubleArrayAdapter", true),
      Entry.create("paperparcel.internal.LongSparseArrayAdapter", false),
      Entry.create("paperparcel.internal.SparseBooleanArrayAdapter", true),
      Entry.create("paperparcel.internal.SparseIntArrayAdapter", false),
      Entry.create("paperparcel.internal.SparseLongArrayAdapter", false),
      Entry.create("paperparcel.internal.CollectionAdapter", false),
      Entry.create("paperparcel.internal.ArrayMapAdapter", false),
      Entry.create("paperparcel.internal.ArraySetAdapter", false),
      Entry.create("paperparcel.internal.SetAdapter", false),
      Entry.create("paperparcel.internal.CharArrayAdapter", true),
      Entry.create("paperparcel.internal.FloatArrayAdapter", true),
      Entry.create("paperparcel.internal.ShortArrayAdapter", false),
      Entry.create("paperparcel.internal.CharacterAdapter", false),
      Entry.create("paperparcel.internal.EnumAdapter", false));

  private final List<Entry> adapterNames = Lists.newArrayList(BUILT_IN_ADAPTER_CLASS_NAMES);
  private final Map<TypeName, Adapter> adapters = Maps.newLinkedHashMap();

  void registerAdapter(String qualifiedName, boolean nullSafe) {
    adapterNames.add(0, Entry.create(qualifiedName, nullSafe));
  }

  ImmutableList<Entry> getAdapterEntries() {
    return ImmutableList.copyOf(adapterNames);
  }

  void registerAdapterFor(TypeName fieldType, Adapter adapter) {
    adapters.put(fieldType, adapter);
  }

  Optional<Adapter> getAdapterFor(TypeName fieldType) {
    return Optional.fromNullable(adapters.get(fieldType));
  }
}
