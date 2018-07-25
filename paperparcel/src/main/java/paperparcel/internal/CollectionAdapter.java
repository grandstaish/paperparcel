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

package paperparcel.internal;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class CollectionAdapter<T> implements TypeAdapter<Collection<T>> {
  private final TypeAdapter<T> itemAdapter;

  public CollectionAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @NonNull @Override public Collection<T> readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    Collection<T> value = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      value.add(itemAdapter.readFromParcel(source));
    }
    return value;
  }

  @Override
  public void writeToParcel(@NonNull Collection<T> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (T item : value) {
      itemAdapter.writeToParcel(item, dest, flags);
    }
  }
}
