package nz.bradcampbell.paperparcel

import android.os.Parcel
import android.os.Parcelable

interface PaperParcelable : Parcelable {
  class Creator<T>(private val type: Class<out T>) : Parcelable.Creator<T> {
    override fun createFromParcel(parcel: Parcel): T {
      return PaperParcels.unwrap<T>(parcel.readParcelable<Parcelable>(type.classLoader))
    }

    override fun newArray(i: Int): Array<T> {
      return PaperParcels.newArray(type, i)
    }
  }

  override fun writeToParcel(outParcel: Parcel, flags: Int) {
    outParcel.writeParcelable(PaperParcels.wrap(this), flags)
  }

  override fun describeContents(): Int {
    return 0
  }
}
