package paperparcel.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class BundleAdapter implements TypeAdapter<Bundle> {
  public static final BundleAdapter INSTANCE = new BundleAdapter();

  @Nullable @Override public Bundle readFromParcel(@NonNull Parcel source) {
    return source.readBundle(getClass().getClassLoader());
  }

  @Override public void writeToParcel(@Nullable Bundle value, @NonNull Parcel dest, int flags) {
    dest.writeBundle(value);
  }

  private BundleAdapter() {}
}
