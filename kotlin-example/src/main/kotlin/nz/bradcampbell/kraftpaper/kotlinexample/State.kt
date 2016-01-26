package nz.bradcampbell.kraftpaper.kotlinexample

import nz.bradcampbell.kraftpaper.KraftPaper
import java.util.*

@KraftPaper(typeAdapters = arrayOf(DateTypeAdapter::class))
data class State(val count: Int, val modificationDate: Date)
