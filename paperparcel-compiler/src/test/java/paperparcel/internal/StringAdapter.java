package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class StringAdapter implements TypeAdapter<String> {
  public static final StringAdapter INSTANCE = new StringAdapter();

  @Override public String readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(String value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private StringAdapter() {
    throw new RuntimeException("Stub!");
  }
}
