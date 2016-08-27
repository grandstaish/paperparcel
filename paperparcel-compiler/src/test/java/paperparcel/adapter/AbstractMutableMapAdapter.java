package paperparcel.adapter;

import android.os.Parcel;
import java.util.Map;
import paperparcel.TypeAdapter;

public abstract class AbstractMutableMapAdapter<M extends Map<K, V>, K, V>
    extends AbstractAdapter<M> {
  public AbstractMutableMapAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override protected M read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(M value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  protected abstract M newMap(int size);
}
