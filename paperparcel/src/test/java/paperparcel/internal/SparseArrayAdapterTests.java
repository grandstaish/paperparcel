package paperparcel.internal;

import android.annotation.SuppressLint;
import android.util.SparseArray;
import paperparcel.TypeAdapter;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SparseArrayAdapterTests {
  @Test public void sparseArraysAreCorrectlyParcelled() {
    TypeAdapter<SparseArray<Integer>> adapter = new SparseArrayAdapter<>(StaticAdapters.INTEGER_ADAPTER);
    @SuppressLint("UseSparseArrays")
    SparseArray<Integer> expected = new SparseArray<>();
    expected.put(42, 42);
    SparseArray<Integer> result = TestUtils.writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }
}
