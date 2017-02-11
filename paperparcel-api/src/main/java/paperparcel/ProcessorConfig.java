package paperparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
  /** Defines all of the custom {@link TypeAdapter}s to register into PaperParcel's type system. */
  Adapter[] adapters() default {};

  /** Defines the default processor options for {@literal @}PaperParcel classes. */
  PaperParcel.Options options() default @PaperParcel.Options;
}
