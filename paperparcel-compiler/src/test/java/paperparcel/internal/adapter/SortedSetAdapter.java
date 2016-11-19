package paperparcel.internal.adapter;

import java.util.SortedSet;
import paperparcel.TypeAdapter;

public final class SortedSetAdapter<T> extends AbstractMutableCollectionAdapter<SortedSet<T>, T> {
  public SortedSetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected SortedSet<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
