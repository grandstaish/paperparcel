package paperparcel.internal.adapter;

import java.util.Set;
import paperparcel.TypeAdapter;

public final class SetAdapter<T> extends AbstractMutableCollectionAdapter<Set<T>, T> {
  public SetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected Set<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
