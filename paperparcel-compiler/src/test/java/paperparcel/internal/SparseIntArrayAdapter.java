package paperparcel.internal;

import android.os.Parcel;
import android.util.SparseIntArray;
import paperparcel.TypeAdapter;

public final class SparseIntArrayAdapter implements TypeAdapter<SparseIntArray> {
  public static final SparseIntArrayAdapter INSTANCE = new SparseIntArrayAdapter();

  @Override public SparseIntArray readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(SparseIntArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseIntArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
