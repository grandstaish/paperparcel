package paperparcel.adapter;

import android.os.Parcel;

public final class ShortAdapter extends AbstractAdapter<Short> {
  public static final ShortAdapter INSTANCE = new ShortAdapter();

  @Override public Short read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Short value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ShortAdapter() {
    throw new RuntimeException("Stub!");
  }
}
