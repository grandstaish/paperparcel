package paperparcel.internal.adapter;

import android.os.Parcel;
import android.util.SparseLongArray;
import paperparcel.AbstractAdapter;

public final class SparseLongArrayAdapter extends AbstractAdapter<SparseLongArray> {
  public static final SparseLongArrayAdapter INSTANCE = new SparseLongArrayAdapter();

  @Override protected SparseLongArray read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(SparseLongArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseLongArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
