package nz.bradcampbell.kraftpaper.example

import android.os.Parcel
import nz.bradcampbell.kraftpaper.TypeAdapter
import java.util.*

class DateTypeAdapter : TypeAdapter<Date> {
    override fun writeToParcel(value: Date, outParcel: Parcel) {
        outParcel.writeLong(value.time)
    }

    override fun readFromParcel(inParcel: Parcel): Date {
        return Date(inParcel.readLong())
    }
}
