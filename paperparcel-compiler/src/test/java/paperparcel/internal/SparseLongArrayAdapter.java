package paperparcel.internal;

import android.os.Parcel;
import android.util.SparseLongArray;
import paperparcel.TypeAdapter;

public final class SparseLongArrayAdapter implements TypeAdapter<SparseLongArray> {
  public static final SparseLongArrayAdapter INSTANCE = new SparseLongArrayAdapter();

  @Override public SparseLongArray readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(SparseLongArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseLongArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
