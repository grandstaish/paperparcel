package paperparcel.internal.adapter;

import android.os.Parcel;
import android.util.SparseIntArray;
import paperparcel.AbstractAdapter;

public final class SparseIntArrayAdapter extends AbstractAdapter<SparseIntArray> {
  public static final SparseIntArrayAdapter INSTANCE = new SparseIntArrayAdapter();

  @Override protected SparseIntArray read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(SparseIntArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseIntArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
