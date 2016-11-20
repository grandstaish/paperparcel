package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class EnumAdapter<T extends Enum<T>> implements TypeAdapter<T> {
  @Override public T readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
