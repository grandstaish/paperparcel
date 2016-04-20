/*
 * Copyright (C) 2009 Google Inc.
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

package nz.bradcampbell.paperparcel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * An annotation for matching fields via {@link #name()}, {@link #declaringClass()},
 * {@link #type()}, and {@link #annotation()}.
 *
 * @see ExcludeFields
 */
@Documented @Retention(SOURCE)
public @interface FieldMatcher {

  /** A string that matches any variable name */
  String ANY_NAME = "";

  /** A Class that matches any class */
  class AnyClass {}

  /** An annotation that matches any annotation, or no annotations */
  @interface AnyAnnotation {}

  /** Matches only fields whose name is this name. Defaults to match all fields */
  String name() default ANY_NAME;

  /** Matches only fields whose declaring class is this type. Defaults to match all fields */
  Class<?> declaringClass() default AnyClass.class;

  /** Matches only fields of this type. Defaults to match all fields. */
  Class<?> type() default AnyClass.class;

  /** Matches only fields annotated with this annotation. Defaults to match all fields. */
  Class<? extends Annotation> annotation() default AnyAnnotation.class;
}
