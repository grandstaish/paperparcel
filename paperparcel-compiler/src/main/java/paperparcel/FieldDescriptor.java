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

  /** The normalized type of the field. Primitive types will always be boxed. */
  abstract Equivalence.Wrapper<TypeMirror> normalizedType();

  static final class Factory {
    private final Types types;

    Factory(
        Types types) {
      this.types = types;
    }

    FieldDescriptor create(VariableElement element) {
      String name = element.getSimpleName().toString();
      TypeMirror type = element.asType();
      TypeMirror normalizedType = Utils.normalize(types, type);
      Equivalence.Wrapper<TypeMirror> wrappedType = MoreTypes.equivalence().wrap(type);
      Equivalence.Wrapper<TypeMirror> wrappedNormalizedType =
          MoreTypes.equivalence().wrap(normalizedType);
      return new AutoValue_FieldDescriptor(element, name, wrappedType, wrappedNormalizedType);
    }
  }
}
