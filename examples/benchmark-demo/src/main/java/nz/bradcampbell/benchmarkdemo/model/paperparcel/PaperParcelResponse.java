package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import paperparcel.PaperParcel;

@PaperParcel
public class PaperParcelResponse implements Parcelable {
  public static final Creator<PaperParcelResponse> CREATOR = PaperParcelPaperParcelResponse.CREATOR;

  public List<User> users;
  public String status;
  @SerializedName("is_real_json") public boolean isRealJson;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelPaperParcelResponse.writeToParcel(this, dest, flags);
  }
}
