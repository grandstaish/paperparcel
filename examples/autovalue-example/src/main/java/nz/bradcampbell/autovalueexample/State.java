package nz.bradcampbell.autovalueexample;

import com.google.auto.value.AutoValue;
import android.os.Parcelable;

import nz.bradcampbell.paperparcel.TypeAdapters;

import java.util.Date;

@AutoValue
@TypeAdapters(DateTypeAdapter.class)
public abstract class State implements Parcelable {
  public abstract int count();
  public abstract Date modificationDate();

  public static State create(int count, Date modificationDate) {
    return new AutoValue_State(count, modificationDate);
  }
}
