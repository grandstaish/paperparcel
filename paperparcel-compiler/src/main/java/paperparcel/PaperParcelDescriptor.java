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

import com.google.auto.common.MoreElements;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static paperparcel.Constants.PARCELABLE_CLASS_NAME;

/** Represents a {@link PaperParcel} annotated object */
@AutoValue
abstract class PaperParcelDescriptor {

  /** The original {@link TypeElement} that this class is describing */
  abstract TypeElement element();

  /** All of the fields to be serialized */
  abstract ImmutableList<FieldDescriptor> fields();

  /** All of the field names that can be found in the primary constructor */
  abstract ImmutableList<String> constructorArgumentNames();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  /** Returns true if the class is assignable from Parcelable */
  abstract boolean isParcelable();

  static final class Factory {
    private final Elements elements;
    private final Types types;
    private final FieldDescriptor.Factory fieldDescriptorFactory;

    Factory(
        Elements elements,
        Types types,
        FieldDescriptor.Factory fieldDescriptorFactory) {
      this.elements = elements;
      this.types = types;
      this.fieldDescriptorFactory = fieldDescriptorFactory;
    }

    PaperParcelDescriptor create(TypeElement element) {
      ImmutableList<FieldDescriptor> fields = getFieldDescriptors(element);
      ImmutableList<String> constructorArguments = getConstructorArgumentNames(element);
      boolean singleton = Utils.isSingleton(types, element);
      TypeMirror parcelableType = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
      boolean parcelable = types.isAssignable(element.asType(), parcelableType);
      return new AutoValue_PaperParcelDescriptor(
          element, fields, constructorArguments, singleton, parcelable);
    }

    /** Finds the primary constructor and returns a list of all the argument names */
    private ImmutableList<String> getConstructorArgumentNames(TypeElement element) {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(element);
      if (mainConstructor.isPresent()) {
        ExecutableElement it = mainConstructor.get();
        for (VariableElement variableElement : it.getParameters()) {
          builder.add(variableElement.getSimpleName().toString());
        }
      }
      return builder.build();
    }

    /** Creates all of the {@link FieldDescriptor} instances for the given element */
    private ImmutableList<FieldDescriptor> getFieldDescriptors(TypeElement element) {
      ImmutableList.Builder<FieldDescriptor> builder = ImmutableList.builder();
      ImmutableSet<ExecutableElement> allMethods = getLocalAndInheritedMethods(element);
      ImmutableList<VariableElement> fields = Utils.getLocalAndInheritedFields(types, element);
      for (VariableElement field : fields) {
        builder.add(fieldDescriptorFactory.create(field, allMethods));
      }
      return builder.build();
    }

    /**
     * Returns a list of all non-private local and inherited methods (excluding methods
     * defined in {@link Object})
     */
    private ImmutableSet<ExecutableElement> getLocalAndInheritedMethods(TypeElement element) {
      return FluentIterable.from(MoreElements.getLocalAndInheritedMethods(element, elements))
          .filter(new Predicate<ExecutableElement>() {
            @Override public boolean apply(ExecutableElement method) {
              // Filter out any methods defined in java.lang.Object as they are just
              // wasted cycles
              return !method.getEnclosingElement().asType().toString()
                  .equals("java.lang.Object");
            }
          })
          .toSet();
    }
  }
}
