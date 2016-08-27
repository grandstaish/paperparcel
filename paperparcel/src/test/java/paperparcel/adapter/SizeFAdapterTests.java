package paperparcel.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.SizeF;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class SizeFAdapterTests {
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Test public void sizeFIsCorrectlyParcelled() {
    SizeFAdapter adapter = SizeFAdapter.INSTANCE;
    SizeF expected = new SizeF(10.f, 10.f);
    SizeF result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
