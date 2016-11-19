package paperparcel.internal.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.LongSparseArray;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 16)
public class LongSparseArrayAdapterTests {
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Test public void sparseArraysAreCorrectlyParcelled() {
    LongSparseArrayAdapter<Integer> adapter = new LongSparseArrayAdapter<>(IntegerAdapter.INSTANCE);
    LongSparseArray<Integer> expected = new LongSparseArray<>();
    expected.put(42, 42);
    expected.put(Long.MAX_VALUE, 1337);
    LongSparseArray<Integer> result = TestUtils.writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }
}
