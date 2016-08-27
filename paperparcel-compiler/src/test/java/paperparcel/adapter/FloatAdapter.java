package paperparcel.adapter;

import android.os.Parcel;

public final class FloatAdapter extends AbstractAdapter<Float> {
  public static final FloatAdapter INSTANCE = new FloatAdapter();

  @Override protected Float read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Float value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private FloatAdapter() {
    throw new RuntimeException("Stub!");
  }
}
