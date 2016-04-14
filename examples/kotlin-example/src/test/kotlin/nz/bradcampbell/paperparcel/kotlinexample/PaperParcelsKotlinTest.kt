package nz.bradcampbell.paperparcel.kotlinexample

import android.os.Parcel
import android.os.Parcelable
import com.google.common.truth.Truth.assertThat
import nz.bradcampbell.paperparcel.PaperParcels
import nz.bradcampbell.paperparcel.TypedParcelable
import org.junit.Test
import java.util.*

class PaperParcelsKotlinTest {
  @Test
  fun basicPaperParcelsTest() {
    val originalObj = State(1, Date())
    val stateParcel = PaperParcels.wrap(originalObj)
    val unwrapped = PaperParcels.unwrap<State>(stateParcel)
    assertThat(originalObj).isEqualTo(unwrapped)
  }

  @Test
  fun wrapNonPaperParcelsType() {
    try {
      PaperParcels.wrap("foo")
    } catch (e: Throwable) {
      assertThat(e).isInstanceOf(IllegalArgumentException::class.java)
    }
  }

  @Test
  fun unwrapNonPaperParcelableType() {
    try {
      PaperParcels.unwrap<Any>(NonPaperParcelable("taco"))
    } catch (e: Throwable) {
      assertThat(e).isInstanceOf(IllegalArgumentException::class.java)
    }
  }

  @Test
  fun unwrapNonGeneratedTypedParcelable() {
    try {
      PaperParcels.unwrap<Any>(NonGeneratedTypedParcelable(Taco("taco")))
    } catch (e: Throwable) {
      assertThat(e).isInstanceOf(IllegalArgumentException::class.java)
    }
  }

  private class NonGeneratedTypedParcelable(private val taco: Taco) : TypedParcelable<Taco> {
    override fun get(): Taco {
      return taco;
    }

    override fun describeContents(): Int {
      return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(taco.taco)
    }

    companion object {
      @JvmField val CREATOR: Parcelable.Creator<Taco> = object : Parcelable.Creator<Taco> {
        override fun createFromParcel(parcel: Parcel): Taco {
          return Taco(parcel)
        }

        override fun newArray(size: Int): Array<Taco?> {
          return arrayOfNulls(size)
        }
      }
    }
  }

  private class Taco {
    val taco: String

    constructor(taco: String) {
      this.taco = taco
    }

    constructor(parcel: Parcel) {
      taco = parcel.readString()
    }
  }

  private class NonPaperParcelable : Parcelable {
    private val taco: String

    constructor(taco: String) {
      this.taco = taco
    }

    private constructor(parcel: Parcel) {
      taco = parcel.readString()
    }

    override fun describeContents(): Int {
      return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(taco)
    }

    companion object {
      @JvmField val CREATOR: Parcelable.Creator<NonPaperParcelable> = object : Parcelable.Creator<NonPaperParcelable> {
        override fun createFromParcel(parcel: Parcel): NonPaperParcelable {
          return NonPaperParcelable(parcel)
        }

        override fun newArray(size: Int): Array<NonPaperParcelable?> {
          return arrayOfNulls(size)
        }
      }
    }
  }
}
