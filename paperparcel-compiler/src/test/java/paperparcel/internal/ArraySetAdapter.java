package paperparcel.internal;

import android.os.Parcel;
import android.util.ArraySet;
import paperparcel.TypeAdapter;

public final class ArraySetAdapter<T> implements TypeAdapter<ArraySet<T>> {
  public ArraySetAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public ArraySet<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(ArraySet<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
