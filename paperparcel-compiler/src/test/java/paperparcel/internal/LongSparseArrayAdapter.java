package paperparcel.internal;

import android.os.Parcel;
import android.util.LongSparseArray;
import paperparcel.TypeAdapter;

public final class LongSparseArrayAdapter<T> implements TypeAdapter<LongSparseArray<T>> {
  public LongSparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public LongSparseArray<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(LongSparseArray<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
