package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class ShortArrayAdapter implements TypeAdapter<short[]> {
  public static final ShortArrayAdapter INSTANCE = new ShortArrayAdapter();

  @Override public short[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(short[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ShortArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
