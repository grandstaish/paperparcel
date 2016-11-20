package paperparcel.internal;

import android.os.IBinder;
import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class IBinderAdapter implements TypeAdapter<IBinder> {
  public static final IBinderAdapter INSTANCE = new IBinderAdapter();

  @Override public IBinder readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(IBinder value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IBinderAdapter() {
    throw new RuntimeException("Stub!");
  }
}
