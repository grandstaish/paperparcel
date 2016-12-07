package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.Parcel;
import nz.bradcampbell.benchmarkdemo.model.parceler.ParcelerResponse;
import org.parceler.Parcels;

public class ParcelerTask extends ParcelTask<ParcelerResponse> {
  public ParcelerTask(ParcelListener parcelListener, ParcelerResponse response) {
    super(parcelListener, response);
  }

  @Override protected int writeThenRead(ParcelerResponse response, Parcel parcel) {
    parcel.writeParcelable(Parcels.wrap(response), 0);
    parcel.setDataPosition(0);
    ParcelerResponse out = Parcels.unwrap(parcel.readParcelable(ParcelerResponse.class.getClassLoader()));
    return out.users.size();
  }
}
