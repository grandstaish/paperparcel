package paperparcel.adapter;

import java.util.HashMap;
import paperparcel.TypeAdapter;

public final class HashMapAdapter<K, V> extends AbstractMutableMapAdapter<HashMap<K, V>, K, V> {
  public HashMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected HashMap<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
