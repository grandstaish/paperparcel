package paperparcel.utils;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class TestUtils {
  private TestUtils() {
    // No instances.
  }

  public static <A extends TypeAdapter<T>, T> T writeThenRead(A adapter, T input) {
    Parcel parcel = Parcel.obtain();
    adapter.writeToParcel(input, parcel, 0);
    parcel.setDataPosition(0);
    T result = adapter.readFromParcel(parcel);
    parcel.recycle();
    return result;
  }
}
