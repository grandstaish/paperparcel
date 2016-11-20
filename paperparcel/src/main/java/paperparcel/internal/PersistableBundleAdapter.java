package paperparcel.internal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import paperparcel.TypeAdapter;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class PersistableBundleAdapter implements TypeAdapter<PersistableBundle> {
  public static final PersistableBundleAdapter INSTANCE = new PersistableBundleAdapter();

  @Nullable @Override public PersistableBundle readFromParcel(@NonNull Parcel source) {
    return source.readPersistableBundle(getClass().getClassLoader());
  }

  @Override public void writeToParcel(@Nullable PersistableBundle value, @NonNull Parcel dest, int flags) {
    dest.writePersistableBundle(value);
  }

  private PersistableBundleAdapter() {}
}
