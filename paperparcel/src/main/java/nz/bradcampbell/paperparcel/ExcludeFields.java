package nz.bradcampbell.paperparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * An annotation for excluding certain fields from the parcelling/unparcelling process. Can
 * be applied to any class annotated with {@link PaperParcel}
 */
@Documented @Retention(CLASS) @Target({ TYPE })
public @interface ExcludeFields {
  FieldMatcher[] value();
}
