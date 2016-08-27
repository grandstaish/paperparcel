package paperparcel.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Size;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk=21)
public class SizeAdapterTests {
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Test public void sizeIsCorrectlyParcelled() {
    SizeAdapter adapter = SizeAdapter.INSTANCE;
    Size expected = new Size(10, 10);
    Size result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
