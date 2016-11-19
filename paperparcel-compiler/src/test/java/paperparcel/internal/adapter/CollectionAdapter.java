package paperparcel.internal.adapter;

import java.util.Collection;
import paperparcel.TypeAdapter;

public final class CollectionAdapter<T> extends AbstractMutableCollectionAdapter<Collection<T>, T> {
  public CollectionAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected Collection<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
