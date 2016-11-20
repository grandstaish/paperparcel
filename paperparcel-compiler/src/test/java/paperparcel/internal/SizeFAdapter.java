package paperparcel.internal;

import android.os.Parcel;
import android.util.SizeF;
import paperparcel.TypeAdapter;

public final class SizeFAdapter implements TypeAdapter<SizeF> {
  public static final SizeFAdapter INSTANCE = new SizeFAdapter();

  @Override public SizeF readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(SizeF value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SizeFAdapter() {
    throw new RuntimeException("Stub!");
  }
}
