package paperparcel.internal.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArraySet;
import paperparcel.TypeAdapter;

@TargetApi(Build.VERSION_CODES.M)
public final class ArraySetAdapter<T> extends AbstractMutableCollectionAdapter<ArraySet<T>, T> {
  public ArraySetAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected ArraySet<T> newCollection(int size) {
    throw new RuntimeException("Stub!");
  }
}
