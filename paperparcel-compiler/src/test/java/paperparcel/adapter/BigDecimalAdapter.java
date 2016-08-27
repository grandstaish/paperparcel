package paperparcel.adapter;

import android.os.Parcel;
import java.math.BigDecimal;

public final class BigDecimalAdapter extends AbstractAdapter<BigDecimal> {
  public static final BigDecimalAdapter INSTANCE = new BigDecimalAdapter();

  @Override protected BigDecimal read(Parcel source) {
    throw new RuntimeException("Stub!");
  }

  @Override protected void write(BigDecimal value, Parcel dest, int flags) {
    throw new RuntimeException("Stub!");
  }

  private BigDecimalAdapter() {
    throw new RuntimeException("Stub!");
  }
}
