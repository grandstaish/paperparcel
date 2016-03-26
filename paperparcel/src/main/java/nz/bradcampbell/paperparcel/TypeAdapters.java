package nz.bradcampbell.paperparcel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Scope custom type adapters directly to variables or classes. You can apply as many type adapters as you want
 * against a class or variable (for AutoValue classes, apply this annotation directly to the abstract accessor method).
 *
 * TypeAdapters defined this way will override the default type adapters of the same type (set via
 * {@link DefaultAdapter}). Additionally, variable-scoped adapters will take precedence over class-scoped adapters.
 *
 * Example:
 * <pre><code>
 *   &#64;TypeAdapters(ClassScopedTypeAdapter::class)
 *   data class Example(
 *       val a: Int,
 *       &#64;TypeAdapters(VariableScopedTypeAdapter::class) val b: Map&lt;SomeKey, SomeValue&gt;)
 * </code></pre>
 */
@Documented
@Retention(CLASS)
@Target({ FIELD, METHOD, TYPE })
public @interface TypeAdapters {
  Class<? extends TypeAdapter<?>>[] value() default {};
}
