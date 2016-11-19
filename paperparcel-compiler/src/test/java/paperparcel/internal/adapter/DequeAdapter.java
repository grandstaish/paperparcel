package paperparcel.internal.adapter;

import java.util.Deque;
import paperparcel.TypeAdapter;

public final class DequeAdapter<T> extends AbstractMutableCollectionAdapter<Deque<T>, T> {
  public DequeAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected Deque<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
