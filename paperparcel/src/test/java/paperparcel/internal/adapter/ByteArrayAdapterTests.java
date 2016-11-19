package paperparcel.internal.adapter;

import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ByteArrayAdapterTests {
  @Test public void byteArraysAreCorrectlyParcelled() {
    ByteArrayAdapter adapter = ByteArrayAdapter.INSTANCE;
    byte[] expected = new byte[] { 42 };
    byte[] result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
