package nz.bradcampbell.paperparcel.kotlinexample

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import nz.bradcampbell.paperparcel.TypeAdapters
import java.util.Date

@PaperParcel
@TypeAdapters(DateTypeAdapter::class)
data class State(val count: Int, val modificationDate: Date) : PaperParcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelable.Creator(State::class.java)
  }
}
