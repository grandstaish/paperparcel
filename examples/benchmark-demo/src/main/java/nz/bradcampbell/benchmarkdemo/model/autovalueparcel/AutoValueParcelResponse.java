package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@AutoValue
public abstract class AutoValueParcelResponse implements Parcelable {
  public abstract List<User> users();
  public abstract String status();
  @SerializedName("is_real_json") public abstract boolean isRealJson();

  public static TypeAdapter<AutoValueParcelResponse> typeAdapter(Gson gson) {
    return new AutoValue_AutoValueParcelResponse.GsonTypeAdapter(gson);
  }
}
