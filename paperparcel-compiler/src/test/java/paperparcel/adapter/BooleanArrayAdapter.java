package paperparcel.adapter;

import android.os.Parcel;

public final class BooleanArrayAdapter extends AbstractAdapter<boolean[]> {
  public static final BooleanArrayAdapter INSTANCE = new BooleanArrayAdapter();

  @Override protected boolean[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(boolean[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BooleanArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
