package paperparcel.internal.adapter;

import java.util.List;
import paperparcel.TypeAdapter;

public final class ListAdapter<T> extends AbstractMutableCollectionAdapter<List<T>, T> {
  public ListAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected List<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
