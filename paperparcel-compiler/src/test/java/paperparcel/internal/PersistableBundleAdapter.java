package paperparcel.internal;

import android.os.Parcel;
import android.os.PersistableBundle;
import paperparcel.AbstractAdapter;

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
