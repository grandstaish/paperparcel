package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate any {@link TypeAdapter} with this annotation to globally use this type adapter with anything
 * processed by PaperParcel.
 *
 * TypeAdapters applied using {@link PaperParcel} or {@link FieldTypeAdapter} will override any TypeAdapters annotated
 * with this annotation.
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface GlobalTypeAdapter {
}
