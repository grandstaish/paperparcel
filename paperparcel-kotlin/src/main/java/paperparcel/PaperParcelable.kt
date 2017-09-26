package paperparcel

import android.os.Parcel
import android.os.Parcelable
import java.lang.reflect.Method

/**
 * [Parcelable] with a default [Parcelable.describeContents] and [Parcelable.writeToParcel]
 * implementations.
 *
 * @since 2.0
 */
interface PaperParcelable : Parcelable {
  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    val writeMethod = synchronized(writeMethods) {
      writeMethods.getOrPut(javaClass) {
        val annotatedClass = javaClass.annotatedClass
        val generatedClass = Class.forName(annotatedClass.implementationName)
        generatedClass
                .getDeclaredMethod("writeToParcel", annotatedClass, Parcel::class.java, Int::class.javaPrimitiveType)
                .apply {
                  isAccessible = true
                }
      }
    }
    writeMethod.invoke(null, this, dest, flags)
  }

  private val Class<*>.annotatedClass: Class<*>
    get() {
      if (isAnnotationPresent(PaperParcel::class.java)) {
        return this
      }
      require(this != Any::class.java) {
        "Cannot find @${PaperParcel::class.java.simpleName} on ${this@PaperParcelable.javaClass.name}."
      }
      return superclass.annotatedClass
    }

  private val Class<*>.implementationName: String
    get() = "${`package`.name}.PaperParcel${name.substring(`package`.name.length + 1).replace('$', '_')}"

  private companion object {
    val writeMethods = HashMap<Class<*>, Method>()
  }
}
