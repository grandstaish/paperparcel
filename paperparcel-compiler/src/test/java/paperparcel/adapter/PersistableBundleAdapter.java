package paperparcel.adapter;

import android.os.Parcel;
import android.os.PersistableBundle;

public final class PersistableBundleAdapter extends AbstractAdapter<PersistableBundle> {
  public static final PersistableBundleAdapter INSTANCE = new PersistableBundleAdapter();

  @Override protected PersistableBundle read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(PersistableBundle value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private PersistableBundleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
