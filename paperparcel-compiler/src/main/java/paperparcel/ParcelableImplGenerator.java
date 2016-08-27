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
import javax.lang.model.util.Elements;

/**
 * The class responsible for generating the Parcelable implementation code classes for the
 * specified {@link ParcelableImplDescriptor}
 */
final class ParcelableImplGenerator extends SourceFileGenerator<ParcelableImplDescriptor> {
  ParcelableImplGenerator(
      Filer filer,
      Elements elements) {
    super(filer, elements);
  }

  @Override ClassName nameGeneratedType(ParcelableImplDescriptor input) {
    ClassName paperParcelClassName = ClassName.get(input.paperParcelClass().element());
    String implName =
        "PaperParcel" + Joiner.on('_').join(paperParcelClassName.simpleNames());
    return paperParcelClassName.topLevelClassName().peerClass(implName);
  }

  @Override Optional<? extends Element> getElementForErrorReporting(
      ParcelableImplDescriptor input) {
    return Optional.of(input.paperParcelClass().element());
  }

  @Override Optional<TypeSpec.Builder> write(ClassName generatedTypeName,
      ParcelableImplDescriptor input) {
    return Optional.of(
        new ParcelableImplWriter(generatedTypeName, input)
            .write());
  }
}
