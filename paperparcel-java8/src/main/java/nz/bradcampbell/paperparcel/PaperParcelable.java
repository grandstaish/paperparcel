package nz.bradcampbell.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

public interface PaperParcelable extends Parcelable {
  Parcelable.Creator<Object> CREATOR = new Parcelable.Creator<Object>() {
    @Override public Object createFromParcel(Parcel parcel) {
      return PaperParcels.unsafeUnwrap(parcel.readParcelable(PaperParcelable.class.getClassLoader()));
    }

    @Override public Object[] newArray(int i) {
      return new Object[i];
    }
  };

  @Override default int describeContents() {
    return 0;
  }

  @Override default void writeToParcel(Parcel parcel, int flags) {
    parcel.writeParcelable(PaperParcels.wrap(this), flags);
  }
}
