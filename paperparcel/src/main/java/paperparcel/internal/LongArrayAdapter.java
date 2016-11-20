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
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class LongArrayAdapter implements TypeAdapter<long[]> {
  public static final LongArrayAdapter INSTANCE = new LongArrayAdapter();

  @Nullable @Override public long[] readFromParcel(@NonNull Parcel source) {
    return source.createLongArray();
  }

  @Override public void writeToParcel(@Nullable long[] value, @NonNull Parcel dest, int flags) {
    dest.writeLongArray(value);
  }

  private LongArrayAdapter() {}
}
