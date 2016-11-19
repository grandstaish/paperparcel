package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class ByteArrayAdapter extends AbstractAdapter<byte[]> {
  public static final ByteArrayAdapter INSTANCE = new ByteArrayAdapter();

  @Override protected byte[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(byte[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ByteArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
