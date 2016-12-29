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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.tools.Diagnostic;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.auto.common.MoreTypes.asDeclared;

/**
 * A {@link BasicAnnotationProcessor.ProcessingStep} that is responsible for registering all of the
 * {@link Adapter}s contained in a {@link ProcessorConfig} annotation.
 */
final class AdapterProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private static final AnnotationValueVisitor<ImmutableList<AnnotationMirror>, Void> ANNOTATION_ARRAY_VISITOR =
      new SimpleAnnotationValueVisitor6<ImmutableList<AnnotationMirror>, Void>() {
        @Override public ImmutableList<AnnotationMirror> visitArray(List<? extends AnnotationValue> list, Void p) {
          ImmutableList.Builder<AnnotationMirror> modifiers = ImmutableList.builder();
          for (AnnotationValue annotationValue : list) {
            modifiers.add(annotationValue.accept(Utils.TO_ANNOTATION, null));
          }
          return modifiers.build();
        }
      };

  private final Messager messager;
  private final AdapterValidator adapterValidator;
  private final AdapterRegistry adapterRegistry;

  AdapterProcessingStep(
      Messager messager,
      AdapterValidator adapterValidator,
      AdapterRegistry adapterRegistry) {
    this.messager = messager;
    this.adapterValidator = adapterValidator;
    this.adapterRegistry = adapterRegistry;
  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(RegisterAdapter.class, ProcessorConfig.class);
  }

  @Override public Set<Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

    Set<Element> processorConfigElements = elementsByAnnotation.get(ProcessorConfig.class);
    if (processorConfigElements.size() > 1) {
      messager.printMessage(Diagnostic.Kind.ERROR, ErrorMessages.MULTIPLE_PROCESSOR_CONFIGS);
    } else if (processorConfigElements.size() == 1) {
      Element configElement = processorConfigElements.iterator().next();
      //noinspection OptionalGetWithoutIsPresent
      AnnotationMirror config = getAnnotationMirror(configElement, ProcessorConfig.class).get();
      ImmutableList<AnnotationMirror> adapterMirrors = getAnnotationValue(config, "adapters")
          .accept(ANNOTATION_ARRAY_VISITOR, null);
      for (AnnotationMirror adapterMirror : adapterMirrors) {
        TypeElement adapterElement = asType(asDeclared(getAnnotationValue(adapterMirror, "value")
            .accept(Utils.TO_TYPE, null))
            .asElement());
        if (!adapterRegistry.contains(adapterElement)) {
          ValidationReport<TypeElement> report = adapterValidator.validate(adapterElement);
          report.printMessagesTo(messager);
          if (report.isClean()) {
            boolean nullSafe = getAnnotationValue(adapterMirror, "nullSafe")
                .accept(Utils.TO_BOOLEAN, null);
            adapterRegistry.addClassEntry(adapterElement, nullSafe);
          }
        }
      }
    }

    // TODO(brad): remove this when @RegisterAdapter is removed
    for (Element element : elementsByAnnotation.get(RegisterAdapter.class)) {
      TypeElement adapterElement = asType(element);
      if (!adapterRegistry.contains(adapterElement)) {
        ValidationReport<TypeElement> report = adapterValidator.validate(adapterElement);
        report.printMessagesTo(messager);
        if (report.isClean()) {
          RegisterAdapter registerAdapter = element.getAnnotation(RegisterAdapter.class);
          adapterRegistry.addClassEntry(adapterElement, registerAdapter.nullSafe());
        }
      }
    }

    return ImmutableSet.of();
  }
}
