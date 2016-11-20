package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class FloatArrayAdapter implements TypeAdapter<float[]> {
  public static final FloatArrayAdapter INSTANCE = new FloatArrayAdapter();

  @Override public float[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(float[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private FloatArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
