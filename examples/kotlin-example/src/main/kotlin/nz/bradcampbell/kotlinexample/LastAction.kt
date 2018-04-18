package nz.bradcampbell.kotlinexample

import android.os.Parcelable
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

sealed class LastAction(val name: String) : PaperParcelable

@PaperParcel
object Initial : LastAction("Initial") {
    @JvmStatic
    val CREATOR: Parcelable.Creator<Initial> = PaperParcelInitial.CREATOR
}

@PaperParcel
object Decrement : LastAction("Decrement") {
    @JvmStatic
    val CREATOR: Parcelable.Creator<Decrement> = PaperParcelDecrement.CREATOR
}

@PaperParcel
object Increment : LastAction("Increment") {
    @JvmStatic
    val CREATOR: Parcelable.Creator<Increment> = PaperParcelIncrement.CREATOR
}