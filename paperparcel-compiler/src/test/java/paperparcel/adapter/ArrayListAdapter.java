package paperparcel.adapter;

import java.util.ArrayList;
import paperparcel.TypeAdapter;

public final class ArrayListAdapter<T> extends AbstractMutableCollectionAdapter<ArrayList<T>, T> {
  public ArrayListAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected ArrayList<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
