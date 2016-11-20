package paperparcel.internal;

import android.os.Parcel;
import android.util.Size;
import paperparcel.AbstractAdapter;

public final class SizeAdapter extends AbstractAdapter<Size> {
  public static final SizeAdapter INSTANCE = new SizeAdapter();

  @Override public Size read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Size value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SizeAdapter() {
    throw new RuntimeException("Stub!");
  }
}
