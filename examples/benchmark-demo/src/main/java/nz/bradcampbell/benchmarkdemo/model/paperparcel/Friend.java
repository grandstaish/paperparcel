package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;
import paperparcel.PaperParcel;

@PaperParcel
public class Friend implements Parcelable {
  public static final Parcelable.Creator<Friend> CREATOR = PaperParcelFriend.CREATOR;

  public int id;
  public String name;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelFriend.writeToParcel(this, dest, flags);
  }
}
