package nz.bradcampbell.kotlinexample

import android.os.Parcel
import paperparcel.RegisterAdapter
import paperparcel.TypeAdapter
import java.util.Date

@RegisterAdapter object DateAdapter : TypeAdapter<@JvmSuppressWildcards Date> {
  override fun readFromParcel(source: Parcel): Date {
    return Date(source.readLong())
  }

  override fun writeToParcel(value: Date, dest: Parcel, flags: Int) {
    dest.writeLong(value.time)
  }
}
