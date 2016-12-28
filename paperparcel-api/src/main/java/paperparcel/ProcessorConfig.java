package paperparcel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * <p>Custom configuration of {@code PaperParcelProcessor}.</p>
 *
 * <p>This annotation can be applied to any annotation, interface, class, or package element in
 * your source code. While this annotation can be almost applied anywhere, the convention is to
 * put it in one of the following places:</p>
 *
 * <ul>
 *   <li>A custom {@code android.app.Application} class.</li>
 *   <li>An empty interface named {@code PaperParcelConfig}.</li>
 *   <li>A package-info.java file.</li>
 * </ul>
 *
 * <p>Note that this configuration will only apply to the module that it is defined in. If you
 * have a multi-module project where multiple modules are using PaperParcel, you'll need to
 * define a {@code ProcessorConfig} for each of those modules.</p>
 *
 * <p>Only one {@code ProcessorConfig} can be applied per module.</p>
 */
@Documented
@Retention(SOURCE)
@Target({ ANNOTATION_TYPE, TYPE, PACKAGE })
public @interface ProcessorConfig {
  /**
   * All of the custom {@link TypeAdapter}s to register into PaperParcel's type system.
   */
  Adapter[] adapters() default {};

  /**
   * Configures PaperParcel to exclude any field that is annotated with any of the given
   * annotations.
   */
  Class<? extends Annotation>[] excludeAnnotations() default {};

  /**
   * <p>Configures PaperParcel to only include fields that are annotated with any of the given
   * annotations.</p>
   *
   * <p>This API only works when returning {@code true} from {@link #excludeNonExposedFields()}.</p>
   *
   * @see #excludeNonExposedFields()
   */
  Class<? extends Annotation>[] exposeAnnotations() default {};

  /**
   * Configures PaperParcel to exclude all fields that are not annotated by one of the
   * annotations returned by {@link #exposeAnnotations()}. This is useful in a style of
   * programming where you want to explicitly specify all fields that should be considered for
   * parcelling.
   *
   * @see #exposeAnnotations()
   */
  boolean excludeNonExposedFields() default false;

  /**
   * <p>Configures PaperParcel to exclude any field that has the given modifiers. The
   * {@code int} values returned by this method must be {@link Modifier} constants. Modifiers
   * can be combined using the bitwise OR operator if you want to exclude specific combinations
   * of modifiers.</p>
   *
   * <p>By default any {@code transient} or {@code static} field is excluded.</p>
   */
  int[] excludeModifiers() default { Modifier.TRANSIENT, Modifier.STATIC };

  /**
   * <p>Configures PaperParcel to be able to access private constructors and fields that are
   * annotated with any of the given annotations (using reflection).</p>
   *
   * <p>Because reflection is slow on Android, this option should be used sparingly. Because
   * of the performance implications, PaperParcel will always favor <i>not</i> using
   * reflection when it is possible.</p>
   *
   * <p>Note: if your code will be obfuscated, be sure to retain the names of the annotated
   * fields and constructors so that the generated reflection calls will continue to work.</p>
   */
  Class<? extends Annotation>[] reflectAnnotations() default {};
}
