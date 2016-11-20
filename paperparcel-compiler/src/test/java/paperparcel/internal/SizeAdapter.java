package paperparcel.internal;

import android.os.Parcel;
import android.util.Size;
import paperparcel.TypeAdapter;

public final class SizeAdapter implements TypeAdapter<Size> {
  public static final SizeAdapter INSTANCE = new SizeAdapter();

  @Override public Size readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Size value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SizeAdapter() {
    throw new RuntimeException("Stub!");
  }
}
