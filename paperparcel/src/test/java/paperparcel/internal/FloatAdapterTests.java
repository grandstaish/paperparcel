package paperparcel.internal;

import paperparcel.TypeAdapter;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FloatAdapterTests {
  @Test public void floatsAreCorrectlyParcelled() {
    TypeAdapter<Float> adapter = StaticAdapters.FLOAT_ADAPTER;
    Float expected = 42.42f;
    Float result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isWithin(0).of(expected);
  }
}
