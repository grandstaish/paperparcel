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
import android.util.SparseArray;

import androidx.annotation.NonNull;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class SparseArrayAdapter<T> implements TypeAdapter<SparseArray<T>> {
  private final TypeAdapter<T> itemAdapter;

  public SparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @NonNull @Override public SparseArray<T> readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    SparseArray<T> sparseArray = new SparseArray<>(size);
    for (int i = 0; i < size; i++) {
      sparseArray.put(source.readInt(), itemAdapter.readFromParcel(source));
    }
    return sparseArray;
  }

  @Override public void writeToParcel(@NonNull SparseArray<T> value, @NonNull Parcel dest, int flags) {
    int size = value.size();
    dest.writeInt(size);
    for (int i = 0; i < size; i++) {
      int key = value.keyAt(i);
      dest.writeInt(key);
      itemAdapter.writeToParcel(value.get(key), dest, flags);
    }
  }
}
