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
import com.google.common.collect.ImmutableList;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
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

    if (element.getKind() == ElementKind.INTERFACE) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_INTERFACE);
    }
    if (element.getModifiers().contains(Modifier.ABSTRACT)) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_ABSTRACT_CLASS);
    }
    if (!Utils.isParcelable(elements, types, element.asType())) {
      builder.addError(ErrorMessages.PAPERPARCEL_ON_NON_PARCELABLE);
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
        if (containsWildcards(field.asType())) {
          builder.addError(ErrorMessages.WILDCARD_IN_FIELD_TYPE, field);
        }

        if (isRawType(field.asType())) {
          builder.addError(
              String.format(ErrorMessages.RAW_FIELD,
                  builder.getSubject().getQualifiedName(), field.getSimpleName()),
              field);
        }
      }
    }

    return builder.build();
  }

  /** Returns true if {@code typeMirror} contains any wildcards. */
  private boolean containsWildcards(TypeMirror typeMirror) {
    return typeMirror.accept(new SimpleTypeVisitor6<Boolean, Void>(false) {
      @Override public Boolean visitArray(ArrayType type, Void p) {
        return type.getComponentType().accept(this, p);
      }

      @Override public Boolean visitDeclared(DeclaredType type, Void p) {
        for (TypeMirror arg : type.getTypeArguments()) {
          if (arg.accept(this, p)) return true;
        }
        return false;
      }

      @Override public Boolean visitWildcard(WildcardType type, Void p) {
        return true;
      }
    }, null);
  }

  /** Returns true if {@code typeMirror} is a raw type. */
  private boolean isRawType(TypeMirror typeMirror) {
    return typeMirror.accept(new SimpleTypeVisitor6<Boolean, Void>(false) {
      @Override public Boolean visitDeclared(DeclaredType t, Void p) {
        int expected = MoreElements.asType(t.asElement()).getTypeParameters().size();
        int actual = t.getTypeArguments().size();
        return expected != actual;
      }
    }, null);
  }
}
