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
import java.util.Date;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

/** Default {@link TypeAdapter} for {@link Date} types */
public final class DateAdapter extends AbstractAdapter<Date> {
  public static final DateAdapter INSTANCE = new DateAdapter();

  @NonNull @Override protected Date read(@NonNull Parcel source) {
    return new Date(source.readLong());
  }

  @Override protected void write(@NonNull Date value, @NonNull Parcel dest, int flags) {
    dest.writeLong(value.getTime());
  }

  private DateAdapter() {}
}
