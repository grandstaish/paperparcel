package paperparcel.internal;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class IntegerAdapter extends AbstractAdapter<Integer> {
  public static final IntegerAdapter INSTANCE = new IntegerAdapter();

  @Override protected Integer read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Integer value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private IntegerAdapter() {
    throw new RuntimeException("Stub!");
  }
}
