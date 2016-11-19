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
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

/** Default {@link TypeAdapter} for {@code long[]} types */
public final class LongArrayAdapter extends AbstractAdapter<long[]> {
  public static final LongArrayAdapter INSTANCE = new LongArrayAdapter();

  @NonNull @Override protected long[] read(@NonNull Parcel source) {
    return source.createLongArray();
  }

  @Override protected void write(@NonNull long[] value, @NonNull Parcel dest, int flags) {
    dest.writeLongArray(value);
  }

  private LongArrayAdapter() {}
}
