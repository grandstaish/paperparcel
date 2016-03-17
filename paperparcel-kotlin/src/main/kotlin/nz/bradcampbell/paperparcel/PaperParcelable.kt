package nz.bradcampbell.paperparcel

import android.os.Parcel
import android.os.Parcelable

interface PaperParcelable : Parcelable {
  companion object CREATOR : Parcelable.Creator<Any> {
    override fun createFromParcel(inParcel: Parcel): Any {
      return PaperParcels.unsafeUnwrap(inParcel.readParcelable(PaperParcelable::class.java.classLoader))
    }

    override fun newArray(size: Int): Array<Any?> {
      return arrayOfNulls(size)
    }
  }

  override fun writeToParcel(outParcel: Parcel, flags: Int) {
    outParcel.writeParcelable(PaperParcels.wrap(this), flags)
  }

  override fun describeContents(): Int {
    return 0
  }
}
