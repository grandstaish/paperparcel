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

import android.support.annotation.Nullable;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
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

/** A grab bag of shared utility methods with no home. FeelsBadMan. */
final class Utils {
  private static final String TYPE_ADAPTER_CLASS_NAME = "paperparcel.TypeAdapter";
  private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";

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
      Elements elements, Types types, TypeElement element) {
    return FluentIterable.from(MoreElements.getLocalAndInheritedMethods(element, types, elements))
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
   * Returns the {@link TypeMirror} argument found in a given TypeAdapter type
   */
  static TypeMirror getAdaptedType(Elements elements, Types types, DeclaredType adapterType) {
    TypeElement typeAdapterElement = elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME);
    TypeParameterElement param = typeAdapterElement.getTypeParameters().get(0);
    try {
      return types.asMemberOf(adapterType, param);
    } catch (IllegalArgumentException e) {
      return null;
    }
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

  /** Boxes primitive types */
  static TypeMirror normalize(Types types, TypeMirror type) {
    TypeKind kind = type.getKind();
    return kind.isPrimitive()
        ? types.boxedClass((PrimitiveType) type).asType()
        : type;
  }

  /** Finds an annotation with the given name on the given element, or null if not found. */
  @Nullable static AnnotationMirror getAnnotationWithNameOrNull(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return mirror;
      }
    }
    return null;
  }

  private Utils() {}
}
