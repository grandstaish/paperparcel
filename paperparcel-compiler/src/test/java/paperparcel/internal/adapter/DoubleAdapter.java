package paperparcel.internal.adapter;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class DoubleAdapter extends AbstractAdapter<Double> {
  public static final DoubleAdapter INSTANCE = new DoubleAdapter();

  @Override protected Double read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(Double value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private DoubleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
