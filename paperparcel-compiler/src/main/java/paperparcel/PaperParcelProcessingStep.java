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

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.UnknownTypeException;
import javax.tools.Diagnostic;

/**
 * A {@link BasicAnnotationProcessor.ProcessingStep} that is responsible for dealing with all
 * {@link PaperParcel} annotated objects as part of the {@link PaperParcelProcessor}.
 */
final class PaperParcelProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private final Messager messager;
  private final OptionsHolder optionsHolder;
  private final PaperParcelValidator paperParcelValidator;
  private final PaperParcelDescriptor.Factory paperParcelDescriptorFactory;
  private final PaperParcelGenerator paperParcelGenerator;

  PaperParcelProcessingStep(
      Messager messager,
      OptionsHolder optionsHolder,
      PaperParcelValidator paperParcelValidator,
      PaperParcelDescriptor.Factory paperParcelDescriptorFactory,
      PaperParcelGenerator paperParcelGenerator) {
    this.messager = messager;
    this.optionsHolder = optionsHolder;
    this.paperParcelValidator = paperParcelValidator;
    this.paperParcelDescriptorFactory = paperParcelDescriptorFactory;
    this.paperParcelGenerator = paperParcelGenerator;
  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(PaperParcel.class);
  }

  @Override public Set<Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    for (Element element : elementsByAnnotation.get(PaperParcel.class)) {
      TypeElement paperParcelElement = MoreElements.asType(element);
      OptionsDescriptor options;
      // TODO(brad): always use optionsHolder.getOptions() when PaperParcel.Options is deleted.
      if (optionsHolder.isOptionsApplied()) {
        options = optionsHolder.getOptions();
      } else {
        options = Utils.getOptions(paperParcelElement);
      }
      ValidationReport<TypeElement> validationReport =
          paperParcelValidator.validate(paperParcelElement, options);
      validationReport.printMessagesTo(messager);
      if (validationReport.isClean()) {
        try {
          generatePaperParcel(paperParcelDescriptorFactory.create(paperParcelElement, options));
        } catch (PaperParcelDescriptor.NonWritableFieldsException e) {
          printMessages(e, paperParcelElement);
        } catch (PaperParcelDescriptor.NonReadableFieldsException e) {
          printMessages(e, paperParcelElement);
        } catch (UnknownTypeException e) {
          messager.printMessage(Diagnostic.Kind.ERROR,
              String.format(ErrorMessages.FIELD_MISSING_TYPE_ADAPTER,
                  e.getUnknownType().toString(),
                  ErrorMessages.SITE_URL + "#typeadapters"),
              (Element) e.getArgument());
        }
      }
    }
    return ImmutableSet.of();
  }

  private void generatePaperParcel(PaperParcelDescriptor descriptor) {
    try {
      paperParcelGenerator.generate(descriptor);
    } catch (SourceFileGenerationException e) {
      e.printMessageTo(messager);
    }
  }

  private void printMessages(PaperParcelDescriptor.NonWritableFieldsException e, TypeElement element) {
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
          messager.printMessage(Diagnostic.Kind.ERROR,
              String.format(ErrorMessages.FIELD_NOT_WRITABLE,
                  element.getQualifiedName(),
                  fieldName,
                  validConstructor.toString(),
                  ErrorMessages.SITE_URL + "#model-conventions"),
                  nonWritableField);
        }
      }
    } else {
      // Log errors for unassignable parameters in each invalid constructor
      for (ExecutableElement invalidConstructor : invalidConstructors) {
        ImmutableList<VariableElement> unassignableFields =
            e.unassignableConstructorParameterMap().get(invalidConstructor);
        for (VariableElement unassignableField : unassignableFields) {
          String fieldName = unassignableField.getSimpleName().toString();
          messager.printMessage(Diagnostic.Kind.ERROR,
              String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
                  fieldName,
                  element.getQualifiedName()),
              invalidConstructor);
        }
      }
    }
  }

  private void printMessages(PaperParcelDescriptor.NonReadableFieldsException e, TypeElement element) {
    for (VariableElement nonReadableField : e.nonReadableFields()) {
      String fieldName = nonReadableField.getSimpleName().toString();
      messager.printMessage(Diagnostic.Kind.ERROR,
          String.format(ErrorMessages.FIELD_NOT_ACCESSIBLE,
              element.getQualifiedName(),
              fieldName,
              ErrorMessages.SITE_URL + "#model-conventions"),
          nonReadableField);
    }
  }
}
