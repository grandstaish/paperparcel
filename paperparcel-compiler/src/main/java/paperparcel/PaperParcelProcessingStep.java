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
import paperparcel.PaperParcelValidator.PaperParcelValidation;

/**
 * A {@link BasicAnnotationProcessor.ProcessingStep} that is responsible for dealing with all
 * {@link PaperParcel} annotated objects as part of the {@link PaperParcelProcessor}.
 */
final class PaperParcelProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private final Messager messager;
  private final PaperParcelValidator paperParcelValidator;
  private final PaperParcelDescriptor.Factory paperParcelDescriptorFactory;
  private final PaperParcelGenerator paperParcelGenerator;

  PaperParcelProcessingStep(
      Messager messager,
      PaperParcelValidator paperParcelValidator,
      PaperParcelDescriptor.Factory paperParcelDescriptorFactory,
      PaperParcelGenerator paperParcelGenerator) {
    this.messager = messager;
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
      PaperParcelValidation validationReport =
          paperParcelValidator.validate(paperParcelElement);
      validationReport.report().printMessagesTo(messager);
      if (validationReport.report().isClean()) {
        generatePaperParcel(paperParcelDescriptorFactory.create(
            paperParcelElement, validationReport.writeInfo(), validationReport.readInfo()));
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
}
