package nz.bradcampbell.mixedexample

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Test(val i: Int) : PaperParcelable {
  companion object {
    @JvmField val CREATOR = PaperParcelable.Creator(Test::class.java)
  }
}
