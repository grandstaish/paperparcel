package paperparcel.adapter;

import android.os.Parcel;

public final class CharArrayAdapter extends AbstractAdapter<char[]> {
  public static final CharArrayAdapter INSTANCE = new CharArrayAdapter();

  @Override protected char[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(char[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
