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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * A {@link BasicAnnotationProcessor.ProcessingStep} that is responsible for dealing with all
 * {@link PaperParcel} annotated objects as part of the {@link PaperParcelProcessor}.
 */
final class PaperParcelProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private final Messager messager;
  private final PaperParcelValidator paperParcelValidator;
  private final FieldsValidator fieldsValidator;
  private final PaperParcelDescriptor.Factory paperParcelDescriptorFactory;
  private final ParcelableImplDescriptor.Factory parcelableImplDescriptorFactory;
  private final ParcelableImplGenerator parcelableImplGenerator;

  PaperParcelProcessingStep(
      Messager messager,
      PaperParcelValidator paperParcelValidator,
      FieldsValidator fieldsValidator,
      PaperParcelDescriptor.Factory paperParcelDescriptorFactory,
      ParcelableImplDescriptor.Factory parcelableImplDescriptorFactory,
      ParcelableImplGenerator parcelableImplGenerator) {
    this.messager = messager;
    this.paperParcelValidator = paperParcelValidator;
    this.fieldsValidator = fieldsValidator;
    this.paperParcelDescriptorFactory = paperParcelDescriptorFactory;
    this.parcelableImplDescriptorFactory = parcelableImplDescriptorFactory;
    this.parcelableImplGenerator = parcelableImplGenerator;
  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(PaperParcel.class);
  }

  @Override public Set<Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    for (Element element : elementsByAnnotation.get(PaperParcel.class)) {
      TypeElement paperParcelElement = MoreElements.asType(element);
      ValidationReport<TypeElement> typeValidationReport =
          paperParcelValidator.validate(paperParcelElement);
      typeValidationReport.printMessagesTo(messager);
      if (typeValidationReport.isClean()) {
        PaperParcelDescriptor descriptor = paperParcelDescriptorFactory.create(paperParcelElement);
        ValidationReport<TypeElement> fieldsValidationReport = fieldsValidator.validate(descriptor);
        fieldsValidationReport.printMessagesTo(messager);
        if (fieldsValidationReport.isClean()) {
          generateParcelableImpl(parcelableImplDescriptorFactory.create(descriptor));
        }
      }
    }
    return ImmutableSet.of();
  }

  private void generateParcelableImpl(ParcelableImplDescriptor descriptor) {
    try {
      parcelableImplGenerator.generate(descriptor);
    } catch (SourceFileGenerationException e) {
      e.printMessageTo(messager);
    }
  }
}
