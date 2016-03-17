package nz.bradcampbell.paperparcel.kotlinexample

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel(typeAdapters = arrayOf(DateTypeAdapter::class))
data class State(val count: Int, val modificationDate: Date) : PaperParcelable
