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

package nz.bradcampbell.realmexample;

import android.os.Parcel;
import android.support.annotation.NonNull;
import io.realm.RealmList;
import io.realm.RealmModel;
import paperparcel.TypeAdapter;

public class RealmListTypeAdapter<T extends RealmModel> implements TypeAdapter<RealmList<T>> {
  private final TypeAdapter<T> itemAdapter;

  public RealmListTypeAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @NonNull @Override public RealmList<T> readFromParcel(@NonNull Parcel source) {
    int size = source.readInt();
    RealmList<T> list = new RealmList<>();
    for (int i = 0; i < size; i++) {
      list.add(itemAdapter.readFromParcel(source));
    }
    return list;
  }

  @Override public void writeToParcel(@NonNull RealmList<T> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (int i = 0; i < value.size(); i++) {
      itemAdapter.writeToParcel(value.get(i), dest, flags);
    }
  }
}
