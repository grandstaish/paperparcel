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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should be exposed for parcelling.
 *
 * <p>This annotation has no effect unless you apply an {@link PaperParcel.Options} annotation with
 * {@link PaperParcel.Options#excludeFieldsWithoutPackAnnotation()} set to {@code true} to the
 * field's containing class.</p>
 *
 * <p>Here is an example of how {@literal @}Pack can be used:
 * <p><pre>
 * &#64PaperParcel.Options(excludeFieldsWithoutPackAnnotation = true)
 * &#64PaperParcel
 * public final class User implements Parcelable {
 *   &#Pack String username;
 *   String password;
 *   // ...
 * }
 * </pre></p>
 * In this example, the generated {@code CREATOR} and {@code writeToParcel(...)} implementations
 * will not include code for parcelling the {@code password} field. They will only include the
 * {@code username} field because it is annotated with {@code Pack}.
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
