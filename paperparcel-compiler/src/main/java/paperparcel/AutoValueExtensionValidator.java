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

import android.support.annotation.Nullable;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

final class AutoValueExtensionValidator {
  private final Elements elements;
  private final Types types;

  AutoValueExtensionValidator(
      Elements elements,
      Types types) {
    this.elements = elements;
    this.types = types;
  }

  ValidationReport<TypeElement> validate(TypeElement subject) {
    ValidationReport.Builder<TypeElement> report = ValidationReport.about(subject);
    ExecutableElement writeToParcel = findWriteToParcel(subject);
    if (writeToParcel != null) {
      report.addError(String.format(
          ErrorMessages.MANUAL_IMPLEMENTATION_OF_WRITE_TO_PARCEL, subject.getQualifiedName()),
          writeToParcel);
    }
    VariableElement creator = Utils.findCreator(elements, types, subject);
    if (creator != null) {
      report.addError(String.format(
          ErrorMessages.MANUAL_IMPLEMENTATION_OF_CREATOR, subject.getQualifiedName()),
          creator);
    }
    return report.build();
  }

  @Nullable private ExecutableElement findWriteToParcel(TypeElement subject) {
    TypeMirror parcel = elements.getTypeElement("android.os.Parcel").asType();
    @SuppressWarnings("deprecation") // Support for kapt2
    ImmutableSet<ExecutableElement> methods =
        MoreElements.getLocalAndInheritedMethods(subject, elements);
    for (ExecutableElement element : methods) {
      if (element.getSimpleName().contentEquals("writeToParcel")
          && MoreTypes.isTypeOf(void.class, element.getReturnType())
          && !element.getModifiers().contains(Modifier.ABSTRACT)) {
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.size() == 2
            && types.isSameType(parcel, parameters.get(0).asType())
            && MoreTypes.isTypeOf(int.class, parameters.get(1).asType())) {
          return element;
        }
      }
    }
    return null;
  }
}
