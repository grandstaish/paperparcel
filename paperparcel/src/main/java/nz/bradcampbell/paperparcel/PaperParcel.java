package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.os.Parcelable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * For use on Kotlin data classes to generate parcelable "wrapper" classes that know how to read and write this
 * object to and from an {@link android.os.Parcel}
 *
 * Generated classes will be in the format {ClassName} + "Parcel", e.g.:
 * <pre><code>
 *   &#64;PaperParcel
 *   data class Example(val a: Int)
 * </code></pre>
 * Will produce ExampleParcel.java in the same package as your data class.
 *
 * &#64;PaperParcel cannot be used directly on a generic data class
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface PaperParcel {

  /**
   * @return The flag that will be used in the generated {@link Parcelable#describeContents()} method. Defaults to 0.
   */
  int describeContents() default 0;
}
