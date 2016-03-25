package nz.bradcampbell.mixedexample;

import com.google.auto.value.AutoValue;
import android.os.Parcelable;

import java.util.Date;

@AutoValue
public abstract class State implements Parcelable {
  public abstract Test count();
  public abstract Date modificationDate();

  public static State create(Test count, Date modificationDate) {
    return new AutoValue_State(count, modificationDate);
  }
}
