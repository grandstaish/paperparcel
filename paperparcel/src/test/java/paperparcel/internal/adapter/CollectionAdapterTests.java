package paperparcel.internal.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import paperparcel.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CollectionAdapterTests {
  @Test public void listsAreCorrectlyParcelled() {
    CollectionAdapter<Integer> adapter = new CollectionAdapter<>(IntegerAdapter.INSTANCE);
    Collection<Integer> expected = Arrays.asList(42, 0);
    Collection<Integer> result = TestUtils.writeThenRead(adapter, expected);
    assertItemsAreEqual(expected, result);
  }

  private <T> void assertItemsAreEqual(Collection<T> first, Collection<T> second) {
    assertThat(first.size()).isEqualTo(second.size());
    Iterator<T> firstIterator = first.iterator();
    Iterator<T> secondIterator = second.iterator();
    while (firstIterator.hasNext()) {
      T firstItem = firstIterator.next();
      T secondItem = secondIterator.next();
      assertThat(firstItem).isEqualTo(secondItem);
    }
  }
}
