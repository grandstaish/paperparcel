package paperparcel.adapter;

import android.os.Parcel;

public final class ShortArrayAdapter extends AbstractAdapter<short[]> {
  public static final ShortArrayAdapter INSTANCE = new ShortArrayAdapter();

  @Override protected short[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(short[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ShortArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
