package paperparcel.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import paperparcel.utils.TestEnum;
import paperparcel.utils.TestUtils;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EnumAdapterTests {
  @Test public void enumsAreCorrectlyParcelled() {
    EnumAdapter<TestEnum> adapter = new EnumAdapter<>();

    TestEnum expected = TestEnum.A;
    TestEnum result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);

    expected = TestEnum.C;
    result = TestUtils.writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }
}
