package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

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
@Retention(CLASS)
@Target(TYPE)
public @interface PaperParcel {
}
