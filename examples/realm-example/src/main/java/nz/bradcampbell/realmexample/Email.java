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
import android.os.Parcelable;
import io.realm.RealmObject;
import paperparcel.PaperParcel;

@PaperParcel
public class Email extends RealmObject implements Parcelable {
  public static final Creator<Email> CREATOR = PaperParcelEmail.CREATOR;

  public String address;
  public boolean active;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelEmail.writeToParcel(this, dest, flags);
  }
}
