package nz.bradcampbell.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PaperParcelable implements Parcelable {
  public static final Parcelable.Creator<Object> CREATOR = new Parcelable.Creator<Object>() {
    @Override public Object createFromParcel(Parcel parcel) {
      return PaperParcels.unsafeUnwrap(parcel.readParcelable(PaperParcelable.class.getClassLoader()));
    }

    @Override public Object[] newArray(int i) {
      return new Object[i];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeParcelable(PaperParcels.wrap(this), flags);
  }
}
