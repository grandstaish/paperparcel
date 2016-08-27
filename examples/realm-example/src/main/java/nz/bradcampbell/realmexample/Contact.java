package nz.bradcampbell.realmexample;

import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmList;
import io.realm.RealmObject;
import paperparcel.PaperParcel;

@PaperParcel
public class Contact extends RealmObject implements Parcelable {
  public static final Creator<Contact> CREATOR = PaperParcelContact.CREATOR;

  public String name;
  public RealmList<Email> emails;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelContact.writeToParcel(this, dest, flags);
  }
}
