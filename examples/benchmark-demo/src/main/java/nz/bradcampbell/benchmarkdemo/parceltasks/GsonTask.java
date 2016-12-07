package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.Parcel;
import com.google.gson.Gson;
import nz.bradcampbell.benchmarkdemo.model.parceler.ParcelerResponse;

public class GsonTask extends ParcelTask<ParcelerResponse> {
  private final Gson gson;

  public GsonTask(Gson gson, ParcelListener parcelListener, ParcelerResponse response) {
    super(parcelListener, response);
    this.gson = gson;
  }

  @Override protected int writeThenRead(ParcelerResponse response, Parcel parcel) {
    parcel.writeString(gson.toJson(response));
    parcel.setDataPosition(0);
    ParcelerResponse out = gson.fromJson(parcel.readString(), ParcelerResponse.class);
    return out.users.size();
  }
}
