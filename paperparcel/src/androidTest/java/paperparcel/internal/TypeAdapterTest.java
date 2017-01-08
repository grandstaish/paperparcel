package paperparcel.internal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Test;
import paperparcel.TypeAdapter;
import paperparcel.internal.ArrayAdapter;
import paperparcel.internal.CollectionAdapter;
import paperparcel.internal.EnumAdapter;
import paperparcel.internal.MapAdapter;
import paperparcel.internal.ParcelableAdapter;
import paperparcel.internal.SerializableAdapter;
import paperparcel.internal.SparseArrayAdapter;
import paperparcel.internal.StaticAdapters;

import static com.google.common.truth.Truth.assertThat;

public class TypeAdapterTest {

  @Test public void arraysAreCorrectlyParcelled() {
    TypeAdapter<String[]> adapter = new ArrayAdapter<>(String.class, StaticAdapters.STRING_ADAPTER);
    String[] expected = new String[] { "hello world" };
    String[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void booleansAreCorrectlyParcelled() {
    TypeAdapter<Boolean> adapter = StaticAdapters.BOOLEAN_ADAPTER;
    Boolean resultTrue = writeThenRead(adapter, true);
    assertThat(resultTrue).isEqualTo(true);
    Boolean resultFalse = writeThenRead(adapter, false);
    assertThat(resultFalse).isEqualTo(false);
  }

  @Test public void booleanArraysAreCorrectlyParcelled() {
    TypeAdapter<boolean[]> adapter = StaticAdapters.BOOLEAN_ARRAY_ADAPTER;
    boolean[] expected = new boolean[] { true, false };
    boolean[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void bytesAreCorrectlyParcelled() {
    TypeAdapter<Byte> adapter = StaticAdapters.BYTE_ADAPTER;
    Byte expected = (byte) 42;
    Byte result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void byteArraysAreCorrectlyParcelled() {
    TypeAdapter<byte[]> adapter = StaticAdapters.BYTE_ARRAY_ADAPTER;
    byte[] expected = new byte[] { 42 };
    byte[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void charactersAreCorrectlyParcelled() {
    TypeAdapter<Character> adapter = StaticAdapters.CHARACTER_ADAPTER;
    Character expected = 42;
    Character result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void charArraysAreCorrectlyParcelled() {
    TypeAdapter<char[]> adapter = StaticAdapters.CHAR_ARRAY_ADAPTER;
    char[] expected = new char[] { 42 };
    char[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void charSequencesAreCorrectlyParcelled() {
    TypeAdapter<CharSequence> adapter = StaticAdapters.CHAR_SEQUENCE_ADAPTER;
    CharSequence expected = "Hello";
    CharSequence result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void listsAreCorrectlyParcelled() {
    CollectionAdapter<Integer> adapter = new CollectionAdapter<>(StaticAdapters.INTEGER_ADAPTER);
    Collection<Integer> expected = Arrays.asList(42, 0);
    Collection<Integer> result = writeThenRead(adapter, expected);
    assertItemsAreEqual(expected, result);
  }

  @Test public void doublesAreCorrectlyParcelled() {
    TypeAdapter<Double> adapter = StaticAdapters.DOUBLE_ADAPTER;
    Double expected = 42.42;
    Double result = writeThenRead(adapter, expected);
    assertThat(result).isWithin(0.0).of(expected);
  }

  @Test public void doubleArraysAreCorrectlyParcelled() {
    TypeAdapter<double[]> adapter = StaticAdapters.DOUBLE_ARRAY_ADAPTER;
    double[] expected = new double[] { 42.42 };
    double[] result = writeThenRead(adapter, expected);
    assertThat(result).hasValuesWithin(0).of(expected);
  }

  @Test public void enumsAreCorrectlyParcelled() {
    TypeAdapter<TestEnum> adapter = new EnumAdapter<>(TestEnum.class);
    TestEnum expected = TestEnum.A;
    TestEnum result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
    expected = TestEnum.B;
    result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void floatsAreCorrectlyParcelled() {
    TypeAdapter<Float> adapter = StaticAdapters.FLOAT_ADAPTER;
    Float expected = 42.42f;
    Float result = writeThenRead(adapter, expected);
    assertThat(result).isWithin(0).of(expected);
  }

  @Test public void floatArraysAreCorrectlyParcelled() {
    TypeAdapter<float[]> adapter = StaticAdapters.FLOAT_ARRAY_ADAPTER;
    float[] expected = new float[] { 42.42f };
    float[] result = writeThenRead(adapter, expected);
    assertThat(result).hasValuesWithin(0).of(expected);
  }

  @Test public void intArraysAreCorrectlyParcelled() {
    TypeAdapter<int[]> adapter = StaticAdapters.INT_ARRAY_ADAPTER;
    int[] expected = new int[] { 42 };
    int[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void integersAreCorrectlyParcelled() {
    TypeAdapter<Integer> adapter = StaticAdapters.INTEGER_ADAPTER;
    Integer expected = 42;
    Integer result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void longsAreCorrectlyParcelled() {
    TypeAdapter<Long> adapter = StaticAdapters.LONG_ADAPTER;
    Long expected = Long.MAX_VALUE;
    Long result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void longArraysAreCorrectlyParcelled() {
    TypeAdapter<long[]> adapter = StaticAdapters.LONG_ARRAY_ADAPTER;
    long[] expected = new long[] { Long.MAX_VALUE, Long.MIN_VALUE };
    long[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void mapsAreCorrectlyParcelled() {
    TypeAdapter<Map<String, Integer>> adapter =
        new MapAdapter<>(StaticAdapters.STRING_ADAPTER, StaticAdapters.INTEGER_ADAPTER);
    Map<String, Integer> expected = new HashMap<>();
    expected.put("LIFE_MEANING", 42);
    Map<String, Integer> result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void parcelablesAreCorrectlyParcelled() {
    TypeAdapter<TestParcelable> adapter = new ParcelableAdapter<>(TestParcelable.CREATOR);
    TestParcelable expected = new TestParcelable(42);
    TestParcelable result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void shortsAreCorrectlyParcelled() {
    TypeAdapter<Short> adapter = StaticAdapters.SHORT_ADAPTER;
    Short expected = 42;
    Short result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void shortArraysAreCorrectlyParcelled() {
    TypeAdapter<short[]> adapter = StaticAdapters.SHORT_ARRAY_ADAPTER;
    short[] expected = new short[] { 42 };
    short[] result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Test public void sizeIsCorrectlyParcelled() {
    TypeAdapter<Size> adapter = StaticAdapters.SIZE_ADAPTER;
    Size expected = new Size(10, 10);
    Size result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Test public void sizeFIsCorrectlyParcelled() {
    TypeAdapter<SizeF> adapter = StaticAdapters.SIZE_F_ADAPTER;
    SizeF expected = new SizeF(10.f, 10.f);
    SizeF result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @SuppressLint("UseSparseArrays")
  @Test public void sparseArraysAreCorrectlyParcelled() {
    TypeAdapter<SparseArray<Integer>> adapter = new SparseArrayAdapter<>(StaticAdapters.INTEGER_ADAPTER);
    SparseArray<Integer> expected = new SparseArray<>();
    expected.put(42, 42);
    SparseArray<Integer> result = writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }

  @Test public void sparseBooleanArraysAreCorrectlyParcelled() {
    TypeAdapter<SparseBooleanArray> adapter = StaticAdapters.SPARSE_BOOLEAN_ARRAY_ADAPTER;
    SparseBooleanArray expected = new SparseBooleanArray();
    expected.put(42, false);
    expected.put(420, true);
    SparseBooleanArray result = writeThenRead(adapter, expected);
    assertThat(expected.size()).isEqualTo(result.size());
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.keyAt(i)).isEqualTo(expected.keyAt(i));
      assertThat(result.valueAt(i)).isEqualTo(expected.valueAt(i));
    }
  }

  @Test public void stringsAreCorrectlyParcelled() {
    TypeAdapter<String> adapter = StaticAdapters.STRING_ADAPTER;
    String expected = "hello world";
    String result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  @Test public void serializableTypesAreCorrectlyParcelled() {
    TypeAdapter<String> adapter = new SerializableAdapter<>();
    String expected = "hello world";
    String result = writeThenRead(adapter, expected);
    assertThat(result).isEqualTo(expected);
  }

  private static class TestParcelable implements Parcelable {
    public static final Parcelable.Creator<TestParcelable> CREATOR =
        new Parcelable.Creator<TestParcelable>() {

          @Override
          public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
          }

          @Override
          public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
          }
        };

    private final int value;

    TestParcelable(int value) {
      this.value = value;
    }

    TestParcelable(Parcel in) {
      value = in.readInt();
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(value);
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TestParcelable that = (TestParcelable) o;

      return value == that.value;
    }

    @Override public int hashCode() {
      return value;
    }
  }

  private enum TestEnum {
    A,
    B
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

  private static <A extends TypeAdapter<T>, T> T writeThenRead(A adapter, T input) {
    Parcel parcel = Parcel.obtain();
    adapter.writeToParcel(input, parcel, 0);
    parcel.setDataPosition(0);
    T result = adapter.readFromParcel(parcel);
    parcel.recycle();
    return result;
  }
}
