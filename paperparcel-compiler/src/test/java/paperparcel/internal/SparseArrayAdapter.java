package paperparcel.internal;

import android.os.Parcel;
import android.util.SparseArray;
import paperparcel.TypeAdapter;

public final class SparseArrayAdapter<T> implements TypeAdapter<SparseArray<T>> {
  public SparseArrayAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public SparseArray<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(SparseArray<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
