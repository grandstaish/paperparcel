package paperparcel.internal.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import paperparcel.AbstractAdapter;

public final class ParcelableAdapter<T extends Parcelable> extends AbstractAdapter<T> {
  @Override protected T read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(T value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }
}
