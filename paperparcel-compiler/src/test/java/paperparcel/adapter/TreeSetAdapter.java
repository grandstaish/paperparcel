package paperparcel.adapter;

import java.util.TreeSet;
import paperparcel.TypeAdapter;

public final class TreeSetAdapter<T> extends AbstractMutableCollectionAdapter<TreeSet<T>, T> {
  public TreeSetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected TreeSet<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
