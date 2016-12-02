/*
 * Copyright (C) 2016 Bradley Campbell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paperparcel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * <p>Annotates {@code Parcelable} classes to automatically generate the {@code Parcelable.Creator}
 * and {@code writeToParcel} boilerplate code that is required as a part of the {@code Parcelable}
 * contract. The generated code will be contained in a class with the name of the type annotated
 * with {@code @PaperParcel} prepended with {@code PaperParcel}. For example, annotating a class
 * named {@code MyModel} will produce a class named {@code PaperParcelMyModel}.
 * {@code PaperParcelMyModel} will have a static field named {@code CREATOR} which can be added to
 * your model class. Finally, {@code PaperParcelMyModel} will include a static method named
 * {@code writeToParcel} which you can call from {@code MyModel#writeToParcel}.</p>
 *
 * <p>For example: <pre><code>
 *  {@literal @}PaperParcel
 *   class User {
 *     public static final {@literal Parcelable.Creator<User>} CREATOR = PaperParcelUser.CREATOR;
 *
 *     long id;
 *     String firstName;
 *     String lastName;
 *
 *    {@literal @}Override
 *     public void writeToParcel(Parcel dest, int flags) {
 *       PaperParcelUser.writeToParcel(this, dest, flags);
 *     }
 *
 *    {@literal @}Override
 *     public int describeContents() {
 *       return 0;
 *     }
 *   }
 * </code></pre></p>
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface PaperParcel {
  /**
   * <p>Options for configuring how the {@code PaperParcelProcessor} should handle parsing the
   * annotated class.</p>
   *
   * <p>This annotation can be applied directly to an {@code PaperParcel}-annotated class like so:
   * <pre><code>
   * {@literal @}PaperParcel.Options(...)
   * {@literal @}PaperParcel
   *  public final class User implements Parcelable {
   *    String username;
   *    String password;
   *    // ...
   *  }
   * </code></pre></p>
   *
   * <p>Defining {@code Options} in this manner can become tedious if you want to apply the same
   * options to many (or all) of your model objects. For a more reusable strategy, you may wish to
   * create a custom annotation which will define all of the rules you wish to apply; then use your
   * custom annotation on your {@code PaperParcel} classes instead. Here's an example of a custom
   * annotation that has {@code Options} applied to it:
   * <pre><code>
   * {@literal @}PaperParcel.Options(...)
   * {@literal @}Retention(RetentionPolicy.SOURCE)
   * {@literal @}Target(ElementType.TYPE)
   *  public {@literal @}interface MyOptions {
   *  }
   * </code></pre></p>
   *
   * <p>Now {@code MyOptions} could be applied to any {@code PaperParcel} class to apply the
   * rules associated with it:
   * <pre><code>
   * {@literal @}MyOptions
   * {@literal @}PaperParcel
   *  public final class User implements Parcelable {
   *    String username;
   *    String password;
   *    // ...
   *  }
   * </code></pre></p>
   */
  @Documented
  @Retention(SOURCE)
  @Target({ ANNOTATION_TYPE, TYPE })
  @interface Options {
    /**
     * Configures PaperParcel to exclude any field in the annotated class that is annotated with
     * any of the given annotations.
     */
    Class<? extends Annotation>[] excludeAnnotations() default {};

    /**
     * <p>Configures PaperParcel to only include fields in the annotated class that are annotated with
     * any of the given annotations.</p>
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
     * <p>Configures PaperParcel to exclude any field in the annotated class that has specific
     * modifiers or combinations of modifiers. The int values returned by this method must be
     * {@link Modifier} constants.</p>
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
}
