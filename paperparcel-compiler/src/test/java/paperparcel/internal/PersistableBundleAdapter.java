package paperparcel.internal;

import android.os.Parcel;
import android.os.PersistableBundle;
import paperparcel.TypeAdapter;

public final class PersistableBundleAdapter implements TypeAdapter<PersistableBundle> {
  public static final PersistableBundleAdapter INSTANCE = new PersistableBundleAdapter();

  @Override public PersistableBundle readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(PersistableBundle value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private PersistableBundleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
