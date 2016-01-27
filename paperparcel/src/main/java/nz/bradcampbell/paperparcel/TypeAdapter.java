package nz.bradcampbell.paperparcel;

import android.os.Parcel;
import org.jetbrains.annotations.NotNull;

/**
 * A custom method for reading/writing a Type
 * @param <T> The Type to override the default reading/writing functionality for
 */
public interface TypeAdapter<T> {

  /**
   * Creates a new instance of the desired Type by reading values from the Parcel {@code inParcel}
   * @param inParcel The {@link Parcel} which contains the values of {@code T}
   * @return A new object based on the values in {@code inParcel}.
   */
  @NotNull T readFromParcel(@NotNull Parcel inParcel);

  /**
   * Writes {@code value} to the Parcel {@code outParcel}
   * @param value The object to be written to the {@link Parcel}
   * @param outParcel The {@link Parcel} which will contain the value of {@code T}
   */
  void writeToParcel(@NotNull T value, @NotNull Parcel outParcel);

}