package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.Parcel;
import nz.bradcampbell.benchmarkdemo.model.autovalueparcel.AutoValueParcelResponse;

public class AutoValueParcelTask extends ParcelTask<AutoValueParcelResponse> {
  public AutoValueParcelTask(ParcelListener parcelListener, AutoValueParcelResponse response) {
    super(parcelListener, response);
  }

  @Override protected int writeThenRead(AutoValueParcelResponse response, Parcel parcel) {
    parcel.writeParcelable(response, 0);
    parcel.setDataPosition(0);
    AutoValueParcelResponse out = parcel.readParcelable(AutoValueParcelResponse.class.getClassLoader());
    return out.users().size();
  }
}
