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
import java.lang.reflect.Array;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ArrayAdapter<T> implements TypeAdapter<T[]> {
  private final Class<T> componentType;
  private final TypeAdapter<T> componentAdapter;

  public ArrayAdapter(Class<T> componentType, TypeAdapter<T> componentAdapter) {
    this.componentType = componentType;
    this.componentAdapter = componentAdapter;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked") // Array.newInstance isn't generic.
  public T[] readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    T[] array = (T[]) Array.newInstance(componentType, size);
    for (int i = 0; i < size; i++) {
      array[i] = componentAdapter.readFromParcel(source);
    }
    return array;
  }

  @Override
  @SuppressWarnings("ForLoopReplaceableByForEach") // No allocations.
  public void writeToParcel(@NonNull T[] value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.length);
    for (int i = 0; i < value.length; i++) {
      componentAdapter.writeToParcel(value[i], dest, flags);
    }
  }
}
