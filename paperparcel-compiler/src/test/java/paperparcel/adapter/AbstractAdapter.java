package paperparcel.adapter;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public abstract class AbstractAdapter<T> implements TypeAdapter<T> {
  @Override public final T readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  protected abstract T read(Parcel source);

  @Override public final void writeToParcel(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  protected abstract void write(T value, Parcel dest, int flags);
}
