package paperparcel.adapter;

import java.math.BigDecimal;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BigDecimalAdapterTests {
  @Test public void bigDecimalsAreCorrectlyParcelled() {
    BigDecimalAdapter adapter = BigDecimalAdapter.INSTANCE;
    BigDecimal expected = new BigDecimal("42.42");
    BigDecimal result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
