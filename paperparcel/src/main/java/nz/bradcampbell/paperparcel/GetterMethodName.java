package nz.bradcampbell.paperparcel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotate a property to specify what the exact name of the getter method is. By default, if "x" is the property name,
 * PaperParcel will search for a method named "x()", "getX()", or "isX()". If your getter method is none of these, you
 * must annotate your property with this annotation, e.g.:
 *
 * <pre><code>
 * &#64;GetterMethodName("customGetterMethodName")
 * private final int someField;
 * </code></pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface GetterMethodName {
  String value();
}
