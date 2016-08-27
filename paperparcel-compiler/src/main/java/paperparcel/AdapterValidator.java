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

import javax.lang.model.element.TypeElement;

/** A validator for {@link AdapterDescriptor} instances */
final class AdapterValidator {
  ValidationReport<TypeElement> validate(TypeElement element, AdapterDescriptor descriptor) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(element);
    validateTypeParameters(descriptor, builder);
    return builder.build();
  }

  /** Ensures there is only one {@link AdapterDescriptor.TypeParameter} with no index */
  private void validateTypeParameters(
      AdapterDescriptor descriptor, ValidationReport.Builder<TypeElement> builder) {
    int noIndexCount = 0;
    for (AdapterDescriptor.TypeParameter typeParameter : descriptor.typeParameters()) {
      if (typeParameter.index() == AdapterDescriptor.TypeParameter.NO_INDEX) {
        noIndexCount++;
      }
    }
    if (noIndexCount > 1) {
      builder.addError(ErrorMessages.INCOMPATIBLE_TYPE_PARAMETERS);
    }
  }
}
