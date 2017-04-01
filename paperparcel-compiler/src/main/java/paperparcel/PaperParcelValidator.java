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

import com.google.auto.common.Visibility;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
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

  ValidationReport<TypeElement> validate(TypeElement element, OptionsDescriptor options) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(element);

    if (element.getKind() != ElementKind.CLASS) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_CLASS);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_ABSTRACT_CLASS);
    }
    if (!Utils.isParcelable(elements, types, element.asType())) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_PARCELABLE);
    }
    if (implementsAnnotation(elements, types, element)) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_ANNOTATION);
    }
    ElementKind enclosingKind = element.getEnclosingElement().getKind();
    if (enclosingKind.isClass() || enclosingKind.isInterface()) {
      if (Visibility.ofElement(element) == Visibility.PRIVATE) {
        builder.addError(ErrorMessages.PAPERPARCEL_ON_PRIVATE_CLASS);
      }
      if (!element.getModifiers().contains(Modifier.STATIC)) {
        builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_STATIC_INNER_CLASS);
      }
    }

    if (options.excludeNonExposedFields()
        && options.exposeAnnotationNames().isEmpty()) {
      builder.addError(ErrorMessages.NO_EXPOSE_ANNOTATIONS_DEFINED, element, options.mirror());
    }

    if (!Utils.isSingleton(types, element)) {
      ImmutableList<ExecutableElement> constructors =
          Utils.orderedConstructorsIn(element, options.reflectAnnotations());
      if (constructors.size() == 0) {
        builder.addError(ErrorMessages.PAPERPARCEL_NO_VISIBLE_CONSTRUCTOR);
      }

      ImmutableList<VariableElement> fields = Utils.getFieldsToParcel(element, options);
      for (VariableElement field : fields) {
        if (Utils.containsWildcards(field.asType())) {
          builder.addError(ErrorMessages.WILDCARD_IN_FIELD_TYPE, field);
        } else if (Utils.isRawType(field.asType())) {
          builder.addError(ErrorMessages.FIELD_MISSING_TYPE_ARGUMENTS, field);
        } else if (Utils.hasRecursiveTypeParameter(field.asType())) {
          builder.addError(ErrorMessages.FIELD_TYPE_IS_RECURSIVE, field);
        } else if (Utils.containsIntersection(field.asType())) {
          builder.addError(ErrorMessages.FIELD_TYPE_IS_INTERSECTION_TYPE, field);
        }
      }
    }

    return builder.build();
  }

  private boolean implementsAnnotation(Elements elements, Types types, TypeElement type) {
    TypeMirror annotationType = elements.getTypeElement(Annotation.class.getName()).asType();
    return types.isAssignable(type.asType(), annotationType);
  }
}
