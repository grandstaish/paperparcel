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
public class ArrayAdapterTests {
  @Test public void arraysAreCorrectlyParcelled() {
    TypeAdapter<String[]> adapter = new ArrayAdapter<>(String.class, StaticAdapters.STRING_ADAPTER);
    String[] expected = new String[] { "hello world" };
    String[] result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
