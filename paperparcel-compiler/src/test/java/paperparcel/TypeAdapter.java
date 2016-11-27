package paperparcel;

import android.os.Parcel;

public interface TypeAdapter<T> {
  T readFromParcel(Parcel source);

  void writeToParcel(T value, Parcel dest, int flags);
}
