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
import java.math.BigDecimal;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

/** Default {@link TypeAdapter} for {@link BigDecimal} types */
public final class BigDecimalAdapter extends AbstractAdapter<BigDecimal> {
  public static final BigDecimalAdapter INSTANCE = new BigDecimalAdapter();

  @NonNull @Override protected BigDecimal read(@NonNull Parcel source) {
    return new BigDecimal(BigIntegerAdapter.INSTANCE.readFromParcel(source), source.readInt());
  }

  @Override protected void write(@NonNull BigDecimal value, @NonNull Parcel dest, int flags) {
    BigIntegerAdapter.INSTANCE.writeToParcel(value.unscaledValue(), dest, flags);
    dest.writeInt(value.scale());
  }

  private BigDecimalAdapter() {}
}
