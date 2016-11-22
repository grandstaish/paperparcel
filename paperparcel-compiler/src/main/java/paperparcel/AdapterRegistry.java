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
  static abstract class Entry {
    abstract boolean nullSafe();
  }

  @AutoValue
  static abstract class ClassEntry extends Entry {
    abstract String qualifiedName();

    static ClassEntry create(String qualifiedName, boolean nullSafe) {
      return new AutoValue_AdapterRegistry_ClassEntry(nullSafe, qualifiedName);
    }
  }

  @AutoValue
  static abstract class FieldEntry extends Entry {
    abstract String enclosingClass();
    abstract String fieldName();

    static FieldEntry create(String enclosingClass, String fieldName, boolean nullSafe) {
      return new AutoValue_AdapterRegistry_FieldEntry(nullSafe, enclosingClass, fieldName);
    }
  }

  private static final ImmutableList<Entry> BUILT_IN_ADAPTER_ENTRIES = ImmutableList.of(
      FieldEntry.create("paperparcel.internal.StaticAdapters", "STRING_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "INTEGER_ADAPTER", false),
      ClassEntry.create("paperparcel.internal.MapAdapter", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "BUNDLE_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "PERSISTABLE_BUNDLE_ADAPTER", true),
      ClassEntry.create("paperparcel.internal.ParcelableAdapter", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "SHORT_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "LONG_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "FLOAT_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "DOUBLE_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "BOOLEAN_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "CHAR_SEQUENCE_ADAPTER", true),
      ClassEntry.create("paperparcel.internal.ListAdapter", false),
      ClassEntry.create("paperparcel.internal.SparseArrayAdapter", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "BOOLEAN_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "BYTE_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "STRING_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "IBINDER_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "INT_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "LONG_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "BYTE_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "SIZE_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "SIZE_F_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "DOUBLE_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "SPARSE_BOOLEAN_ARRAY_ADAPTER", true),
      ClassEntry.create("paperparcel.internal.CollectionAdapter", false),
      ClassEntry.create("paperparcel.internal.SetAdapter", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "CHAR_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "FLOAT_ARRAY_ADAPTER", true),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "SHORT_ARRAY_ADAPTER", false),
      FieldEntry.create("paperparcel.internal.StaticAdapters", "CHARACTER_ADAPTER", false),
      ClassEntry.create("paperparcel.internal.EnumAdapter", false));

  private final List<Entry> entries = Lists.newArrayList(BUILT_IN_ADAPTER_ENTRIES);
  private final Map<TypeName, Adapter> adapters = Maps.newLinkedHashMap();

  void addClassEntry(String qualifiedName, boolean nullSafe) {
    entries.add(0, ClassEntry.create(qualifiedName, nullSafe));
  }

  // TODO(brad): add support for users to add field adapters?
  //void addFieldEntry(String enclosingClass, String fieldName, boolean nullSafe) {
  //  entries.add(0, FieldEntry.create(enclosingClass, fieldName, nullSafe));
  //}

  List<Entry> getEntries() {
    return entries;
  }

  void registerAdapterFor(TypeName fieldType, Adapter adapter) {
    adapters.put(fieldType, adapter);
  }

  Optional<Adapter> getAdapterFor(TypeName fieldType) {
    return Optional.fromNullable(adapters.get(fieldType));
  }
}
