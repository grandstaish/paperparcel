package paperparcel.internal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import paperparcel.AbstractAdapter;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class PersistableBundleAdapter extends AbstractAdapter<PersistableBundle> {
  public static final PersistableBundleAdapter INSTANCE = new PersistableBundleAdapter();

  @NonNull @Override protected PersistableBundle read(@NonNull Parcel source) {
    return source.readPersistableBundle(getClass().getClassLoader());
  }

  @Override protected void write(@NonNull PersistableBundle value, @NonNull Parcel dest, int flags) {
    dest.writePersistableBundle(value);
  }

  private PersistableBundleAdapter() {}
}
