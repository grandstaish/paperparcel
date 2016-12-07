package nz.bradcampbell.benchmarkdemo.model.parceler;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.parceler.Parcel;

@Parcel
public class ParcelerResponse {
  public List<User> users;
  public String status;
  @SerializedName("is_real_json") public boolean isRealJson;
}
