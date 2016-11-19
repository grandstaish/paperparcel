package paperparcel.internal.adapter;

import android.os.Parcel;
import java.util.Date;
import paperparcel.AbstractAdapter;

public final class DateAdapter extends AbstractAdapter<Date> {
  public static final DateAdapter INSTANCE = new DateAdapter();

  @Override protected Date read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Date value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private DateAdapter() {
    throw new RuntimeException("Stub!");
  }
}
