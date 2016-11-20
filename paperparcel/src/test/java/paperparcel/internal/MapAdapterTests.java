package paperparcel.internal;

import java.util.HashMap;
import java.util.Map;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MapAdapterTests {
  @Test public void mapsAreCorrectlyParcelled() {
    MapAdapter<String, Integer> adapter = new MapAdapter<>(StringAdapter.INSTANCE, IntegerAdapter.INSTANCE);
    Map<String, Integer> expected = new HashMap<>();
    expected.put("LIFE_MEANING", 42);
    Map<String, Integer> result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
