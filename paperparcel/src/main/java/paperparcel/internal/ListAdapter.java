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
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ListAdapter<T> implements TypeAdapter<List<T>> {
  private final TypeAdapter<T> itemAdapter;

  public ListAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @Nullable @Override public List<T> readFromParcel(@NonNull Parcel source) {
    List<T> value = null;
    if (source.readInt() == 1) {
      int size = source.readInt();
      value = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        value.add(itemAdapter.readFromParcel(source));
      }
    }
    return value;
  }

  @Override
  public void writeToParcel(@Nullable List<T> value, @NonNull Parcel dest, int flags) {
    if (value == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(1);
      dest.writeInt(value.size());
      for (int i = 0; i < value.size(); i++) {
        T item = value.get(i);
        itemAdapter.writeToParcel(item, dest, flags);
      }
    }
  }
}
