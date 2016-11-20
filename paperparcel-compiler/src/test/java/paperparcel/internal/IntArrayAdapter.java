package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class IntArrayAdapter implements TypeAdapter<int[]> {
  public static final IntArrayAdapter INSTANCE = new IntArrayAdapter();

  @Override public int[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(int[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IntArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
