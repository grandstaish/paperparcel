package paperparcel.internal.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.SparseLongArray;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class SparseLongArrayAdapterTests {
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  @Test public void sparseLongArraysAreCorrectlyParcelled() {
    SparseLongArrayAdapter adapter = SparseLongArrayAdapter.INSTANCE;
    SparseLongArray expected = new SparseLongArray();
    expected.put(42, 123);
    expected.put(420, Long.MAX_VALUE);
    SparseLongArray result = TestUtils.writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }
}
