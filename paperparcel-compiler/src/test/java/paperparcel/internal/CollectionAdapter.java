package paperparcel.internal;

import android.os.Parcel;
import java.util.Collection;
import paperparcel.TypeAdapter;

public final class CollectionAdapter<T> implements TypeAdapter<Collection<T>> {
  public CollectionAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public Collection<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Collection<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
