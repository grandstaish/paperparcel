package paperparcel.internal.adapter;

import java.util.HashSet;
import paperparcel.TypeAdapter;

public final class HashSetAdapter<T> extends AbstractMutableCollectionAdapter<HashSet<T>, T> {
  public HashSetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected HashSet<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
