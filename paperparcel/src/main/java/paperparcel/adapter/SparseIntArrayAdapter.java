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
import android.util.SparseIntArray;

public final class SparseIntArrayAdapter extends AbstractAdapter<SparseIntArray> {
  public static final SparseIntArrayAdapter INSTANCE = new SparseIntArrayAdapter();

  @NonNull @Override protected SparseIntArray read(@NonNull Parcel source) {
    int size = source.readInt();
    SparseIntArray result = new SparseIntArray(size);
    for (int i = 0; i < size; i++) {
      result.put(source.readInt(), source.readInt());
    }
    return result;
  }

  @Override protected void write(@NonNull SparseIntArray value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (int i = 0; i < value.size(); i++) {
      dest.writeInt(value.keyAt(i));
      dest.writeInt(value.valueAt(i));
    }
  }

  private SparseIntArrayAdapter() {}
}
