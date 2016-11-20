package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class BooleanAdapter implements TypeAdapter<Boolean> {
  public static final BooleanAdapter INSTANCE = new BooleanAdapter();

  @Override public Boolean readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Boolean value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BooleanAdapter() {
    throw new RuntimeException("Stub!");
  }
}
