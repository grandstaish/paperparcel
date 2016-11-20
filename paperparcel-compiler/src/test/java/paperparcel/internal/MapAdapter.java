package paperparcel.internal;

import android.os.Parcel;
import java.util.Map;
import paperparcel.TypeAdapter;

public final class MapAdapter<K, V> implements TypeAdapter<Map<K, V>> {
  public MapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public Map<K, V> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Map<K, V> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
