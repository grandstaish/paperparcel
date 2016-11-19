package paperparcel.internal.adapter;

import android.os.Parcel;
import java.util.Collection;
import paperparcel.AbstractAdapter;
import paperparcel.TypeAdapter;

abstract class AbstractMutableCollectionAdapter<C extends Collection<T>, T>
    extends AbstractAdapter<C> {
  AbstractMutableCollectionAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override protected C read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(C value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  protected abstract C newCollection(int size);
}
