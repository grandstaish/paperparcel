package paperparcel.internal;

import android.os.Parcel;
import java.util.Set;
import paperparcel.TypeAdapter;

public final class SetAdapter<T> implements TypeAdapter<Set<T>> {
  public SetAdapter(TypeAdapter<T> itemAdapter) {
    throw new RuntimeException("Stub!");
  }

  @Override public Set<T> readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Set<T> value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
