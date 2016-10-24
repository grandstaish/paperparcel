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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotates {@code Parcelable} classes to automatically generate the {@code Parcelable.Creator}
 * and {@code writeToParcel} boilerplate code that is required as a part of the {@code Parcelable}
 * contract. The generated code will be contained in a class with the name of the type annotated
 * with {@code @PaperParcel} prepended with {@code PaperParcel}. For example,
 * {@code @PaperParcel class MyModel {...}} will produce a class named {@code PaperParcelMyModel}.
 * {@code PaperParcelMyModel} will have a static field named {@code CREATOR} which can be added to
 * your model class. Finally, {@code PaperParcelMyModel} will include a static method named
 * {@code writeToParcel} which you can call from {@code MyModel#writeToParcel}.
 *
 * For example: <pre><code>
 *   {@literal @}PaperParcel
 *   class User {
 *     public static final {@literal Parcelable.Creator<User>} CREATOR = PaperParcelUser.CREATOR;
 *
 *     long id;
 *     String firstName;
 *     String lastName;
 *
 *     {@literal @}Override
 *     public void writeToParcel(Parcel dest, int flags) {
 *       PaperParcelUser.writeToParcel(this, dest, flags);
 *     }
 *
 *     {@literal @}Override
 *     public int describeContents() {
 *       return 0;
 *     }
 *   }
 * </code></pre>
 *
 * <p>This annotation must be applied to a top-level class. It can't be applied to a base class,
 * an interface, or an abstract class.
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface PaperParcel {
  /**
   * Configures PaperParcel to exclude any field in the annotated class that is annotated with any
   * of the given annotations.
   */
  Class<? extends Annotation>[] excludeFieldsWithAnnotations() default {};

  /**
   * Configures PaperParcel to exclude any field in the annotated class that has specific modifiers
   * or combinations of modifiers. The int values returned by this method must be {@link Modifier}
   * constants.
   *
   * <p>By default any {@code transient} or {@code static} field is excluded.
   */
  int[] excludeFieldsWithModifiers() default { Modifier.TRANSIENT, Modifier.STATIC };

  /**
   * Configures PaperParcel to exclude any field in the annotated class that is not annotated with
   * {@link Pack}.
   *
   * <p>By default this is false.
   */
  boolean excludeFieldsWithoutPackAnnotation() default false;
}
