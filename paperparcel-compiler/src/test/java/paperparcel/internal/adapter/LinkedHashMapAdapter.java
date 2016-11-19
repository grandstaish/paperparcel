package paperparcel.internal.adapter;

import java.util.LinkedHashMap;
import paperparcel.TypeAdapter;

public final class LinkedHashMapAdapter<K, V> extends
    AbstractMutableMapAdapter<LinkedHashMap<K, V>, K, V> {
  public LinkedHashMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected LinkedHashMap<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
