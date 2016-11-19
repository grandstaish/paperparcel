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

package paperparcel.internal.adapter;

import android.os.Parcel;
import android.support.annotation.NonNull;
import java.util.Map;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

/**
 * A convenience base class for making it easy to write {@link TypeAdapter}s to handle classes
 * that implement the {@link Map} interface. The {@link Map} type must support the
 * {@link Map#put(Object, Object)} method.
 *
 * @param <M> The Map type to handle
 * @param <K> The Key type of the Map (may be generic)
 * @param <V> The Value type of the Map (may be generic)
 */
abstract class AbstractMutableMapAdapter<M extends Map<K, V>, K, V>
    extends AbstractAdapter<M> {
  private final TypeAdapter<K> keyAdapter;
  private final TypeAdapter<V> valueAdapter;

  AbstractMutableMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    this.keyAdapter = keyAdapter;
    this.valueAdapter = valueAdapter;
  }

  @NonNull @Override protected M read(@NonNull Parcel source) {
    int size = source.readInt();
    M map = newMap(size);
    for (int i = 0; i < size; i++) {
      map.put(keyAdapter.readFromParcel(source), valueAdapter.readFromParcel(source));
    }
    return map;
  }

  @Override protected void write(@NonNull M value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (Map.Entry<K, V> entry : value.entrySet()) {
      keyAdapter.writeToParcel(entry.getKey(), dest, flags);
      valueAdapter.writeToParcel(entry.getValue(), dest, flags);
    }
  }

  /** Creates a new instance of the {@link Map} of the given {@code size} */
  protected abstract M newMap(int size);
}
