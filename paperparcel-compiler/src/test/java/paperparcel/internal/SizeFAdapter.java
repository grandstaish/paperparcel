package paperparcel.internal;

import android.os.Parcel;
import android.util.SizeF;
import paperparcel.AbstractAdapter;

public final class SizeFAdapter extends AbstractAdapter<SizeF> {
  public static final SizeFAdapter INSTANCE = new SizeFAdapter();

  @Override public SizeF read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(SizeF value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SizeFAdapter() {
    throw new RuntimeException("Stub!");
  }
}
