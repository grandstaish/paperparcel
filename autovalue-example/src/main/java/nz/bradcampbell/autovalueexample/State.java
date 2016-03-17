package nz.bradcampbell.autovalueexample;

import com.google.auto.value.AutoValue;
import android.os.Parcelable;

import java.util.Date;

@AutoValue
public abstract class State implements Parcelable {
  public abstract int count();
  public abstract Date modificationDate();

  public static State create(int count, Date modificationDate) {
    return new AutoValue_State(count, modificationDate);
  }
}
