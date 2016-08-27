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
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

import static com.google.common.base.Preconditions.checkNotNull;

/** A validator for all of the {@link FieldDescriptor}s in a {@link PaperParcelDescriptor} */
final class FieldsValidator {
  private final Elements elements;
  private final Types types;
  private final AdapterRegistry adapterRegistry;

  FieldsValidator(
      Elements elements,
      Types types,
      AdapterRegistry adapterRegistry) {
    this.elements = elements;
    this.types = types;
    this.adapterRegistry = adapterRegistry;
  }

  ValidationReport<TypeElement> validate(PaperParcelDescriptor model) {
    ValidationReport.Builder<TypeElement> builder = ValidationReport.about(model.element());
    for (FieldDescriptor field : model.fields()) {
      builder.addSubreport(validateField(model, field));
    }
    return builder.build();
  }

  private ValidationReport<VariableElement> validateField(
      PaperParcelDescriptor owner, FieldDescriptor field) {
    ValidationReport.Builder<VariableElement> builder = ValidationReport.about(field.element());
    TypeMirror normalizedType = field.normalizedType().get();
    ensureGenericFieldsAreNotRaw(field, owner, builder);
    ensureAdaptersExistForType(normalizedType, builder);
    ensureFieldIsAccessible(field, owner, builder);
    ensureFieldIsWritable(field, owner, builder);
    return builder.build();
  }

  /** Add an error to {@code builder} if field is a generic type missing its type arguments */
  private void ensureGenericFieldsAreNotRaw(
      final FieldDescriptor field,
      final PaperParcelDescriptor model,
      final ValidationReport.Builder<VariableElement> builder) {
    TypeMirror type = field.normalizedType().get();
    checkNotNull(type);
    type.accept(new SimpleTypeVisitor6<Void, Void>() {
      @Override public Void visitDeclared(DeclaredType t, Void p) {
        int expected = MoreElements.asType(t.asElement()).getTypeParameters().size();
        int actual = t.getTypeArguments().size();
        if (expected != actual) {
          builder.addError(String.format(ErrorMessages.RAW_FIELD,
              model.element().getQualifiedName(),
              field.name()));
        }
        return null;
      }
    }, null);
  }

  /**
   * Appends an error message to the {@link ValidationReport.Builder} if the given field is
   * private and has no accessor method.
   */
  private void ensureFieldIsAccessible(
      FieldDescriptor descriptor,
      PaperParcelDescriptor model,
      ValidationReport.Builder<VariableElement> builder) {
    if (descriptor.isPrivate() && !descriptor.accessorMethod().isPresent()) {
      builder.addError(String.format(ErrorMessages.FIELD_NOT_ACCESSIBLE,
          model.element().getQualifiedName(),
          descriptor.name(),
          FieldDescriptor.possibleGetterNames(descriptor.name())));
    }
  }

  /**
   * Appends an error message to the {@link ValidationReport.Builder} if the given field is
   * private, has no setter method, and has no corresponding constructor argument.
   */
  private void ensureFieldIsWritable(
      FieldDescriptor field,
      PaperParcelDescriptor model,
      ValidationReport.Builder<VariableElement> builder) {
    if (field.isPrivate()
        && !field.setterMethod().isPresent()
        && !model.constructorArgumentNames().contains(field.name())) {
      builder.addError(String.format(ErrorMessages.FIELD_NOT_WRITABLE,
          model.element().getQualifiedName(),
          field.name(),
          FieldDescriptor.possibleSetterNames(field.name())));
    }
  }

  /**
   * Finds all referenced types of a field and checks the {@link AdapterRegistry} to ensure
   * that type can be handled by PaperParcel. Adds an error to {@code builder} for every missing
   * type.
   */
  private void ensureAdaptersExistForType(
      TypeMirror type, ValidationReport.Builder<VariableElement> builder) {

    type.accept(new SimpleTypeVisitor6<Void, ValidationReport.Builder<VariableElement>>() {

      @Override public Void visitWildcard(
          WildcardType t, ValidationReport.Builder<VariableElement> builder) {
        if (t.getExtendsBound() != null) {
          t.getExtendsBound().accept(this, builder);
        }
        if (t.getSuperBound() != null) {
          t.getSuperBound().accept(this, builder);
        }
        return null;
      }

      @Override public Void visitDeclared(
          DeclaredType t, ValidationReport.Builder<VariableElement> builder) {
        for (TypeMirror arg : t.getTypeArguments()) {
          arg.accept(this, builder);
        }
        return super.visitDeclared(t, builder);
      }

      @Override protected Void defaultAction(
          TypeMirror t, ValidationReport.Builder<VariableElement> builder) {
        TypeMirror erased = Utils.getParcelableType(elements, types, t);
        if (!adapterRegistry.getAdapter(TypeName.get(erased)).isPresent()) {
          builder.addError(
              String.format(
                  ErrorMessages.MISSING_TYPE_ADAPTER, t.toString(), ErrorMessages.WIKI_URL));
        }
        return null;
      }
    }, builder);
  }
}
