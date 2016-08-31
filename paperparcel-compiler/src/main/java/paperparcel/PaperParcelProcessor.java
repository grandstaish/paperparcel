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
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * The annotation processor responsible for generating the classes that drive the PaperParcel
 * implementation.
 */
@AutoService(Processor.class)
public class PaperParcelProcessor extends BasicAnnotationProcessor {
  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override protected Iterable<? extends ProcessingStep> initSteps() {
    Messager messager = processingEnv.getMessager();
    Types types = processingEnv.getTypeUtils();
    Elements elements = processingEnv.getElementUtils();
    Filer filer = processingEnv.getFiler();

    AdapterDescriptor.Factory adapterDescriptorFactory =
        new AdapterDescriptor.Factory(elements, types);

    AdapterRegistry adapterRegistry;
    try {
      adapterRegistry = new AdapterRegistry(elements, adapterDescriptorFactory);
    } catch (TypeNotPresentException e) {
      messager.printMessage(Diagnostic.Kind.ERROR,
          String.format(ErrorMessages.TYPE_ADAPTER_NOT_FOUND, e.typeName()));
      return ImmutableList.of();
    }

    FieldDescriptor.Factory fieldDescriptorFactory = new FieldDescriptor.Factory(types);
    WriteInfo.Factory writeInfoFactory = new WriteInfo.Factory(types, fieldDescriptorFactory);
    ReadInfo.Factory readInfoFactory = new ReadInfo.Factory(types, fieldDescriptorFactory);
    AdapterGraph.Factory adapterGraphFactory =
        new AdapterGraph.Factory(elements, types, adapterRegistry);
    PaperParcelDescriptor.Factory paperParcelDescriptorFactory =
        new PaperParcelDescriptor.Factory(types, adapterGraphFactory);

    PaperParcelValidator paperParcelValidator =
        new PaperParcelValidator(
            elements, types, writeInfoFactory, readInfoFactory, adapterRegistry);
    RegisterAdapterValidator registerAdapterValidator =
        new RegisterAdapterValidator(elements, types);

    PaperParcelGenerator paperParcelGenerator = new PaperParcelGenerator(filer, elements);

    return ImmutableList.of(
        new RegisterAdapterProcessingStep(
            messager,
            registerAdapterValidator,
            adapterDescriptorFactory,
            adapterRegistry),
        new PaperParcelProcessingStep(
            messager,
            paperParcelValidator,
            paperParcelDescriptorFactory,
            paperParcelGenerator));
  }
}
