package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.Parcel;
import nz.bradcampbell.benchmarkdemo.model.serializable.SerializableResponse;

public class SerializableTask extends ParcelTask<SerializableResponse> {
  public SerializableTask(ParcelListener parcelListener, SerializableResponse response) {
    super(parcelListener, response);
  }

  @Override protected int writeThenRead(SerializableResponse response, Parcel parcel) {
    parcel.writeSerializable(response);
    parcel.setDataPosition(0);
    SerializableResponse out = (SerializableResponse) parcel.readSerializable();
    return out.users.size();
  }
}
