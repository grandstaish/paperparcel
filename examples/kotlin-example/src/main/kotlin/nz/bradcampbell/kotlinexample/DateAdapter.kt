package nz.bradcampbell.kotlinexample

import android.os.Parcel
import paperparcel.AbstractAdapter
import paperparcel.RegisterAdapter
import java.util.Date

@RegisterAdapter
object DateAdapter : AbstractAdapter<Date>() {
  override fun read(source: Parcel): Date {
    return Date(source.readLong())
  }

  override fun write(value: Date, dest: Parcel, flags: Int) {
    dest.writeLong(value.time)
  }
}
