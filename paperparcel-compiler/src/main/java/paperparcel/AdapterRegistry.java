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

import android.support.annotation.NonNull;
import com.google.auto.common.MoreTypes;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import paperparcel.TypeKey.*;

/**
 * Responsible for caching the qualified class names of all of the TypeAdapters discovered across
 * the various processing rounds and also cache {@link AdapterDescriptor} instances as these can be
 * expensive to create.
 */
final class AdapterRegistry {
  private static final TypeKey STRING = ClassKey.get("java.lang.String");
  private static final TypeKey INTEGER = ClassKey.get("java.lang.Integer");
  private static final TypeKey MAP =
      ParameterizedKey.get(ClassKey.get("java.util.Map"), asList(AnyKey.get("K"), AnyKey.get("V")));
  private static final TypeKey BUNDLE = ClassKey.get("android.os.Bundle");
  private static final TypeKey PERSISTABLE_BUNDLE = ClassKey.get("android.os.PersistableBundle");
  private static final TypeKey PARCELABLE =
      BoundedKey.get("T", asList(ClassKey.get("android.os.Parcelable")));
  private static final TypeKey SHORT = ClassKey.get("java.lang.Short");
  private static final TypeKey LONG = ClassKey.get("java.lang.Long");
  private static final TypeKey FLOAT = ClassKey.get("java.lang.Float");
  private static final TypeKey DOUBLE = ClassKey.get("java.lang.Double");
  private static final TypeKey BOOLEAN = ClassKey.get("java.lang.Boolean");
  private static final TypeKey CHAR_SEQUENCE = ClassKey.get("java.lang.CharSequence");
  private static final TypeKey LIST =
      ParameterizedKey.get(ClassKey.get("java.util.List"), asList(AnyKey.get("T")));
  private static final TypeKey SPARSE_ARRAY =
      ParameterizedKey.get(ClassKey.get("android.util.SparseArray"), asList(AnyKey.get("T")));
  private static final TypeKey BOOLEAN_ARRAY = PrimitiveArrayKey.BOOLEAN_ARRAY;
  private static final TypeKey BYTE_ARRAY = PrimitiveArrayKey.BYTE_ARRAY;
  private static final TypeKey IBINDER = ClassKey.get("android.os.IBinder");
  private static final TypeKey INT_ARRAY = PrimitiveArrayKey.INT_ARRAY;
  private static final TypeKey LONG_ARRAY = PrimitiveArrayKey.LONG_ARRAY;
  private static final TypeKey BYTE = ClassKey.get("java.lang.Byte");
  private static final TypeKey SIZE = ClassKey.get("android.util.Size");
  private static final TypeKey SIZE_F = ClassKey.get("android.util.SizeF");
  private static final TypeKey DOUBLE_ARRAY = PrimitiveArrayKey.DOUBLE_ARRAY;
  private static final TypeKey SPARSE_BOOLEAN_ARRAY = ClassKey.get("android.util.SparseBooleanArray");
  private static final TypeKey COLLECTION =
      ParameterizedKey.get(ClassKey.get("java.util.Collection"), asList(AnyKey.get("T")));
  private static final TypeKey OBJECT_ARRAY = ArrayKey.of(AnyKey.get("T"));
  private static final TypeKey SET =
      ParameterizedKey.get(ClassKey.get("java.util.Set"), asList(AnyKey.get("T")));
  private static final TypeKey CHAR_ARRAY = PrimitiveArrayKey.CHAR_ARRAY;
  private static final TypeKey FLOAT_ARRAY = PrimitiveArrayKey.FLOAT_ARRAY;
  private static final TypeKey SHORT_ARRAY = PrimitiveArrayKey.SHORT_ARRAY;
  private static final TypeKey CHARACTER = ClassKey.get("java.lang.Character");
  private static final TypeKey ENUM =
      BoundedKey.get("T", asList(
          ParameterizedKey.get(ClassKey.get("java.lang.Enum"), asList(AnyKey.get("T")))));

  private static final String STATIC_ADAPTERS = "paperparcel.internal.StaticAdapters";

  private static final int DEFAULT_PRIORITY = 150; // Between Priority.HIGH and Priority.LOW.

  private static ImmutableList<TypeKey> asList(TypeKey... keys) {
    ImmutableList.Builder<TypeKey> builder = ImmutableList.builder();
    for (TypeKey key : keys) {
      builder.add(key);
    }
    return builder.build();
  }

  static abstract class Entry implements Comparable<Entry> {
    abstract TypeKey typeKey();
    abstract int priority();
    abstract boolean nullSafe();

    @Override public int compareTo(@NonNull Entry entry) {
      return Ints.compare(priority(), entry.priority());
    }
  }

  @AutoValue
  static abstract class ClassEntry extends Entry {
    abstract String qualifiedName();

    static ClassEntry create(String qualifiedName, TypeKey key, boolean nullSafe) {
      return create(qualifiedName, key, DEFAULT_PRIORITY, nullSafe);
    }

    static ClassEntry create(
        String qualifiedName, TypeKey key, int priority, boolean nullSafe) {
      return new AutoValue_AdapterRegistry_ClassEntry(key, priority, nullSafe, qualifiedName);
    }
  }

  @AutoValue
  static abstract class FieldEntry extends Entry {
    abstract String enclosingClass();
    abstract String fieldName();

    static FieldEntry create(
        String enclosingClass, String fieldName, TypeKey key, boolean nullSafe) {
      return new AutoValue_AdapterRegistry_FieldEntry(
          key, DEFAULT_PRIORITY, nullSafe, enclosingClass, fieldName);
    }
  }

  private static final ImmutableList<Entry> BUILT_IN_ADAPTER_ENTRIES = ImmutableList.of(
      FieldEntry.create(STATIC_ADAPTERS, "STRING_ADAPTER", STRING, true),
      FieldEntry.create(STATIC_ADAPTERS, "INTEGER_ADAPTER", INTEGER, false),
      ClassEntry.create("paperparcel.internal.MapAdapter", MAP, false),
      FieldEntry.create(STATIC_ADAPTERS, "BUNDLE_ADAPTER", BUNDLE, true),
      FieldEntry.create(STATIC_ADAPTERS, "PERSISTABLE_BUNDLE_ADAPTER", PERSISTABLE_BUNDLE, true),
      ClassEntry.create("paperparcel.internal.ParcelableAdapter", PARCELABLE, true),
      FieldEntry.create(STATIC_ADAPTERS, "SHORT_ADAPTER", SHORT, false),
      FieldEntry.create(STATIC_ADAPTERS, "LONG_ADAPTER", LONG, false),
      FieldEntry.create(STATIC_ADAPTERS, "FLOAT_ADAPTER", FLOAT, false),
      FieldEntry.create(STATIC_ADAPTERS, "DOUBLE_ADAPTER", DOUBLE, false),
      FieldEntry.create(STATIC_ADAPTERS, "BOOLEAN_ADAPTER", BOOLEAN, false),
      FieldEntry.create(STATIC_ADAPTERS, "CHAR_SEQUENCE_ADAPTER", CHAR_SEQUENCE, true),
      ClassEntry.create("paperparcel.internal.ListAdapter", LIST, false),
      ClassEntry.create("paperparcel.internal.SparseArrayAdapter", SPARSE_ARRAY, false),
      FieldEntry.create(STATIC_ADAPTERS, "BOOLEAN_ARRAY_ADAPTER", BOOLEAN_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "BYTE_ARRAY_ADAPTER", BYTE_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "IBINDER_ADAPTER", IBINDER, true),
      FieldEntry.create(STATIC_ADAPTERS, "INT_ARRAY_ADAPTER", INT_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "LONG_ARRAY_ADAPTER", LONG_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "BYTE_ADAPTER", BYTE, false),
      FieldEntry.create(STATIC_ADAPTERS, "SIZE_ADAPTER", SIZE, false),
      FieldEntry.create(STATIC_ADAPTERS, "SIZE_F_ADAPTER", SIZE_F, false),
      FieldEntry.create(STATIC_ADAPTERS, "DOUBLE_ARRAY_ADAPTER", DOUBLE_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "SPARSE_BOOLEAN_ARRAY_ADAPTER", SPARSE_BOOLEAN_ARRAY, true),
      ClassEntry.create("paperparcel.internal.CollectionAdapter", COLLECTION, false),
      ClassEntry.create("paperparcel.internal.ArrayAdapter", OBJECT_ARRAY, false),
      ClassEntry.create("paperparcel.internal.SetAdapter", SET, false),
      FieldEntry.create(STATIC_ADAPTERS, "CHAR_ARRAY_ADAPTER", CHAR_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "FLOAT_ARRAY_ADAPTER", FLOAT_ARRAY, true),
      FieldEntry.create(STATIC_ADAPTERS, "SHORT_ARRAY_ADAPTER", SHORT_ARRAY, false),
      FieldEntry.create(STATIC_ADAPTERS, "CHARACTER_ADAPTER", CHARACTER, false),
      ClassEntry.create("paperparcel.internal.EnumAdapter", ENUM, false));

  private final List<Entry> entries = Lists.newArrayList(BUILT_IN_ADAPTER_ENTRIES);
  private final Map<TypeName, AdapterDescriptor> adapters = Maps.newLinkedHashMap();

  private final Elements elements;
  private final Types types;

  AdapterRegistry(
      Elements elements,
      Types types) {
    this.elements = elements;
    this.types = types;
  }

  void addClassEntry(TypeElement element, int priority, boolean nullSafe) {
    String qualifiedName = element.getQualifiedName().toString();
    TypeKey key = TypeKey.get(
        Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(element.asType())));
    Entry entry = ClassEntry.create(qualifiedName, key, priority, nullSafe);
    int size = entries.size();
    int i = 0;
    while (i < size && entries.get(i).compareTo(entry) > 0) {
      i++;
    }
    entries.add(i, entry);
  }

  boolean contains(TypeElement element) {
    for (Entry entry : entries) {
      if (entry instanceof ClassEntry) {
        if (element.getQualifiedName().contentEquals(((ClassEntry) entry).qualifiedName())) {
          return true;
        }
      }
    }
    return false;
  }

  List<Entry> getEntries() {
    return entries;
  }

  void registerAdapterFor(TypeName fieldType, AdapterDescriptor adapter) {
    adapters.put(fieldType, adapter);
  }

  Optional<AdapterDescriptor> getAdapterFor(TypeName fieldType) {
    return Optional.fromNullable(adapters.get(fieldType));
  }
}
