package paperparcel.adapter;

import android.os.Parcel;

public final class CharacterAdapter extends AbstractAdapter<Character> {
  public static final CharacterAdapter INSTANCE = new CharacterAdapter();

  @Override protected Character read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Character value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharacterAdapter() {
    throw new RuntimeException("Stub!");
  }
}
