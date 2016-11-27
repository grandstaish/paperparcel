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

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;

/**
 * The class responsible for generating the Parcelable implementation code classes for the
 * specified {@link PaperParcelDescriptor}
 */
final class PaperParcelGenerator extends SourceFileGenerator<PaperParcelDescriptor> {
  PaperParcelGenerator(
      Filer filer) {
    super(filer);
  }

  @Override ClassName nameGeneratedType(PaperParcelDescriptor input) {
    ClassName paperParcelClassName = ClassName.get(input.element());
    String implName =
        "PaperParcel" + Joiner.on('_').join(paperParcelClassName.simpleNames());
    return paperParcelClassName.topLevelClassName().peerClass(implName);
  }

  @Override Optional<? extends Element> getElementForErrorReporting(PaperParcelDescriptor input) {
    return Optional.of(input.element());
  }

  @Override Optional<TypeSpec.Builder> write(
      ClassName generatedTypeName, PaperParcelDescriptor input) {
    return Optional.of(
        new PaperParcelWriter(generatedTypeName, input)
            .write());
  }
}
