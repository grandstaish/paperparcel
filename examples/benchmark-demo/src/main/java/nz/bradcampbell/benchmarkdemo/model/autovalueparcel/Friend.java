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

package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Friend implements Parcelable {
  public abstract int id();
  public abstract String name();

  public static TypeAdapter<Friend> typeAdapter(Gson gson) {
    return new AutoValue_Friend.GsonTypeAdapter(gson);
  }
}
