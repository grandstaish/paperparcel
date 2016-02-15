package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Apply this annotation directly to a field (or accessor method) to use this TypeAdapter for the single field only.
 * This will take precedence over any globally-scoped or class-scoped TypeAdapters.
 */
@Documented
@Retention(CLASS)
@Target({ FIELD, METHOD })
public @interface FieldTypeAdapter {
  Class<? extends TypeAdapter<?>> value();
}
