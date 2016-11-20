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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArraySet;
import paperparcel.TypeAdapter;

@TargetApi(Build.VERSION_CODES.M)
@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ArraySetAdapter<T> implements TypeAdapter<ArraySet<T>> {
  private final TypeAdapter<T> itemAdapter;

  public ArraySetAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @Nullable @Override public ArraySet<T> readFromParcel(@NonNull Parcel source) {
    ArraySet<T> value = null;
    if (source.readInt() == 1) {
      int size = source.readInt();
      value = new ArraySet<>(size);
      for (int i = 0; i < size; i++) {
        value.add(itemAdapter.readFromParcel(source));
      }
    }
    return value;
  }

  @Override
  public void writeToParcel(@Nullable ArraySet<T> value, @NonNull Parcel dest, int flags) {
    if (value == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(1);
      dest.writeInt(value.size());
      for (int i = 0; i < value.size(); i++) {
        T item = value.valueAt(i);
        itemAdapter.writeToParcel(item, dest, flags);
      }
    }
  }
}
