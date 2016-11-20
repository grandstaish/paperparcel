package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class ShortAdapter implements TypeAdapter<Short> {
  public static final ShortAdapter INSTANCE = new ShortAdapter();

  @Override public Short readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Short value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ShortAdapter() {
    throw new RuntimeException("Stub!");
  }
}
