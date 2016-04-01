package nz.bradcampbell.paperparcel.kotlinexample

import android.os.Parcel
import nz.bradcampbell.paperparcel.TypeAdapter
import java.util.*

object DateTypeAdapter : TypeAdapter<Date> {
  override fun writeToParcel(value: Date, outParcel: Parcel, flags: Int) {
    outParcel.writeLong(value.time)
  }

  override fun readFromParcel(inParcel: Parcel): Date {
    return Date(inParcel.readLong())
  }
}
