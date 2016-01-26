package nz.bradcampbell.kraftpaper.example

import nz.bradcampbell.kraftpaper.KraftPaper
import java.util.*

@KraftPaper(typeAdapters = arrayOf(DateTypeAdapter::class))
data class State(val count: Int, val modificationDate: Date)
