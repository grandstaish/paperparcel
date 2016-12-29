package paperparcel.internal;

import android.os.Parcel;
import java.io.Serializable;
import paperparcel.TypeAdapter;

public final class SerializableAdapter<T extends Serializable> implements TypeAdapter<T> {
  @Override public T readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
