package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class BooleanArrayAdapter implements TypeAdapter<boolean[]> {
  public static final BooleanArrayAdapter INSTANCE = new BooleanArrayAdapter();

  @Override public boolean[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(boolean[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BooleanArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
