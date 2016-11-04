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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;

/** A representation of {@link PaperParcel.Options} */
@AutoValue
abstract class Options {
  static final Options DEFAULT = create(
      ImmutableList.<Set<Modifier>>of(ImmutableSet.of(STATIC), ImmutableSet.of(TRANSIENT)),
      ImmutableList.<String>of(),
      ImmutableList.<String>of(),
      false);

  abstract List<Set<Modifier>> excludeModifiers();

  abstract List<String> excludeAnnotationNames();

  abstract List<String> exposeAnnotationNames();

  abstract boolean excludeNonExposedFields();

  static Options create(
      List<Set<Modifier>> excludeModifiers,
      List<String> excludeAnnotationNames,
      List<String> exposeAnnotationNames,
      boolean excludeNonExposedFields) {
    return new AutoValue_Options(
        excludeModifiers,
        excludeAnnotationNames,
        exposeAnnotationNames,
        excludeNonExposedFields);
  }
}
