package paperparcel.adapter;

import java.util.LinkedList;
import paperparcel.TypeAdapter;

public final class LinkedListAdapter<T> extends AbstractMutableCollectionAdapter<LinkedList<T>, T> {
  public LinkedListAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected LinkedList<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
