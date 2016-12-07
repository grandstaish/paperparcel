package nz.bradcampbell.benchmarkdemo.model.paperparcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import paperparcel.PaperParcel;

@PaperParcel
public class User implements Parcelable {
  public static final Parcelable.Creator<User> CREATOR = PaperParcelUser.CREATOR;

  public String id;
  public int index;
  public String guid;
  @SerializedName("is_active") public boolean isActive;
  public String balance;
  @SerializedName("picture") public String pictureUrl;
  public int age;
  public Name name;
  public String company;
  public String email;
  public String address;
  public String about;
  public String registered;
  public double latitude;
  public double longitude;
  public List<String> tags;
  public List<Integer> range;
  public List<Friend> friends;
  public List<Image> images;
  public String greeting;
  @SerializedName("favorite_fruit") public String favoriteFruit;
  @SerializedName("eye_color") public String eyeColor;
  public String phone;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    PaperParcelUser.writeToParcel(this, dest, flags);
  }
}
