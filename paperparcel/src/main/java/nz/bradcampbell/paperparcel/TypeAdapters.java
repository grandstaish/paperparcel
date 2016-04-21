package nz.bradcampbell.paperparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Scope custom type adapters directly to variables or classes. You can apply as many type adapters
 * as you want
 * against a class or variable (for AutoValue classes, apply this annotation directly to the
 * abstract accessor method).
 *
 * TypeAdapters defined this way will override the default type adapters of the same type (set via
 * {@link DefaultAdapter}). Additionally, variable-scoped adapters will take precedence over
 * class-scoped adapters.
 */
@Documented @Retention(CLASS) @Target({ FIELD, METHOD, TYPE })
public @interface TypeAdapters {
  Class<? extends TypeAdapter<?>>[] value() default {};
}
