package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class CharArrayAdapter implements TypeAdapter<char[]> {
  public static final CharArrayAdapter INSTANCE = new CharArrayAdapter();

  @Override public char[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(char[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
