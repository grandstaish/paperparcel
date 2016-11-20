package nz.bradcampbell.java7example;

import android.os.Parcel;
import android.support.annotation.NonNull;
import java.util.Date;
import paperparcel.AbstractAdapter;
import paperparcel.RegisterAdapter;

@RegisterAdapter
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
