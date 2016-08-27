package nz.bradcampbell.kotlinexample

import android.os.Parcel
import android.os.Parcelable
import paperparcel.Exclude
import paperparcel.PaperParcel
import java.util.Date

@PaperParcel
data class State(
    val count: Int,
    val modificationDate: Date
) : DefaultParcelable {
  @Exclude val somethingToExclude = 10000L

  companion object {
    @JvmField val CREATOR = PaperParcelState.CREATOR
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    PaperParcelState.writeToParcel(this, dest, flags)
  }
}
