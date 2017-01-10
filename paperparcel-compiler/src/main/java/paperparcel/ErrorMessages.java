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

/** The collection of error messages to be reported back to users. */
final class ErrorMessages {
  /* @Adapter errors */
  static final String ADAPTER_MUST_IMPLEMENT_TYPE_ADAPTER_INTERFACE =
      "%s must implement paperparcel.TypeAdapter.";
  static final String ADAPTER_MUST_BE_CLASS =
      "Type adapters can only be classes.";
  static final String ADAPTER_IS_ABSTRACT =
      "Type adapters cannot be abstract.";
  static final String ADAPTER_INVALID_CONSTRUCTOR =
      "Type adapter constructors can only have 'paperparcel.TypeAdapter' or 'java.lang.Class' parameters.";
  static final String ADAPTER_TYPE_ARGUMENT_HAS_RAW_TYPE =
      "Type adapter argument must not contain raw types.";
  static final String ADAPTER_TYPE_ARGUMENT_HAS_WILDCARDS =
      "Type adapter argument must not contain wildcards.";
  static final String CONSTRUCTOR_PARAMETER_HAS_RAW_TYPE =
      "Type adapter constructor parameter must not contain raw types.";
  static final String CONSTRUCTOR_PARAMETER_HAS_WILDCARD =
      "Type adapter constructor parameter must not contain wildcards.";
  static final String ADAPTER_INCOMPATIBLE_TYPE_PARAMETERS =
      "Type adapter defined with incompatible type parameters.";
  static final String ADAPTER_MUST_BE_PUBLIC =
      "Type adapter classes must be public.";
  static final String ADAPTER_VISIBILITY_RESTRICTED =
      "Type adapter classes cannot be enclosed in non-public types.";
  static final String NESTED_ADAPTER_MUST_BE_STATIC =
      "Nested type adapters must be static.";
  static final String ADAPTER_MUST_HAVE_PUBLIC_CONSTRUCTOR =
      "Type adapter classes must have a public constructor.";

  /* @ProcessorConfig errors */
  static final String MULTIPLE_PROCESSOR_CONFIGS =
      "Multiple @ProcessorConfig annotations found within a single module.";
  static final String NO_EXPOSE_ANNOTATIONS_DEFINED =
      "No expose annotations returned from exposeAnnotations().";

  /* @PaperParcel errors */
  static final String PAPERPARCEL_ON_NON_CLASS =
      "@PaperParcel only applies to classes.";
  static final String PAPERPARCEL_ON_ABSTRACT_CLASS =
      "@PaperParcel cannot be applied to an abstract class.";
  static final String PAPERPARCEL_ON_NON_PARCELABLE =
      "@PaperParcel can only be applied to classes that implement android.os.Parcelable.";
  static final String UNMATCHED_CONSTRUCTOR_PARAMETER =
      "No field match found for the constructor parameter \"%1$s\" in %2$s. Constructor "
          + "arguments are matched with fields via their name and type.";
  static final String WILDCARD_IN_FIELD_TYPE =
      "Wildcard field types are not supported.";
  static final String PAPERPARCEL_ON_ANNOTATION =
      "@PaperParcel may not be used to implement an annotation interface.";
  static final String PAPERPARCEL_ON_PRIVATE_CLASS =
      "@PaperParcel class must not be private.";
  static final String PAPERPARCEL_ON_NON_STATIC_INNER_CLASS =
      "Nested @PaperParcel class must be static.";
  static final String PAPERPARCEL_NO_VISIBLE_CONSTRUCTOR =
      "No visible constructor found.";

  /* FieldDescriptor errors */
  static final String FIELD_NOT_READABLE =
      "Field %1$s.%2$s is private and PaperParcel cannot find an accessor method for it. "
          + "PaperParcel will search for accessor methods using the conventions defined at "
          + "http://grandstaish.github.io/paperparcel/#model-conventions\n\nIf this field should "
          + "be excluded by PaperParcel then you can do this by:\n%3$s\nNote: exclude rules can "
          + "be customized using the @ProcessorConfig API.";
  static final String FIELD_NOT_WRITABLE =
      "Field %1$s.%2$s is private and PaperParcel cannot find a constructor parameter or setter "
          + "method for it (using constructor %3$s). PaperParcel will search for setter methods "
          + "and constructor parameters using the conventions defined at "
          + "http://grandstaish.github.io/paperparcel/#model-conventions\n\nIf this field should "
          + "be excluded by PaperParcel then you can do this by:\n%4$s\nNote: exclude rules can "
          + "be customized using the @ProcessorConfig API.";
  static final String FIELD_MISSING_TYPE_ADAPTER =
      "Unknown type %1$s. You can define a custom type adapter to handle unknown types.";
  static final String FIELD_MISSING_TYPE_ARGUMENTS =
      "All field type arguments must be specified.";
  static final String FIELD_TYPE_IS_RECURSIVE =
      "PaperParcel does not support recursive generic field types.";
  static final String FIELD_TYPE_IS_INTERSECTION_TYPE =
      "PaperParcel does not support intersection field types.";

  /* kapt errors */
  static final String KAPT1_INCOMPATIBLE =
      "PaperParcel is not compatible with legacy kapt. Please upgrade to kotlin 1.0.5 (or greater) "
          + "and apply the 'kotlin-kapt' gradle plugin.";
  static final String KAPT2_UNSTABLE_WARNING =
      "kapt2 has been replaced with a newer version in kotlin 1.0.6 that is a lot more stable. It "
          + "is highly recommended that you upgrade.";
  static final String KAPT2_KT_13804 =
      "PaperParcel is not compatible kotlin 1.0.4. Please upgrade to kotlin 1.0.5 (or greater)";
  static final String KAPT2_INVALID_FIELD_NAME =
      "Due to a bug in kapt, '%s' is not an allowable variable name. This bug is resolved in "
          + "kotlin version 1.0.6. Please upgrade your kotlin version, or change this variable name.";

  /* AutoValue Extension errors */
  static final String MANUAL_IMPLEMENTATION_OF_CREATOR =
      "Manual implementation of a static Parcelable.Creator<T> CREATOR found in %s.";
  static final String MANUAL_IMPLEMENTATION_OF_WRITE_TO_PARCEL =
      "Manual implementation of android.os.Parcelable#writeToParcel(android.os.Parcel, int) "
          + "found in %s.";

  private ErrorMessages() {}
}
