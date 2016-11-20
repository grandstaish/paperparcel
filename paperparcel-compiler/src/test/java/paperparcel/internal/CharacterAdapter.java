package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class CharacterAdapter implements TypeAdapter<Character> {
  public static final CharacterAdapter INSTANCE = new CharacterAdapter();

  @Override public Character readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Character value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharacterAdapter() {
    throw new RuntimeException("Stub!");
  }
}
