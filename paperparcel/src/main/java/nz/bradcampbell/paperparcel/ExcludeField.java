package nz.bradcampbell.paperparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Put this directly on a property to get PaperParcel to exclude it
 */
@Documented @Retention(CLASS) @Target(FIELD)
public @interface ExcludeField {
}
