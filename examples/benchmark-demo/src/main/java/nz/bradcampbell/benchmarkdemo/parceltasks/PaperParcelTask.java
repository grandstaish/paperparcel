package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.Parcel;
import nz.bradcampbell.benchmarkdemo.model.paperparcel.PaperParcelResponse;

public class PaperParcelTask extends ParcelTask<PaperParcelResponse> {
  public PaperParcelTask(ParcelListener parcelListener, PaperParcelResponse response) {
    super(parcelListener, response);
  }

  @Override protected int writeThenRead(PaperParcelResponse response, Parcel parcel) {
    parcel.writeParcelable(response, 0);
    parcel.setDataPosition(0);
    PaperParcelResponse out = parcel.readParcelable(PaperParcelResponse.class.getClassLoader());
    return out.users.size();
  }
}
