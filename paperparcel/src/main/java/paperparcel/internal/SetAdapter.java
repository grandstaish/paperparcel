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
import java.util.LinkedHashSet;
import java.util.Set;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class SetAdapter<T> implements TypeAdapter<Set<T>> {
  private final TypeAdapter<T> itemAdapter;

  public SetAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @NonNull @Override public Set<T> readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    Set<T> value = new LinkedHashSet<>(size);
    for (int i = 0; i < size; i++) {
      value.add(itemAdapter.readFromParcel(source));
    }
    return value;
  }

  @Override
  public void writeToParcel(@NonNull Set<T> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (T item : value) {
      itemAdapter.writeToParcel(item, dest, flags);
    }
  }
}
