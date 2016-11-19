package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class IntArrayAdapter extends AbstractAdapter<int[]> {
  public static final IntArrayAdapter INSTANCE = new IntArrayAdapter();

  @Override protected int[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override
  protected void write(int[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IntArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
