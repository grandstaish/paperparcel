package paperparcel.internal;

import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FloatArrayAdapterTests {
  @Test public void byteArraysAreCorrectlyParcelled() {
    FloatArrayAdapter adapter = FloatArrayAdapter.INSTANCE;
    float[] expected = new float[] { 42.42f };
    float[] result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).hasValuesWithin(0).of(expected);
  }
}
