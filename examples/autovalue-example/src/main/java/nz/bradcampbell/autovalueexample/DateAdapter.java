package nz.bradcampbell.autovalueexample;

import android.os.Parcel;
import android.support.annotation.NonNull;
import java.util.Date;
import paperparcel.RegisterAdapter;
import paperparcel.TypeAdapter;

@RegisterAdapter
public final class DateAdapter implements TypeAdapter<Date> {
  public static final DateAdapter INSTANCE = new DateAdapter();

  @NonNull @Override public Date readFromParcel(@NonNull Parcel source) {
    return new Date(source.readLong());
  }

  @Override public void writeToParcel(@NonNull Date value, @NonNull Parcel dest, int flags) {
    dest.writeLong(value.getTime());
  }

  private DateAdapter() {}
}
