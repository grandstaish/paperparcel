package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;
import paperparcel.PaperParcel;

@PaperParcel
public class Name implements Parcelable {
  public static final Parcelable.Creator<Name> CREATOR = PaperParcelName.CREATOR;

  public String first;
  public String last;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelName.writeToParcel(this, dest, flags);
  }
}
