package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class FloatAdapter implements TypeAdapter<Float> {
  public static final FloatAdapter INSTANCE = new FloatAdapter();

  @Override public Float readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Float value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private FloatAdapter() {
    throw new RuntimeException("Stub!");
  }
}
