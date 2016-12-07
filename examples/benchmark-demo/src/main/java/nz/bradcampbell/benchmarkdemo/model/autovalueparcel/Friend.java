package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Friend implements Parcelable {
  public abstract int id();
  public abstract String name();

  public static TypeAdapter<Friend> typeAdapter(Gson gson) {
    return new AutoValue_Friend.GsonTypeAdapter(gson);
  }
}
