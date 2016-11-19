package paperparcel.internal.adapter;

import android.util.SparseIntArray;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SparseIntArrayAdapterTests {
  @Test public void sparseIntArraysAreCorrectlyParcelled() {
    SparseIntArrayAdapter adapter = SparseIntArrayAdapter.INSTANCE;
    SparseIntArray expected = new SparseIntArray();
    expected.put(42, 123);
    expected.put(420, 456);
    SparseIntArray result = TestUtils.writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }
}
