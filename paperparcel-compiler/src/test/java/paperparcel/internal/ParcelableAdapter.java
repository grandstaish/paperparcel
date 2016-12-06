package paperparcel.internal;

import android.os.Parcel;
import android.os.Parcelable;
import paperparcel.TypeAdapter;

public final class ParcelableAdapter<T extends Parcelable> implements TypeAdapter<T> {
  public ParcelableAdapter(Parcelable.Creator<T> creator) {
    throw new RuntimeException("Stub!");
  }

  @Override public T readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
