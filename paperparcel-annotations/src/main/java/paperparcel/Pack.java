package paperparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should be exposed for parcelling.
 *
 * <p>This annotation has no effect unless you set
 * {@link PaperParcel#excludeFieldsWithoutPackAnnotation()} to {@code true} in the field's
 * containing class.</p>
 *
 * <p>Here is an example of how this annotation is meant to be used:
 * <p><pre>
 * &#64PaperParcel(excludeFieldsWithoutPackAnnotation = true)
 * public final class User implements Parcelable {
 *   &#Pack String username;
 *   String password;
 *   // ...
 * }
 * </pre></p>
 * This will mean that the generated {@code CREATOR} and {@code writeToParcel(...)}
 * implementations will not include code for parcelling the {@code password} field. They will only
 * include the {@code username} field because it is annotated with {@code Pack}.
 *
 * <p>Note that another way to achieve the same effect would have been to just mark the
 * {@code password} field as {@code transient}, and PaperParcel would have excluded it with
 * the default settings. The {@code @Pack} annotation is useful in a style of programming where
 * you want to explicitly specify all fields that should get considered for parcelling.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Pack {
}
