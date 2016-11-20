package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class LongArrayAdapter implements TypeAdapter<long[]> {
  public static final LongArrayAdapter INSTANCE = new LongArrayAdapter();

  @Override public long[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(long[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private LongArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
