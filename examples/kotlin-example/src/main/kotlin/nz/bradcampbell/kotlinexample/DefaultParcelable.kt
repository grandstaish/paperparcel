package nz.bradcampbell.kotlinexample

import android.os.Parcelable

/** {@link Parcelable} with a default {@link Parcelable#describeContents()} implementation */
interface DefaultParcelable : Parcelable {
  override fun describeContents() = 0
}
