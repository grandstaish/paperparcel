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
import com.google.googlejavaformat.java.filer.FormattingFiler;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * The annotation processor responsible for generating the classes that drive the PaperParcel
 * implementation.
 */
@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
public class PaperParcelProcessor extends BasicAnnotationProcessor {
  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override protected Iterable<? extends ProcessingStep> initSteps() {
    Messager messager = processingEnv.getMessager();
    Types types = processingEnv.getTypeUtils();
    Elements elements = processingEnv.getElementUtils();
    Filer filer = new FormattingFiler(processingEnv.getFiler());

    AdapterRegistry adapterRegistry = new AdapterRegistry(elements, types);
    OptionsHolder optionsHolder = new OptionsHolder();

    AdapterValidator adapterValidator = new AdapterValidator(elements, types);
    PaperParcelValidator paperParcelValidator =
        new PaperParcelValidator(elements, types);

    FieldDescriptor.Factory fieldDescriptorFactory = new FieldDescriptor.Factory(types);
    AdapterDescriptor.Factory adapterFactory = new AdapterDescriptor.Factory(elements, types, adapterRegistry);
    PaperParcelDescriptor.Factory paperParcelDescriptorFactory =
        new PaperParcelDescriptor.Factory(
            elements,
            types,
            adapterFactory,
            fieldDescriptorFactory);

    PaperParcelGenerator paperParcelGenerator = new PaperParcelGenerator(filer);

    return ImmutableList.of(
        new OptionsProcessingStep(
            messager,
            optionsHolder),
        new AdapterProcessingStep(
            messager,
            adapterValidator,
            adapterRegistry),
        new PaperParcelProcessingStep(
            messager,
            optionsHolder,
            paperParcelValidator,
            paperParcelDescriptorFactory,
            paperParcelGenerator));
  }
}
