package nz.bradcampbell.kotlinexample

import paperparcel.PaperParcelable
import paperparcel.PaperParcel
import java.util.Date

@PaperParcel
data class State(
    val count: Int,
    val modificationDate: Date
) : PaperParcelable {
  @Transient val somethingToExclude = 10000L

  companion object {
    @JvmField val CREATOR = PaperParcelState.CREATOR
  }
}
