package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;
import paperparcel.PaperParcel;

@PaperParcel
public class Image implements Parcelable {
  public static final Creator<Image> CREATOR = PaperParcelImage.CREATOR;

  public String id;
  public String format;
  public String url;
  public String description;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelImage.writeToParcel(this, dest, flags);
  }
}
