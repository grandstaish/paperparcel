package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@AutoValue
public abstract class User implements Parcelable {
  @SerializedName("_id") public abstract String id();
  public abstract int index();
  public abstract String guid();
  @SerializedName("is_active") public abstract boolean isActive();
  public abstract String balance();
  @SerializedName("picture") public abstract String pictureUrl();
  public abstract int age();
  public abstract Name name();
  public abstract String company();
  public abstract String email();
  public abstract String address();
  public abstract String about();
  public abstract String registered();
  public abstract double latitude();
  public abstract double longitude();
  public abstract List<String> tags();
  public abstract List<Integer> range();
  public abstract List<Friend> friends();
  public abstract List<Image> images();
  public abstract String greeting();
  @SerializedName("favorite_fruit") public abstract String favoriteFruit();
  @SerializedName("eye_color") public abstract String eyeColor();
  public abstract String phone();

  public static TypeAdapter<User> typeAdapter(Gson gson) {
    return new AutoValue_User.GsonTypeAdapter(gson);
  }
}
