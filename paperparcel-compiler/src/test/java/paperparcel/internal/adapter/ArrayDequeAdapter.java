package paperparcel.internal.adapter;

import java.util.ArrayDeque;
import paperparcel.TypeAdapter;

public final class ArrayDequeAdapter<T> extends AbstractMutableCollectionAdapter<ArrayDeque<T>, T> {
  public ArrayDequeAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected ArrayDeque<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
