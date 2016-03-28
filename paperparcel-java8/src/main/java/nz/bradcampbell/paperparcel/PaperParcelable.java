package nz.bradcampbell.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

public interface PaperParcelable extends Parcelable {
  class Creator<T> implements Parcelable.Creator<T> {
    private final Class<? extends T> type;

    public Creator(Class<? extends T> type) {
      this.type = type;
    }

    public T createFromParcel(Parcel parcel) {
      return PaperParcels.unsafeUnwrap(parcel.readParcelable(type.getClassLoader()));
    }

    @Override public T[] newArray(int i) {
      return PaperParcels.newArray(type, i);
    }
  }

  @Override default int describeContents() {
    return 0;
  }

  @Override default void writeToParcel(Parcel parcel, int flags) {
    parcel.writeParcelable(PaperParcels.wrap(this), flags);
  }
}
