package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class LongAdapter implements TypeAdapter<Long> {
  public static final LongAdapter INSTANCE = new LongAdapter();

  @Override public Long readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Long value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private LongAdapter() {
    throw new RuntimeException("Stub!");
  }
}
