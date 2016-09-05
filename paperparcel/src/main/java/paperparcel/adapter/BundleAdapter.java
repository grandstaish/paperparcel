package paperparcel.adapter;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import paperparcel.TypeAdapter;

/** Default {@link TypeAdapter} for {@link Bundle} types */
public final class BundleAdapter extends AbstractAdapter<Bundle> {
  public static final BundleAdapter INSTANCE = new BundleAdapter();

  @NonNull @Override protected Bundle read(@NonNull Parcel source) {
    return source.readBundle(getClass().getClassLoader());
  }

  @Override protected void write(@NonNull Bundle value, @NonNull Parcel dest, int flags) {
    dest.writeBundle(value);
  }

  private BundleAdapter() {}
}
