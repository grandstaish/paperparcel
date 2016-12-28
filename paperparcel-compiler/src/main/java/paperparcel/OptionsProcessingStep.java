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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import static com.google.auto.common.MoreElements.getAnnotationMirror;

/**
 * Extracts global {@link OptionsDescriptor} to be used for processing and stores it in
 */
final class OptionsProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private final Messager messager;
  private final OptionsHolder optionsHolder;

  OptionsProcessingStep(
      Messager messager,
      OptionsHolder optionsHolder) {
    this.messager = messager;
    this.optionsHolder = optionsHolder;
  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(ProcessorConfig.class);
  }

  @Override public Set<Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    Set<Element> processorConfigElements = elementsByAnnotation.get(ProcessorConfig.class);
    if (optionsHolder.isOptionsApplied() || processorConfigElements.size() > 1) {
      messager.printMessage(Diagnostic.Kind.ERROR, ErrorMessages.MULTIPLE_PROCESSOR_CONFIGS);
    } else if (processorConfigElements.size() == 1) {
      Element configElement = processorConfigElements.iterator().next();
      //noinspection OptionalGetWithoutIsPresent
      AnnotationMirror config = getAnnotationMirror(configElement, ProcessorConfig.class).get();
      optionsHolder.setOptions(Utils.getModuleOptions(config));
    }
    return ImmutableSet.of();
  }
}
