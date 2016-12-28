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

package nz.bradcampbell.autovalueexample;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import java.util.Date;

@AutoValue
public abstract class State implements Parcelable {
  public static State create(int count, Date modificationDate) {
    return new AutoValue_State(count, modificationDate);
  }

  public abstract int count();

  public abstract Date modificationDate();
}
