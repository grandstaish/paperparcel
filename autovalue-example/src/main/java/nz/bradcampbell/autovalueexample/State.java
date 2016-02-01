package nz.bradcampbell.autovalueexample;

import com.google.auto.value.AutoValue;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

@AutoValue
public abstract class State implements Parcelable {
    public abstract int count();
    public abstract Date modificationDate();
    @Nullable public abstract String nullableString();

    public static State create(int count, Date modificationDate, String nullableString) {
        return new AutoValue_State(count, modificationDate, nullableString);
    }
}
