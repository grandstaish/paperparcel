package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class DoubleAdapter implements TypeAdapter<Double> {
  public static final DoubleAdapter INSTANCE = new DoubleAdapter();

  @Override public Double readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(Double value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private DoubleAdapter() {
    throw new RuntimeException("Stub!");
  }
}
