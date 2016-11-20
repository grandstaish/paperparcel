package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class ByteArrayAdapter implements TypeAdapter<byte[]> {
  public static final ByteArrayAdapter INSTANCE = new ByteArrayAdapter();

  @Override public byte[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(byte[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ByteArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
