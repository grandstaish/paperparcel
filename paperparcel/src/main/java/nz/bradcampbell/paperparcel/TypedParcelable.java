package nz.bradcampbell.paperparcel;

import android.os.Parcelable;

public interface TypedParcelable<T> extends Parcelable {
  T get();
}
