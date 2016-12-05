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

import com.google.auto.common.MoreElements;
import com.google.auto.common.Visibility;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
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

    if (element.getKind() != ElementKind.CLASS) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_CLASS);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_ABSTRACT_CLASS);
    }
    if (!Utils.isParcelable(elements, types, element.asType())) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_PARCELABLE);
    }
    if (ancestorIsPaperParcel(types, element)) {
      builder.addError(ErrorMessages.PAPERPARCEL_EXTENDS_PAPERPARCEL);
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

    Options options = Utils.getOptions(element);
    if (options.excludeNonExposedFields()
        && options.exposeAnnotationNames().isEmpty()) {
      builder.addError(ErrorMessages.OPTIONS_NO_EXPOSE_ANNOTATIONS, element, options.mirror());
    }

    if (!Utils.isSingleton(types, element)) {
      ImmutableList<ExecutableElement> constructors =
          Utils.orderedConstructorsIn(element, options.reflectAnnotations());
      if (constructors.size() == 0) {
        builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
      }

      ImmutableList<VariableElement> fields = Utils.getFieldsToParcel(types, element, options);
      for (VariableElement field : fields) {
        if (Utils.containsWildcards(field.asType())) {
          builder.addError(ErrorMessages.WILDCARD_IN_FIELD_TYPE, field);
        } else if (isRawType(field.asType())) {
          builder.addError(ErrorMessages.FIELD_MISSING_TYPE_ARGUMENTS, field);
        }
      }
    }

    return builder.build();
  }

  /** Returns true if {@code typeMirror} is a raw type. */
  private boolean isRawType(TypeMirror typeMirror) {
    return typeMirror.accept(new SimpleTypeVisitor6<Boolean, Void>(false) {
      @Override public Boolean visitDeclared(DeclaredType t, Void p) {
        int expected = MoreElements.asType(t.asElement()).getTypeParameters().size();
        int actual = t.getTypeArguments().size();
        boolean raw = expected != actual;
        if (!raw) {
          for (int i = 0; i < t.getTypeArguments().size(); i++) {
            raw = t.getTypeArguments().get(i).accept(this, p);
            if (raw) break;
          }
        }
        return raw;
      }

      @Override public Boolean visitArray(ArrayType t, Void p) {
        return t.getComponentType().accept(this, p);
      }

      @Override public Boolean visitTypeVariable(TypeVariable t, Void p) {
        TypeParameterElement element = (TypeParameterElement) t.asElement();
        for (TypeMirror bound : element.getBounds()) {
          if (bound.accept(this, p)) return true;
        }
        return false;
      }
    }, null);
  }

  private boolean implementsAnnotation(Elements elements, Types types, TypeElement type) {
    TypeMirror annotationType = elements.getTypeElement(Annotation.class.getName()).asType();
    return types.isAssignable(type.asType(), annotationType);
  }

  private boolean ancestorIsPaperParcel(Types types, TypeElement type) {
    while (true) {
      TypeMirror parentMirror = type.getSuperclass();
      if (parentMirror.getKind() == TypeKind.NONE) {
        return false;
      }
      TypeElement parentElement = (TypeElement) types.asElement(parentMirror);
      if (MoreElements.isAnnotationPresent(parentElement, PaperParcel.class)) {
        return true;
      }
      type = parentElement;
    }
  }
}
