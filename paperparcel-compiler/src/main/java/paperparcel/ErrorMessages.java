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
  static final String SITE_URL = "https://github.com/grandstaish/paperparcel";

  /* Shared */
  static final String NO_VISIBLE_CONSTRUCTOR =
      "No visible constructor found";

  /* @RegisterAdapter errors */
  static final String REGISTERADAPTER_ON_NON_TYPE_ADAPTER =
      "@RegisterAdapter must be applied to a class that implements TypeAdapter<T>";
  static final String REGISTERADAPTER_ON_INTERFACE =
      "@RegisterAdapter cannot be applied to an interface";
  static final String REGISTERADAPTER_ON_ABSTRACT_CLASS =
      "@RegisterAdapter cannot be applied to an abstract class";
  static final String INVALID_TYPE_ADAPTER_CONSTRUCTOR =
      "TypeAdapters can only have other TypeAdapters as constructor arguments";
  static final String REGISTERADAPTER_ON_RAW_TYPE_ADAPTER =
      "TypeAdapters cannot be a raw type";
  static final String RAW_TYPE_ADAPTER_IN_CONSTRUCTOR =
      "TypeAdapter arguments can not be raw types";

  /* @PaperParcel errors */
  static final String PAPERPARCEL_ON_GENERIC_CLASS =
      "@PaperParcel cannot be applied to a class with type parameters";
  static final String PAPERPARCEL_ON_INTERFACE =
      "@PaperParcel cannot be applied to an interface";
  static final String PAPERPARCEL_ON_ABSTRACT_CLASS =
      "@PaperParcel cannot be applied to an abstract class";
  static final String PAPERPARCEL_ON_NON_PARCELABLE =
      "@PaperParcel can only be applied to classes that implement android.os.Parcelable.";
  static final String UNMATCHED_CONSTRUCTOR_PARAMETER =
      "No field match found for the constructor parameter \"%1$s\" in %2$s. Constructor "
          + "arguments are matched with fields via their name and type";

  /* Adapter errors */
  static final String INCOMPATIBLE_TYPE_PARAMETERS =
      "TypeAdapter defined with incompatible type parameters.";

  /* FieldDescriptor errors */
  static final String FIELD_NOT_ACCESSIBLE =
      "Field %1$s.%2$s not accessible. See %3$s for more info.";
  static final String FIELD_NOT_WRITABLE =
      "Field %1$s.%2$s not writable when constructing an instance of %1$s using the constructor "
          + "%3$s. See %4$s for more info.";
  static final String MISSING_TYPE_ADAPTER =
      "Unknown type %1$s. Define a TypeAdapter to handle custom types. For more info, "
          + "see %2$s";
  static final String RAW_FIELD =
      "Missing type information on %1$s.%2$s";

  /* AutoValue errors */
  static final String MANUAL_IMPLEMENTATION_OF_CREATOR =
      "Manual implementation of a static Parcelable.Creator<T> CREATOR found in %s.";
  static final String MANUAL_IMPLEMENTATION_OF_WRITE_TO_PARCEL =
      "Manual implementation of android.os.Parcelable#writeToParcel(android.os.Parcel, int) "
          + "found in %s.";
  static final String AUTOVALUE_ON_GENERIC_CLASS =
      "@AutoValue classes that implement android.os.Parcelable cannot have type parameters.";

  private ErrorMessages() {}
}
