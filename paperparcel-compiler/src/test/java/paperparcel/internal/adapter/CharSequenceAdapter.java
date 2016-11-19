package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class CharSequenceAdapter extends AbstractAdapter<CharSequence> {
  public static final CharSequenceAdapter INSTANCE = new CharSequenceAdapter();

  @Override protected CharSequence read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(CharSequence value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private CharSequenceAdapter() {
    throw new RuntimeException("Stub!");
  }
}
