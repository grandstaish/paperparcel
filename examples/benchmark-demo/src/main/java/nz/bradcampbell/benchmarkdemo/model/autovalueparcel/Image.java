package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Image implements Parcelable {
  public abstract String id();
  public abstract String format();
  public abstract String url();
  public abstract String description();

  public static TypeAdapter<Image> typeAdapter(Gson gson) {
    return new AutoValue_Image.GsonTypeAdapter(gson);
  }
}
