package paperparcel.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArrayMap;
import paperparcel.TypeAdapter;

@TargetApi(Build.VERSION_CODES.KITKAT)
public final class ArrayMapAdapter<K, V> extends AbstractMutableMapAdapter<ArrayMap<K, V>, K, V> {
  public ArrayMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    super(keyAdapter, valueAdapter);
  }

  @Override protected ArrayMap<K, V> newMap(int size) {
    throw new RuntimeException("Stub!");
  }
}
