package paperparcel.internal;

import android.os.Parcel;
import android.util.ArrayMap;
import paperparcel.TypeAdapter;

public final class ArrayMapAdapter<K, V> implements TypeAdapter<ArrayMap<K, V>> {
  public ArrayMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public ArrayMap<K, V> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(ArrayMap<K, V> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
