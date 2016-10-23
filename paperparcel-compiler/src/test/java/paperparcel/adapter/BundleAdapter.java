package paperparcel.adapter;

import android.os.Bundle;
import android.os.Parcel;

public final class BundleAdapter extends AbstractAdapter<Bundle> {
  public static final BundleAdapter INSTANCE = new BundleAdapter();

  @Override protected Bundle read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Bundle value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BundleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
