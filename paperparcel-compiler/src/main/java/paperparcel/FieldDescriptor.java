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

import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import com.google.auto.value.AutoValue;
import com.google.common.base.Equivalence;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/** Represents a single field in a {@link PaperParcel} class */
@AutoValue
abstract class FieldDescriptor {

  /** The original {@link VariableElement} that this class is describing */
  abstract VariableElement element();

  /** The simple name of the field */
  abstract String name();

  /** The original type of the field */
  abstract Equivalence.Wrapper<TypeMirror> type();

  /** True if this field is not private */
  abstract boolean isVisible();

  /** True if the field is not annotated with {@code @NonNull} or {@code @NotNull}. */
  abstract boolean isNullable();

  static final class Factory {
    private static final String NON_NULL_ANNOTATION_NAME = "NonNull";
    private static final String NOT_NULL_ANNOTATION_NAME = "NotNull";

    private final Types types;

    Factory(
        Types types) {
      this.types = types;
    }

    FieldDescriptor create(VariableElement element) {
      String name = element.getSimpleName().toString();
      TypeMirror type = element.asType();
      TypeMirror fieldType = Utils.eraseTypeVariables(types, type);
      Equivalence.Wrapper<TypeMirror> wrappedType = MoreTypes.equivalence().wrap(fieldType);
      boolean isVisible = Visibility.ofElement(element) != Visibility.PRIVATE;
      boolean isNullable =
          Utils.getAnnotationWithSimpleName(element, NON_NULL_ANNOTATION_NAME) == null
          && Utils.getAnnotationWithSimpleName(element, NOT_NULL_ANNOTATION_NAME) == null;
      return new AutoValue_FieldDescriptor(element, name, wrappedType, isVisible, isNullable);
    }
  }
}
