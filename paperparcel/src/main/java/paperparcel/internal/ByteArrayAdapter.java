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
import paperparcel.AbstractAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class ByteArrayAdapter extends AbstractAdapter<byte[]> {
  public static final ByteArrayAdapter INSTANCE = new ByteArrayAdapter();

  @NonNull @Override protected byte[] read(@NonNull Parcel source) {
    return source.createByteArray();
  }

  @Override protected void write(@NonNull byte[] value, @NonNull Parcel dest, int flags) {
    dest.writeByteArray(value);
  }

  private ByteArrayAdapter() {}
}
