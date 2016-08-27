package paperparcel.adapter;

import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BooleanAdapterTests {
  @Test public void booleansAreCorrectlyParcelled() {
    BooleanAdapter adapter = BooleanAdapter.INSTANCE;

    Boolean resultTrue = TestUtils.writeThenRead(adapter, true);
    assertThat(resultTrue).isEqualTo(true);

    Boolean resultFalse = TestUtils.writeThenRead(adapter, false);
    assertThat(resultFalse).isEqualTo(false);
  }
}
