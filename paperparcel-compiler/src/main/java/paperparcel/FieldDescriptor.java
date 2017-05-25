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

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import com.google.auto.value.AutoValue;
import com.google.common.base.Equivalence;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static paperparcel.JvmAnnotationNames.JAVAX_NONNULL_ANNOTATION;
import static paperparcel.JvmAnnotationNames.NONNULL_BY_DEFAULT_ANNOTATIONS;
import static paperparcel.JvmAnnotationNames.NOT_NULL_ANNOTATIONS;
import static paperparcel.JvmAnnotationNames.NULLABLE_ANNOTATIONS;
import static paperparcel.JvmAnnotationNames.NULLABLE_BY_DEFAULT_ANNOTATIONS;

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
    private final Types types;

    Factory(Types types) {
      this.types = types;
    }

    FieldDescriptor create(TypeElement owner, VariableElement element) {
      String name = element.getSimpleName().toString();
      TypeMirror type = types.asMemberOf((DeclaredType) owner.asType(), element);
      TypeMirror fieldType = Utils.replaceTypeVariablesWithUpperBounds(types, type);
      Equivalence.Wrapper<TypeMirror> wrappedType = MoreTypes.equivalence().wrap(fieldType);
      boolean isVisible = Visibility.ofElement(element) != Visibility.PRIVATE;
      boolean isNullable = isNullable(element);
      return new AutoValue_FieldDescriptor(element, name, wrappedType, isVisible, isNullable);
    }

    private boolean isNullable(Element element) {
      if (isNonNullByDefault(element)) {
        for (String name : NULLABLE_ANNOTATIONS) {
          if (Utils.getAnnotationWithName(element, name) != null) {
            return true;
          }
        }
        return false;
      } else {
        for (String name : NOT_NULL_ANNOTATIONS) {
          if (Utils.getAnnotationWithName(element, name) != null) {
            return false;
          }
          AnnotationMirror javaxNonnull = Utils.getAnnotationWithName(element,
              JAVAX_NONNULL_ANNOTATION);
          if (javaxNonnull != null) {
            AnnotationValue when = AnnotationMirrors.getAnnotationValue(javaxNonnull, "when");
            return !when.getValue().toString().equals("ALWAYS");
          }
        }
        return true;
      }
    }

    private boolean isNonNullByDefault(Element element) {
      while ((element = element.getEnclosingElement()) != null) {
        for (String name : NONNULL_BY_DEFAULT_ANNOTATIONS) {
          if (Utils.getAnnotationWithName(element, name) != null) {
            return true;
          }
        }
        for (String name : NULLABLE_BY_DEFAULT_ANNOTATIONS) {
          if (Utils.getAnnotationWithName(element, name) != null) {
            return false;
          }
        }
      }
      return false;
    }
  }
}
