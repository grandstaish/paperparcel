package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class CharSequenceAdapter implements TypeAdapter<CharSequence> {
  public static final CharSequenceAdapter INSTANCE = new CharSequenceAdapter();

  @Override public CharSequence readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(CharSequence value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharSequenceAdapter() {
    throw new RuntimeException("Stub!");
  }
}
