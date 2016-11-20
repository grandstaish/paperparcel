package paperparcel.internal;

import android.os.Parcel;
import android.util.SparseBooleanArray;
import paperparcel.AbstractAdapter;

public final class SparseBooleanArrayAdapter extends AbstractAdapter<SparseBooleanArray> {
  public static final SparseBooleanArrayAdapter INSTANCE = new SparseBooleanArrayAdapter();

  @Override protected SparseBooleanArray read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(SparseBooleanArray value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private SparseBooleanArrayAdapter() {
    throw new RuntimeException("Stub!");
  }
}
