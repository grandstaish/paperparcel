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
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

/** A validator for any {@link PaperParcel} annotated {@link TypeElement} */
final class PaperParcelValidator {
  private final Elements elements;
  private final Types types;
  private final WriteInfo.Factory writeInfoFactory;
  private final ReadInfo.Factory readInfoFactory;
  private final Adapter.Factory adapterFactory;

  PaperParcelValidator(
      Elements elements,
      Types types,
      WriteInfo.Factory writeInfoFactory,
      ReadInfo.Factory readInfoFactory,
      Adapter.Factory adapterFactory) {
    this.elements = elements;
    this.types = types;
    this.writeInfoFactory = writeInfoFactory;
    this.readInfoFactory = readInfoFactory;
    this.adapterFactory = adapterFactory;
  }

  @AutoValue
  static abstract class PaperParcelValidation {
    abstract ValidationReport<TypeElement> report();
    @Nullable abstract WriteInfo writeInfo();
    @Nullable abstract ReadInfo readInfo();

    private static PaperParcelValidation create(
        ValidationReport<TypeElement> report, WriteInfo writeInfo, ReadInfo readInfo) {
      return new AutoValue_PaperParcelValidator_PaperParcelValidation(report, writeInfo, readInfo);
    }
  }

  PaperParcelValidation validate(TypeElement element) {
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
    WriteInfo writeInfo = null;
    ReadInfo readInfo = null;
    if (!Utils.isSingleton(types, element)) {
      ImmutableList<VariableElement> fields = Utils.getFieldsToParcel(types, element, options);
      ImmutableList<ExecutableElement> methods =
          Utils.getLocalAndInheritedMethods(elements, types, element);
      ImmutableList<ExecutableElement> constructors =
          Utils.orderedConstructorsIn(element, options.reflectAnnotations());
      if (constructors.size() == 0) {
        builder.addError(ErrorMessages.NO_VISIBLE_CONSTRUCTOR);
      }
      try {
        writeInfo = writeInfoFactory.create(
            fields, methods, constructors, options.reflectAnnotations());
      } catch (WriteInfo.NonWritableFieldsException e) {
        addErrorsForNonWritableFields(e, builder);
      }
      try {
        readInfo = readInfoFactory.create(fields, methods, options.reflectAnnotations());
      } catch (ReadInfo.NonReadableFieldsException e) {
        addErrorsForNonReadableFields(e, builder);
      }
      for (VariableElement field : fields) {
        ensureGenericFieldsAreNotRaw(field, builder);
        ensureAdaptersExistForField(field, builder);
      }
    }
    return PaperParcelValidation.create(builder.build(), writeInfo, readInfo);
  }

  private void addErrorsForNonWritableFields(
      WriteInfo.NonWritableFieldsException e, ValidationReport.Builder<TypeElement> builder) {
    ImmutableSet<ExecutableElement> validConstructors = e.allNonWritableFieldsMap().keySet();
    ImmutableSet<ExecutableElement> invalidConstructors =
        e.unassignableConstructorParameterMap().keySet();
    if (validConstructors.size() > 0) {
      // Log errors for each non-writable field in each valid constructor
      for (ExecutableElement validConstructor : validConstructors) {
        ImmutableList<VariableElement> nonWritableFields =
            e.allNonWritableFieldsMap().get(validConstructor);
        for (VariableElement nonWritableField : nonWritableFields) {
          String fieldName = nonWritableField.getSimpleName().toString();
          builder.addError(String.format(ErrorMessages.FIELD_NOT_WRITABLE,
              builder.getSubject().getQualifiedName(),
              fieldName,
              validConstructor.toString(),
              ErrorMessages.SITE_URL),
              nonWritableField);
        }
      }
    } else {
      // Log errors for unassignable parameters in each invalid constructor
      for (ExecutableElement invalidConstructor : invalidConstructors) {
        ValidationReport.Builder<ExecutableElement> constructorValidationReport =
            ValidationReport.about(invalidConstructor);
        ImmutableList<VariableElement> unassignableFields =
            e.unassignableConstructorParameterMap().get(invalidConstructor);
        for (VariableElement unassignableField : unassignableFields) {
          String fieldName = unassignableField.getSimpleName().toString();
          constructorValidationReport.addError(
              String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
                  fieldName, builder.getSubject().getQualifiedName()), invalidConstructor);
        }
        builder.addSubreport(constructorValidationReport.build());
      }
    }
  }

  private void addErrorsForNonReadableFields(
      ReadInfo.NonReadableFieldsException e, ValidationReport.Builder<TypeElement> builder) {
    for (VariableElement nonReadableField : e.nonReadableFields()) {
      String fieldName = nonReadableField.getSimpleName().toString();
      builder.addError(String.format(ErrorMessages.FIELD_NOT_ACCESSIBLE,
          builder.getSubject().getQualifiedName(),
          fieldName,
          ErrorMessages.SITE_URL),
          nonReadableField);
    }
  }

  /** Add an error to {@code builder} if field is a generic type missing its type arguments */
  private void ensureGenericFieldsAreNotRaw(
      final VariableElement field,
      final ValidationReport.Builder<TypeElement> builder) {
    field.asType().accept(new SimpleTypeVisitor6<Void, Void>() {
      @Override public Void visitDeclared(DeclaredType t, Void p) {
        int expected = MoreElements.asType(t.asElement()).getTypeParameters().size();
        int actual = t.getTypeArguments().size();
        if (expected != actual) {
          builder.addError(String.format(ErrorMessages.RAW_FIELD,
              builder.getSubject().getQualifiedName(),
              field.getSimpleName()),
              field);
        }
        return null;
      }
    }, null);
  }

  private void ensureAdaptersExistForField(
      VariableElement field, ValidationReport.Builder<TypeElement> builder) {
    TypeMirror fieldType = Utils.eraseTypeVariables(types, field.asType());
    if (!fieldType.getKind().isPrimitive() && adapterFactory.create(fieldType) == null) {
      builder.addError(
          String.format(ErrorMessages.MISSING_TYPE_ADAPTER,
              fieldType.toString(),
              ErrorMessages.SITE_URL),
          field);
    }
  }
}
