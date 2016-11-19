package paperparcel.internal.adapter;

import java.util.Map;
import paperparcel.TypeAdapter;

public final class MapAdapter<K, V> extends AbstractMutableMapAdapter<Map<K, V>, K, V> {
  public MapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected Map<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
