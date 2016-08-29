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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import org.jetbrains.annotations.Nullable;

/** Represents a {@link PaperParcel} annotated object */
@AutoValue
abstract class PaperParcelDescriptor {

  /** The original {@link TypeElement} that this class is describing */
  abstract TypeElement element();

  abstract ImmutableList<FieldDescriptor> fields();

  @Nullable abstract WriteInfo writeInfo();

  @Nullable abstract ReadInfo readInfo();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  static final class Factory {
    private final Types types;

    Factory(
        Types types) {
      this.types = types;
    }

    PaperParcelDescriptor create(TypeElement element, WriteInfo writeInfo, ReadInfo readInfo) {
      ImmutableList.Builder<FieldDescriptor> fields = ImmutableList.builder();
      if (readInfo != null) {
        fields.addAll(readInfo.readableFields());
        fields.addAll(readInfo.getterMethodMap().keySet());
      }
      boolean singleton = Utils.isSingleton(types, element);
      return new AutoValue_PaperParcelDescriptor(
          element, fields.build(), writeInfo, readInfo, singleton);
    }
  }
}
