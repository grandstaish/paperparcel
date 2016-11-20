package paperparcel.internal;

import android.os.Parcel;
import paperparcel.AbstractAdapter;

public final class DoubleArrayAdapter extends AbstractAdapter<double[]> {
  public static final DoubleArrayAdapter INSTANCE = new DoubleArrayAdapter();

  @Override protected double[] read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(double[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private DoubleArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
