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
import android.support.annotation.NonNull;
import android.util.SparseArray;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class SparseArrayAdapter<T> extends AbstractAdapter<SparseArray<T>> {
  private final TypeAdapter<T> itemAdapter;

  public SparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @NonNull @Override protected SparseArray<T> read(@NonNull Parcel source) {
    int size = source.readInt();
    SparseArray<T> sparseArray = new SparseArray<>(size);
    for (int i = 0; i < size; i++) {
      sparseArray.put(source.readInt(), itemAdapter.readFromParcel(source));
    }
    return sparseArray;
  }

  @Override protected void write(@NonNull SparseArray<T> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (int i = 0; i < value.size(); i++) {
      int key = value.keyAt(i);
      dest.writeInt(key);
      itemAdapter.writeToParcel(value.get(key), dest, flags);
    }
  }
}
