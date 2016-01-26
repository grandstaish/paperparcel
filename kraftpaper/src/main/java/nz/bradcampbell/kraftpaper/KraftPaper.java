package nz.bradcampbell.kraftpaper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * For use on Kotlin data classes to generate parcelable "wrapper" classes that know how to read and write this
 * object to and from an {@link android.os.Parcel}
 *
 * Generated classes will be in the format {ClassName} + "Parcel", e.g.:
 * <pre><code>
 *   &#64;KraftPaper
 *   data class Example(val a: Int)
 * </code></pre>
 * Will produce ExampleParcel.java in the same package as your data class.
 *
 * &#64;KraftPaper cannot be used on a generic data class, e.g.:
 * <pre><code>
 *   &#64;KraftPaper
 *   data class BadExample<T>(val a: T)
 * </code></pre>
 */
@Retention(CLASS)
@Target(TYPE)
public @interface KraftPaper {

  /**
   * @return A list of custom TypeAdapters
   */
  Class<? extends TypeAdapter<?>>[] typeAdapters() default {};
}
