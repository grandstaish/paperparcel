package paperparcel.internal;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class StringArrayAdapter extends AbstractAdapter<String[]> {
  public static final StringArrayAdapter INSTANCE = new StringArrayAdapter();

  @Override protected String[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(String[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private StringArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
