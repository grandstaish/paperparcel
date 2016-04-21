package nz.bradcampbell.paperparcel.internal;

import android.os.Parcelable;

public interface ParcelableWrapper<T> extends Parcelable {
  T get();
}
