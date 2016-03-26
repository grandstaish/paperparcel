package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate any {@link TypeAdapter} with this annotation to set the default method for parcelling/unparcelling an
 * object type.
 *
 * TypeAdapters applied using {@link TypeAdapters} on more specific elements (e.g. a class or a variable) will
 * take precedence over any TypeAdapters annotated with this annotation.
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface DefaultAdapter {
}
