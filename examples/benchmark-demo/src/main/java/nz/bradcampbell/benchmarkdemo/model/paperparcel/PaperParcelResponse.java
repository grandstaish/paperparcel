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

package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import paperparcel.PaperParcel;

@PaperParcel
public class PaperParcelResponse implements Parcelable {
  public static final Creator<PaperParcelResponse> CREATOR = PaperParcelPaperParcelResponse.CREATOR;

  public List<User> users;
  public String status;
  @SerializedName("is_real_json") public boolean isRealJson;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelPaperParcelResponse.writeToParcel(this, dest, flags);
  }
}
