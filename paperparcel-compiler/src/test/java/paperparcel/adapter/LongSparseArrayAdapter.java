package paperparcel.adapter;

import android.os.Parcel;
import android.util.LongSparseArray;
import paperparcel.TypeAdapter;

public final class LongSparseArrayAdapter<T> extends AbstractAdapter<LongSparseArray<T>> {
  public LongSparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override protected LongSparseArray<T> read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(LongSparseArray<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
