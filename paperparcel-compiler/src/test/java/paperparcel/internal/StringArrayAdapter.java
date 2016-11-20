package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class StringArrayAdapter implements TypeAdapter<String[]> {
  public static final StringArrayAdapter INSTANCE = new StringArrayAdapter();

  @Override public String[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(String[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private StringArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
