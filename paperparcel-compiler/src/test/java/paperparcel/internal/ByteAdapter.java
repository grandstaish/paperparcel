package paperparcel.internal;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class ByteAdapter extends AbstractAdapter<Byte> {
  public static final ByteAdapter INSTANCE = new ByteAdapter();

  @Override protected Byte read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Byte value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private ByteAdapter() {
    throw new RuntimeException("Stub!");
  }
}
