package paperparcel.adapter;

import java.util.Date;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DateAdapterTests {
  @Test public void datesAreCorrectlyParcelled() {
    DateAdapter adapter = DateAdapter.INSTANCE;
    Date expected = new Date();
    Date result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
