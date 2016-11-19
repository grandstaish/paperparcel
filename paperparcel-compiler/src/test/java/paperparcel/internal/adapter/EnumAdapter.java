package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class EnumAdapter<T extends Enum<T>> extends AbstractAdapter<T> {
  @Override protected T read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
