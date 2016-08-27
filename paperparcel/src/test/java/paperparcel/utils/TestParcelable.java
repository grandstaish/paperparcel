package paperparcel.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class TestParcelable implements Parcelable {
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

  public TestParcelable(int value) {
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
