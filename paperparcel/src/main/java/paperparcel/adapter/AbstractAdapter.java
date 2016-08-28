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

package paperparcel.adapter;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import paperparcel.TypeAdapter;

/** Convenience base class for {@link TypeAdapter}s that handles null-checking */
public abstract class AbstractAdapter<T> implements TypeAdapter<T> {
  @Nullable @Override public final T readFromParcel(@NonNull Parcel source) {
    T value = null;
    if (source.readInt() == 1) {
      value = read(source);
    }
    return value;
  }

  @NonNull protected abstract T read(@NonNull Parcel source);

  @Override public final void writeToParcel(@Nullable T value, @NonNull Parcel dest, int flags) {
    if (value == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(1);
      write(value, dest, flags);
    }
  }

  protected abstract void write(@NonNull T value, @NonNull Parcel dest, int flags);
}
