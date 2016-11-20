package paperparcel.internal;

import android.os.Bundle;
import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class BundleAdapter implements TypeAdapter<Bundle> {
  public static final BundleAdapter INSTANCE = new BundleAdapter();

  @Override public Bundle readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Bundle value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BundleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
