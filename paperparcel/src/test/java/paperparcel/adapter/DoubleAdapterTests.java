package paperparcel.adapter;

import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DoubleAdapterTests {
  @Test public void doublesAreCorrectlyParcelled() {
    DoubleAdapter adapter = DoubleAdapter.INSTANCE;
    Double expected = 42.42;
    Double result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isWithin(0.0).of(expected);
  }
}
