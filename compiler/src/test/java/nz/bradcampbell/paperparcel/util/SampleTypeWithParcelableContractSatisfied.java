package nz.bradcampbell.paperparcel.util;

import android.os.Parcel;
import android.os.Parcelable;

public class SampleTypeWithParcelableContractSatisfied implements Parcelable {
  public static final Creator<SampleTypeWithParcelableContractSatisfied> CREATOR =
      new Creator<SampleTypeWithParcelableContractSatisfied>() {
        @Override
        public SampleTypeWithParcelableContractSatisfied createFromParcel(Parcel in) {
          return null;
        }

        @Override
        public SampleTypeWithParcelableContractSatisfied[] newArray(int size) {
          return new SampleTypeWithParcelableContractSatisfied[size];
        }
      };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
  }
}
