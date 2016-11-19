package paperparcel.internal.adapter;

import android.os.Parcel;
import android.util.SparseArray;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

public final class SparseArrayAdapter<T> extends AbstractAdapter<SparseArray<T>> {
  public SparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override protected SparseArray<T> read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(SparseArray<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
