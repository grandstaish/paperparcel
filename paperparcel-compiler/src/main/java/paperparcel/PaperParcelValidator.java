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
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** A validator for any {@link PaperParcel} annotated {@link TypeElement} */
final class PaperParcelValidator {
  private final Elements elements;
  private final Types types;

  PaperParcelValidator(
      Elements elements,
      Types types) {
    this.elements = elements;
    this.types = types;
  }

  ValidationReport<TypeElement> validate(TypeElement element) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(element);
    if (Utils.getTypeArguments(element.asType()).size() > 0) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_GENERIC_CLASS);
    }
    if (element.getKind() == ElementKind.INTERFACE) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_INTERFACE);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_ABSTRACT_CLASS);
    }
    if (!Utils.isParcelable(elements, types, element.asType())) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_PARCELABLE);
    }
    Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
    if (mainConstructor.isPresent()) {
      builder.addSubreport(validateConstructor(element, mainConstructor.get()));
    } else if (!Utils.isSingleton(types, element)) {
      builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
    }
    return builder.build();
  }

  /** Validate all of the constructor arguments have a corresponding field */
  private ValidationReport<ExecutableElement> validateConstructor(
      TypeElement element, ExecutableElement constructor) {
    ValidationReport.Builder<ExecutableElement> report = ValidationReport.about(constructor);
    ImmutableList<VariableElement> fields = Utils.getLocalAndInheritedFields(types, element);
    for (VariableElement parameter : constructor.getParameters()) {
      String name = parameter.getSimpleName().toString();
      Optional<VariableElement> field = findFieldWithName(fields, name);
      if (!field.isPresent() || !matchesType(field.get(), parameter)) {
        ValidationReport.Builder<VariableElement> subReport =
            ValidationReport.about(parameter);
        subReport.addError(String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
            name, element.getQualifiedName()));
        report.addSubreport(subReport.build());
      }
    }
    return report.build();
  }

  private boolean matchesType(VariableElement a, VariableElement b) {
    return MoreTypes.equivalence().equivalent(a.asType(), b.asType());
  }

  private Optional<VariableElement> findFieldWithName(
      ImmutableList<VariableElement> fields, String name) {
    for (VariableElement field : fields) {
      if (Objects.equal(field.getSimpleName().toString(), name)) {
        return Optional.of(field);
      }
    }
    return Optional.absent();
  }
}
