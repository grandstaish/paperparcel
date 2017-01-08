package paperparcel.internal;

import android.os.Parcel;
import android.os.Parcelable;
import org.junit.Test;
import paperparcel.PaperParcel;
import paperparcel.ProcessorConfig;
import paperparcel.TypeAdapter;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@ProcessorConfig(reflectAnnotations = UtilsTest.Reflect.class)
public class UtilsTest {

  @Test public void canReadPrivateField() {
    int expected = 100;
    PrivateRead privateRead = new PrivateRead(expected);
    int actual = Utils.readField(int.class, PrivateRead.class, privateRead, "myPrivateField");
    assertThat(expected).isEqualTo(actual);
  }

  @Test public void canWritePrivateField() {
    int expected = 100;
    PrivateWrite privateWrite = new PrivateWrite();
    Utils.writeField(expected, PrivateWrite.class, privateWrite, "myPrivateField");
    assertThat(expected).isEqualTo(privateWrite.getMyPrivateField());
  }

  @Test public void canCallPrivateConstructor() {
    Utils.init(PrivateConstructor.class, new Class[] {}, new Object[] {});
  }

  @SuppressWarnings("ConstantConditions")
  @Test public void nullSafeClone() {
    Integer expected = null;
    Parcel parcel = Parcel.obtain();
    try {
      StaticAdapters.INTEGER_ADAPTER.writeToParcel(expected, parcel, 0);
      fail();
    } catch (NullPointerException ignore) {
    }
    TypeAdapter<Integer> nullSafeIntAdapter = Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER);
    nullSafeIntAdapter.writeToParcel(expected, parcel, 0);
    parcel.setDataPosition(0);
    Integer actual = nullSafeIntAdapter.readFromParcel(parcel);
    parcel.recycle();
    assertThat(expected).isNull();
    assertThat(actual).isNull();
  }

  @SuppressWarnings("ConstantConditions")
  @Test public void inlineWriteAndReadNullable() {
    Integer expected = null;
    Parcel parcel = Parcel.obtain();
    try {
      StaticAdapters.INTEGER_ADAPTER.writeToParcel(expected, parcel, 0);
      fail();
    } catch (NullPointerException ignore) {
    }
    Utils.writeNullable(expected, parcel, 0, StaticAdapters.INTEGER_ADAPTER);
    parcel.setDataPosition(0);
    Integer actual = Utils.readNullable(parcel, StaticAdapters.INTEGER_ADAPTER);
    parcel.recycle();
    assertThat(expected).isNull();
    assertThat(actual).isNull();
  }

  @interface Reflect {}

  @PaperParcel
  static final class PrivateRead implements Parcelable {
    public static final Parcelable.Creator<PrivateRead> CREATOR
        = PaperParcelUtilsTest_PrivateRead.CREATOR;

    @Reflect
    private int myPrivateField;

    PrivateRead(int myPrivateField) {
      this.myPrivateField = myPrivateField;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      PaperParcelUtilsTest_PrivateRead.writeToParcel(this, dest, flags);
    }
  }

  @PaperParcel
  static final class PrivateWrite implements Parcelable {
    public static final Parcelable.Creator<PrivateWrite> CREATOR
        = PaperParcelUtilsTest_PrivateWrite.CREATOR;

    @Reflect
    private int myPrivateField;

    int getMyPrivateField() {
      return myPrivateField;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      PaperParcelUtilsTest_PrivateWrite.writeToParcel(this, dest, flags);
    }
  }

  @PaperParcel
  static final class PrivateConstructor implements Parcelable {
    public static final Parcelable.Creator<PrivateConstructor> CREATOR
        = PaperParcelUtilsTest_PrivateConstructor.CREATOR;

    private int myPrivateField;

    @Reflect
    private PrivateConstructor() {
    }

    int getMyPrivateField() {
      return myPrivateField;
    }

    void setMyPrivateField(int myPrivateField) {
      this.myPrivateField = myPrivateField;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      PaperParcelUtilsTest_PrivateConstructor.writeToParcel(this, dest, flags);
    }
  }
}
