package nz.bradcampbell.autovalueexample;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;
import java.util.Date;

@AutoValue
public abstract class State implements Parcelable {
  public static State create(int count, Date modificationDate) {
    return new AutoValue_State(count, modificationDate);
  }

  public abstract int count();

  public abstract Date modificationDate();
}
