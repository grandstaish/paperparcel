package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class ByteAdapter implements TypeAdapter<Byte> {
  public static final ByteAdapter INSTANCE = new ByteAdapter();

  @Override public Byte readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Byte value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ByteAdapter() {
    throw new RuntimeException("Stub!");
  }
}
