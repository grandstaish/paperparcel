package paperparcel.internal.adapter;

import java.util.TreeMap;
import paperparcel.TypeAdapter;

public final class TreeMapAdapter<K, V> extends AbstractMutableMapAdapter<TreeMap<K, V>, K, V> {
  public TreeMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected TreeMap<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
