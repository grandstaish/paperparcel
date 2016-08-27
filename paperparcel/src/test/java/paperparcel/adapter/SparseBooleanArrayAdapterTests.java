package paperparcel.adapter;

import android.util.SparseBooleanArray;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SparseBooleanArrayAdapterTests {
  @Test public void sparseBooleanArraysAreCorrectlyParcelled() {
    SparseBooleanArrayAdapter adapter = SparseBooleanArrayAdapter.INSTANCE;
    SparseBooleanArray expected = new SparseBooleanArray();
    expected.put(42, false);
    expected.put(420, true);
    SparseBooleanArray result = TestUtils.writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }
}
