package paperparcel.internal.adapter;

import java.util.SortedMap;
import paperparcel.TypeAdapter;

public final class SortedMapAdapter<K, V> extends AbstractMutableMapAdapter<SortedMap<K, V>, K, V> {
  public SortedMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected SortedMap<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
