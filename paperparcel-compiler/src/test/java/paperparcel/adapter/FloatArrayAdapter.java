package paperparcel.adapter;

import android.os.Parcel;

public final class FloatArrayAdapter extends AbstractAdapter<float[]> {
  public static final FloatArrayAdapter INSTANCE = new FloatArrayAdapter();

  @Override protected float[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(float[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private FloatArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
