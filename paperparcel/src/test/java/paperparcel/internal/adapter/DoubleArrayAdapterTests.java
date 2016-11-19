package paperparcel.internal.adapter;

import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DoubleArrayAdapterTests {
  @Test public void doubleArraysAreCorrectlyParcelled() {
    DoubleArrayAdapter adapter = DoubleArrayAdapter.INSTANCE;
    double[] expected = new double[] { 42.42 };
    double[] result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).hasValuesWithin(0).of(expected);
  }
}
