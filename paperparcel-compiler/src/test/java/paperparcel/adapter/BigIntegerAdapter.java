package paperparcel.adapter;

import android.os.Parcel;
import java.math.BigInteger;

public final class BigIntegerAdapter extends AbstractAdapter<BigInteger> {
  public static final BigIntegerAdapter INSTANCE = new BigIntegerAdapter();

  @Override protected BigInteger read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(BigInteger value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BigIntegerAdapter() {
    throw new RuntimeException("Stub!");
  }
}
