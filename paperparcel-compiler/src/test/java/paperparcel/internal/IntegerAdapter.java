package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class IntegerAdapter implements TypeAdapter<Integer> {
  public static final IntegerAdapter INSTANCE = new IntegerAdapter();

  @Override public Integer readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Integer value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IntegerAdapter() {
    throw new RuntimeException("Stub!");
  }
}
