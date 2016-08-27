package paperparcel.adapter;

import android.os.Parcel;

public final class LongAdapter extends AbstractAdapter<Long> {
  public static final LongAdapter INSTANCE = new LongAdapter();

  @Override protected Long read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Long value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private LongAdapter() {
    throw new RuntimeException("Stub!");
  }
}
