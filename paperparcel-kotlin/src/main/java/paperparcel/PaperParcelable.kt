package paperparcel

import android.os.Parcel
import android.os.Parcelable
import java.lang.reflect.Method

/**
 * {@link Parcelable} with a default {@link Parcelable#describeContents()} and
 * {@link Parcelable#writeToParcel(Parcel, Int) implementations.
 */
interface PaperParcelable : Parcelable {
  object Cache {
    val writeMethods = mutableMapOf<Class<*>, Method>()
  }

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    if (!Cache.writeMethods.containsKey(javaClass)) {
      val annotatedClass = findAnnotatedClass(javaClass)
      val generatedClass = Class.forName(implementationName(annotatedClass))
      val args = arrayOf(annotatedClass, Parcel::class.java, Int::class.javaPrimitiveType)
      val writeMethod = generatedClass.getDeclaredMethod("writeToParcel", *args)
      writeMethod.isAccessible = true
      Cache.writeMethods.put(javaClass, writeMethod)
    }
    Cache.writeMethods[javaClass]!!.invoke(null, this, dest, flags)
  }

  private fun findAnnotatedClass(type: Class<*>): Class<*> {
    if (type.isAnnotationPresent(PaperParcel::class.java)) {
      return type
    }
    if (type == Any::class.java) {
      throw IllegalArgumentException(
          "Cannot find @${PaperParcel::class.java.simpleName} on ${javaClass.name}.")
    }
    return findAnnotatedClass(type.superclass)
  }

  private fun implementationName(type: Class<*>): String {
    val packageName = type.`package`.name
    val className = type.name.substring(packageName.length + 1).replace('$', '_')
    return packageName + ".PaperParcel" + className
  }
}
