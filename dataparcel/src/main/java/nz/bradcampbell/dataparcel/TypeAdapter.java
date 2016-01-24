package nz.bradcampbell.dataparcel;

import android.os.Parcel;

/**
 * A custom method for reading/writing a Type
 * @param <T> The Type to override the default reading/writing functionality for
 */
public interface TypeAdapter<T> {

  /**
   * Creates a new instance of the desired Type by reading values from the Parcel {@code in}
   * @param in The {@link Parcel} which contains the values of {@code T}
   * @return A new object based on the values in {@code in}.
   */
  T readFromParcel(Parcel in);

  /**
   * Writes {@code value} to the Parcel {@code dest}
   * @param value The object to be written to the {@link Parcel}
   * @param dest The {@link Parcel} which will contain the value of {@code T}
   */
  void writeToParcel(T value, Parcel dest);

}