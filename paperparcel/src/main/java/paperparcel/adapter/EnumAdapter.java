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

public final class EnumAdapter<T extends Enum<T>> extends AbstractAdapter<T> {
  @SuppressWarnings("unchecked")
  @NonNull @Override protected T read(@NonNull Parcel source) {
    try {
      Class<T> enumClass = (Class<T>) Class.forName(source.readString());
      return Enum.valueOf(enumClass, source.readString());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override protected void write(@NonNull T value, @NonNull Parcel dest, int flags) {
    dest.writeString(value.getClass().getName());
    dest.writeString(value.name());
  }
}
