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
 * {@link RegisterAdapter} annotated objects as part of the {@link PaperParcelProcessor}.
 */
final class RegisterAdapterProcessingStep implements BasicAnnotationProcessor.ProcessingStep {
  private final Messager messager;
  private final RegisterAdapterValidator registerAdapterValidator;
  private final AdapterRegistry adapterRegistry;

  RegisterAdapterProcessingStep(
      Messager messager,
      RegisterAdapterValidator registerAdapterValidator,
      AdapterRegistry adapterRegistry) {
    this.messager = messager;
    this.registerAdapterValidator = registerAdapterValidator;
    this.adapterRegistry = adapterRegistry;
  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(RegisterAdapter.class);
  }

  @Override public Set<Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    for (Element element : elementsByAnnotation.get(RegisterAdapter.class)) {
      TypeElement adapterElement = MoreElements.asType(element);
      ValidationReport<TypeElement> report = registerAdapterValidator.validate(adapterElement);
      report.printMessagesTo(messager);
      if (report.isClean()) {
        RegisterAdapter registerAdapter = element.getAnnotation(RegisterAdapter.class);
        adapterRegistry.registerAdapter(
            adapterElement.getQualifiedName().toString(), registerAdapter.nullSafe());
      }
    }
    return ImmutableSet.of();
  }
}
