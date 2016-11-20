package paperparcel.internal;

import android.os.Parcel;
import android.util.SparseBooleanArray;
import paperparcel.TypeAdapter;

public final class SparseBooleanArrayAdapter implements TypeAdapter<SparseBooleanArray> {
  public static final SparseBooleanArrayAdapter INSTANCE = new SparseBooleanArrayAdapter();

  @Override public SparseBooleanArray readFromParcel(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override public void writeToParcel(SparseBooleanArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseBooleanArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
