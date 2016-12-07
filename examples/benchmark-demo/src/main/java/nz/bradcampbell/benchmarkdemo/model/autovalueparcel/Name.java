package nz.bradcampbell.benchmarkdemo.model.autovalueparcel;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Name implements Parcelable {
  public abstract String first();
  public abstract String last();

  public static TypeAdapter<Name> typeAdapter(Gson gson) {
    return new AutoValue_Name.GsonTypeAdapter(gson);
  }
}
