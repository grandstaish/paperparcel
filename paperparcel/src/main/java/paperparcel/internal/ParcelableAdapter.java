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
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ParcelableAdapter<T extends Parcelable> implements TypeAdapter<T> {
  private final Parcelable.Creator<T> creator;

  public ParcelableAdapter(@Nullable Parcelable.Creator<T> creator) {
    this.creator = creator;
  }

  @Nullable @Override public T readFromParcel(@NonNull Parcel source) {
    if (creator != null) {
      T result = null;
      if (source.readInt() == 1) {
        result = creator.createFromParcel(source);
      }
      return result;
    } else {
      return source.readParcelable(ParcelableAdapter.class.getClassLoader());
    }
  }

  @Override public void writeToParcel(@Nullable T value, @NonNull Parcel dest, int flags) {
    if (creator != null) {
      if (value == null) {
        dest.writeInt(0);
      } else {
        dest.writeInt(1);
        value.writeToParcel(dest, flags);
      }
    } else {
      dest.writeParcelable(value, flags);
    }
  }
}
