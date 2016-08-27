package nz.bradcampbell.realmexample;

import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmObject;
import paperparcel.PaperParcel;

@PaperParcel
public class Email extends RealmObject implements Parcelable {
  public static final Creator<Email> CREATOR = PaperParcelEmail.CREATOR;

  public String address;
  public boolean active;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelEmail.writeToParcel(this, dest, flags);
  }
}
