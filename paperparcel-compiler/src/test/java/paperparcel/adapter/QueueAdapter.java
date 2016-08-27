package paperparcel.adapter;

import java.util.Queue;
import paperparcel.TypeAdapter;

public final class QueueAdapter<T> extends AbstractMutableCollectionAdapter<Queue<T>, T> {
  public QueueAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected Queue<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
