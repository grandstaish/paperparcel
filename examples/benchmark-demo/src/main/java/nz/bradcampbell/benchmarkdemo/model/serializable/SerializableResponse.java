package nz.bradcampbell.benchmarkdemo.model.serializable;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SerializableResponse implements Serializable {
  public List<User> users;
  public String status;
  @SerializedName("is_real_json") public boolean isRealJson;
}
