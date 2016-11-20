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
import android.util.ArrayMap;
import java.util.Map;
import paperparcel.TypeAdapter;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ArrayMapAdapter<K, V> implements TypeAdapter<ArrayMap<K, V>> {
  private final TypeAdapter<K> keyAdapter;
  private final TypeAdapter<V> valueAdapter;

  public ArrayMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    this.keyAdapter = keyAdapter;
    this.valueAdapter = valueAdapter;
  }

  @NonNull @Override public ArrayMap<K, V> readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    ArrayMap<K, V> map = new ArrayMap<>(size);
    for (int i = 0; i < size; i++) {
      map.put(keyAdapter.readFromParcel(source), valueAdapter.readFromParcel(source));
    }
    return map;
  }

  @Override
  public void writeToParcel(@NonNull ArrayMap<K, V> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (Map.Entry<K, V> entry : value.entrySet()) {
      keyAdapter.writeToParcel(entry.getKey(), dest, flags);
      valueAdapter.writeToParcel(entry.getValue(), dest, flags);
    }
  }
}
