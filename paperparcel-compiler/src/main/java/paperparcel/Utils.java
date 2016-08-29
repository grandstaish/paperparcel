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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static paperparcel.Constants.PARCELABLE_CLASS_NAME;

/** A grab bag of shared utility methods with no home. FeelsBadMan. */
final class Utils {

  private static final TypeVisitor<List<? extends TypeMirror>, Void> TYPE_ARGUMENTS_VISITOR =
      new SimpleTypeVisitor7<List<? extends TypeMirror>, Void>(
          Collections.<TypeMirror>emptyList()) {
        @Override public List<? extends TypeMirror> visitDeclared(DeclaredType t, Void p) {
          return t.getTypeArguments();
        }
      };

  private static final Ordering<ExecutableElement> PARAMETER_COUNT_ORDER =
      new Ordering<ExecutableElement>() {
        @Override public int compare(
            ExecutableElement left, ExecutableElement right) {
          return Ints.compare(left.getParameters().size(),
              right.getParameters().size());
        }
      };

  /**
   * Returns all type arguments on a type, or an empty list if there are none or {@code type} is
   * not a type that supports type arguments. This method will never throw an exception.
   */
  static List<? extends TypeMirror> getTypeArguments(TypeMirror type) {
    return type.accept(TYPE_ARGUMENTS_VISITOR, null);
  }

  /** Returns the constructor in a given class with the largest number of arguments */
  static Optional<ExecutableElement> findLargestConstructor(TypeElement typeElement) {
    List<ExecutableElement> constructors =
        FluentIterable.from(ElementFilter.constructorsIn(typeElement.getEnclosedElements()))
            .filter(new Predicate<ExecutableElement>() {
              @Override public boolean apply(ExecutableElement executableElement) {
                return !executableElement.getModifiers().contains(PRIVATE);
              }
            })
            .toList();

    if (constructors.size() == 0) {
      return Optional.absent();
    }

    return Optional.of(PARAMETER_COUNT_ORDER.max(constructors));
  }

  /**
   * Returns all of the constructors in a {@link TypeElement} ordered from highest parameter count
   * to lowest
   */
  static ImmutableList<ExecutableElement> orderedConstructorsIn(TypeElement element) {
    return PARAMETER_COUNT_ORDER.reverse().immutableSortedCopy(
        ElementFilter.constructorsIn(element.getEnclosedElements()));
  }

  /**
   * Returns a list of all non-private local and inherited methods (excluding methods
   * defined in {@link Object})
   */
  static ImmutableList<ExecutableElement> getLocalAndInheritedMethods(
      Elements elements, TypeElement element) {
    return FluentIterable.from(MoreElements.getLocalAndInheritedMethods(element, elements))
        .filter(new Predicate<ExecutableElement>() {
          @Override public boolean apply(ExecutableElement method) {
            // Filter out any methods defined in java.lang.Object as they are just
            // wasted cycles
            return !method.getEnclosingElement().asType().toString()
                .equals("java.lang.Object");
          }
        })
        .toList();
  }

  /**
   * Tries to find the {@link TypeMirror} arguments found in {@code from}, but only within the
   * type {@code of}.
   *
   * E.g.: if {@code from} is {@code ArrayList<Integer>} and {@code of} is {@link List}, then
   * this method will return a list containing a single element of {@code Integer}
   */
  static List<? extends TypeMirror> getTypeArgumentsOfTypeFromType(
      Types types, TypeMirror from, TypeMirror of) {
    if (types.isSameType(types.erasure(from), types.erasure(of))) {
      DeclaredType declaredType = (DeclaredType) from;
      return declaredType.getTypeArguments();
    }
    List<? extends TypeMirror> superTypes = types.directSupertypes(from);
    List<? extends TypeMirror> result = null;
    for (TypeMirror superType : superTypes) {
      result = getTypeArgumentsOfTypeFromType(types, superType, of);
      if (result != null) break;
    }
    return result;
  }

  /**
   * A singleton is defined by a class with a public static final field named "INSTANCE"
   * with a type assignable from itself
   */
  static boolean isSingleton(Types types, TypeElement element) {
    for (Element e : ElementFilter.fieldsIn(element.getEnclosedElements())) {
      Set<Modifier> modifiers = e.getModifiers();
      if (modifiers.contains(STATIC)
          && modifiers.contains(PUBLIC)
          && modifiers.contains(FINAL)) {
        if (e.getSimpleName().contentEquals("INSTANCE")) {
          TypeMirror erasedClassType = types.erasure(element.asType());
          TypeMirror erasedFieldType = types.erasure(e.asType());
          return types.isAssignable(erasedFieldType, erasedClassType);
        }
      }
    }
    return false;
  }

  /** Returns all fields on a {@link TypeElement}, including subclasses of that element */
  static ImmutableList<VariableElement> getLocalAndInheritedFields(Types types, TypeElement element) {
    ImmutableList.Builder<VariableElement> fields = ImmutableList.builder();
    for (VariableElement variableElement : fieldsIn(element.getEnclosedElements())) {
      if (!variableElement.getModifiers().contains(STATIC)
          && variableElement.getAnnotation(Exclude.class) == null) {
        fields.add(variableElement);
      }
    }
    TypeMirror superType = element.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      TypeElement superElement = MoreElements.asType(types.asElement(superType));
      fields.addAll(getLocalAndInheritedFields(types, superElement));
    }
    return fields.build();
  }

  /** Returns true if a type implements Parcelable */
  static boolean isParcelable(Elements elements, Types types, TypeMirror type) {
    TypeMirror parcelableType = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
    return types.isAssignable(type, parcelableType);
  }

  /**
   * Converts {@code type} into a {@link TypeMirror} that an {@link AdapterDescriptor} is
   * intended to be able to handle. Results of this call can be used to look up an
   * {@link AdapterDescriptor} from the {@link AdapterRegistry}.
   */
  static TypeMirror getParcelableType(Elements elements, Types types, TypeMirror type) {
    TypeMirror parcelableType = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
    if (!types.isAssignable(type, parcelableType)) {
      return types.erasure(type);
    }
    return parcelableType;
  }

  private Utils() {}
}
