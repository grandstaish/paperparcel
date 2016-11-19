package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class LongArrayAdapter extends AbstractAdapter<long[]> {
  public static final LongArrayAdapter INSTANCE = new LongArrayAdapter();

  @Override protected long[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(long[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private LongArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
