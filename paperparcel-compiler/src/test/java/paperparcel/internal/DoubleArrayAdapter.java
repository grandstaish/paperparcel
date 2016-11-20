package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class DoubleArrayAdapter implements TypeAdapter<double[]> {
  public static final DoubleArrayAdapter INSTANCE = new DoubleArrayAdapter();

  @Override public double[] readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(double[] value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private DoubleArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
