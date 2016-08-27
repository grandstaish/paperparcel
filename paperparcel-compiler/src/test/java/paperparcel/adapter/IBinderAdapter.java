package paperparcel.adapter;

import android.os.IBinder;
import android.os.Parcel;

public final class IBinderAdapter extends AbstractAdapter<IBinder> {
  public static final IBinderAdapter INSTANCE = new IBinderAdapter();

  @Override public IBinder read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(IBinder value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IBinderAdapter() {
    throw new RuntimeException("Stub!");
  }
}
