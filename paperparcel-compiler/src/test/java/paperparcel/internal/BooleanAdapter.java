package paperparcel.internal;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class BooleanAdapter extends AbstractAdapter<Boolean> {
  public static final BooleanAdapter INSTANCE = new BooleanAdapter();

  @Override protected Boolean read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Boolean value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BooleanAdapter() {
    throw new RuntimeException("Stub!");
  }
}
