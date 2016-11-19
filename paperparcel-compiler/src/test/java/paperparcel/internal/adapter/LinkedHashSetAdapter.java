package paperparcel.internal.adapter;

import java.util.LinkedHashSet;
import paperparcel.TypeAdapter;

public final class LinkedHashSetAdapter<T> extends
    AbstractMutableCollectionAdapter<LinkedHashSet<T>, T> {
  public LinkedHashSetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected LinkedHashSet<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
