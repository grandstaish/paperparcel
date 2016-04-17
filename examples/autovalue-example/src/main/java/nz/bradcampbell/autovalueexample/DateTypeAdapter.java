package nz.bradcampbell.autovalueexample;

import android.os.Parcel;
import java.util.Date;
import nz.bradcampbell.paperparcel.TypeAdapter;
import org.jetbrains.annotations.NotNull;

public class DateTypeAdapter implements TypeAdapter<Date> {
  @NotNull @Override public Date readFromParcel(@NotNull Parcel inParcel) {
    return new Date(inParcel.readLong());
  }

  @Override public void writeToParcel(@NotNull Date value, @NotNull Parcel outParcel, int flags) {
    outParcel.writeLong(value.getTime());
  }
}
