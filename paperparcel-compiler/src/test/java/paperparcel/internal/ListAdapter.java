package paperparcel.internal;

import android.os.Parcel;
import java.util.List;
import paperparcel.TypeAdapter;

public final class ListAdapter<T> implements TypeAdapter<List<T>> {
  public ListAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public List<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(List<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
