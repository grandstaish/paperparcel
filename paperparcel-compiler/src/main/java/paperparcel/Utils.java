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
import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
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

  private static final Ordering<ExecutableElement> PARAMETER_COUNT_ORDER =
      new Ordering<ExecutableElement>() {
        @Override public int compare(
            ExecutableElement left, ExecutableElement right) {
          return Ints.compare(left.getParameters().size(),
              right.getParameters().size());
        }
      };

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
  @SuppressWarnings("UnusedParameters") // Will be used when using auto-common 0.8 (after kapt is fixed)
  static ImmutableList<ExecutableElement> getLocalAndInheritedMethods(
      Elements elements, Types types, TypeElement element) {
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

  /** Returns all non-excluded fields on a {@link PaperParcel} annotated {@link TypeElement}. */
  static ImmutableList<VariableElement> getFieldsToParcel(Types types, TypeElement element) {
    Optional<AnnotationMirror> paperParcelMirror =
        MoreElements.getAnnotationMirror(element, PaperParcel.class);
    if (paperParcelMirror.isPresent()) {
      Options options = getOptions(element);
      return getFieldsToParcelInner(types, element, options);
    } else {
      throw new IllegalArgumentException("element must be annotated with @PaperParcel");
    }
  }

  static Options getOptions(TypeElement element) {
    Optional<AnnotationMirror> optionsMirror = findOptionsMirror(element);
    Options options = Options.DEFAULT;
    if (optionsMirror.isPresent()) {
      List<Set<Modifier>> excludeModifiers = getExcludeModifiers(optionsMirror.get());
      List<String> excludeAnnotationNames = getExcludeAnnotations(optionsMirror.get());
      List<String> exposeAnnotationNames = getExposeAnnotations(optionsMirror.get());
      boolean excludeNonExposedFields = getExcludeNonExposedFields(optionsMirror.get());
      options = Options.create(
          optionsMirror.get(),
          excludeModifiers,
          excludeAnnotationNames,
          exposeAnnotationNames,
          excludeNonExposedFields);
    }
    return options;
  }

  private static Optional<AnnotationMirror> findOptionsMirror(TypeElement element) {
    Optional<AnnotationMirror> options = optionsOnElement(element);
    if (options.isPresent()) return options;
    // Find all annotations on this element that are annotated themselves with @PaperParcel.Options
    // instead.
    ImmutableSet<? extends AnnotationMirror> annotatedAnnotations =
        AnnotationMirrors.getAnnotatedAnnotations(element, PaperParcel.Options.class);
    if (annotatedAnnotations.size() > 1) {
      throw new IllegalStateException("PaperParcel options applied twice.");
    } else if (annotatedAnnotations.size() == 1) {
      AnnotationMirror optionsMirror = annotatedAnnotations.iterator().next();
      return optionsOnElement(optionsMirror.getAnnotationType().asElement());
    }
    return Optional.absent();
  }

  private static Optional<AnnotationMirror> optionsOnElement(Element element) {
    return MoreElements.getAnnotationMirror(element, PaperParcel.Options.class);
  }

  private static ImmutableList<VariableElement> getFieldsToParcelInner(
      Types types, TypeElement element, Options options) {
    ImmutableList.Builder<VariableElement> fields = ImmutableList.builder();
    for (VariableElement variable : fieldsIn(element.getEnclosedElements())) {
      if (!excludeViaModifiers(variable, options.excludeModifiers())
          && !usesAnyAnnotationsFrom(variable, options.excludeAnnotationNames())
          && (!options.excludeNonExposedFields()
              || usesAnyAnnotationsFrom(variable, options.exposeAnnotationNames()))) {
        fields.add(variable);
      }
    }
    TypeMirror superType = element.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      TypeElement superElement = MoreElements.asType(types.asElement(superType));
      fields.addAll(getFieldsToParcelInner(types, superElement, options));
    }
    return fields.build();
  }

  private static boolean excludeViaModifiers(
      final VariableElement variableElement, List<Set<Modifier>> modifiers) {
    return FluentIterable.from(modifiers)
        .firstMatch(new Predicate<Set<Modifier>>() {
          @Override public boolean apply(Set<Modifier> input) {
            return variableElement.getModifiers().containsAll(input);
          }
        })
        .isPresent();
  }

  private static boolean usesAnyAnnotationsFrom(Element element, List<String> annotationNames) {
    for (String annotationName : annotationNames) {
      for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
        String elementAnnotationName = annotationMirror.getAnnotationType().toString();
        if (elementAnnotationName.equals(annotationName)) {
          return true;
        }
      }
    }
    return false;
  }

  private static List<Set<Modifier>> getExcludeModifiers(AnnotationMirror mirror) {
    AnnotationValue excludeFieldsWithModifiers =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeModifiers");
    return convertModifiers(excludeFieldsWithModifiers.accept(INT_ARRAY_VISITOR, null));
  }

  private static List<Set<Modifier>> convertModifiers(List<Integer> intModifiersArray) {
    List<Set<Modifier>> result = new ArrayList<>();
    for (int intModifiers : intModifiersArray) {
      EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
      if ((intModifiers & java.lang.reflect.Modifier.ABSTRACT) != 0) modifiers.add(Modifier.ABSTRACT);
      if ((intModifiers & java.lang.reflect.Modifier.FINAL) != 0) modifiers.add(Modifier.FINAL);
      if ((intModifiers & java.lang.reflect.Modifier.NATIVE) != 0) modifiers.add(Modifier.NATIVE);
      if ((intModifiers & java.lang.reflect.Modifier.PRIVATE) != 0) modifiers.add(Modifier.PRIVATE);
      if ((intModifiers & java.lang.reflect.Modifier.PROTECTED) != 0) modifiers.add(Modifier.PROTECTED);
      if ((intModifiers & java.lang.reflect.Modifier.PUBLIC) != 0) modifiers.add(Modifier.PUBLIC);
      if ((intModifiers & java.lang.reflect.Modifier.STATIC) != 0) modifiers.add(Modifier.STATIC);
      if ((intModifiers & java.lang.reflect.Modifier.STRICT) != 0) modifiers.add(Modifier.STRICTFP);
      if ((intModifiers & java.lang.reflect.Modifier.SYNCHRONIZED) != 0) modifiers.add(Modifier.SYNCHRONIZED);
      if ((intModifiers & java.lang.reflect.Modifier.TRANSIENT) != 0) modifiers.add(Modifier.TRANSIENT);
      if ((intModifiers & java.lang.reflect.Modifier.VOLATILE) != 0) modifiers.add(Modifier.VOLATILE);
      result.add(modifiers);
    }
    return result;
  }

  private static final AnnotationValueVisitor<List<Integer>, Void> INT_ARRAY_VISITOR =
      new SimpleAnnotationValueVisitor6<List<Integer>, Void>() {
        @Override public List<Integer> visitArray(List<? extends AnnotationValue> list, Void p) {
          ImmutableList.Builder<Integer> modifiers = ImmutableList.builder();
          for (AnnotationValue annotationValue : list) {
            modifiers.add(annotationValue.accept(TO_INT, null));
          }
          return modifiers.build();
        }
      };

  private static final AnnotationValueVisitor<Integer, Void> TO_INT =
      new SimpleAnnotationValueVisitor6<Integer, Void>() {
        @Override public Integer visitInt(int value, Void p) {
          return value;
        }

        @Override protected Integer defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  private static List<String> getExcludeAnnotations(AnnotationMirror mirror) {
    AnnotationValue excludeAnnotationNames =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeAnnotations");
    return excludeAnnotationNames.accept(TYPE_NAME_ARRAY_VISITOR, null);
  }

  private static List<String> getExposeAnnotations(AnnotationMirror mirror) {
    AnnotationValue exposeAnnotationNames =
        AnnotationMirrors.getAnnotationValue(mirror, "exposeAnnotations");
    return exposeAnnotationNames.accept(TYPE_NAME_ARRAY_VISITOR, null);
  }

  private static boolean getExcludeNonExposedFields(AnnotationMirror mirror) {
    AnnotationValue excludeNonExposedFields =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeNonExposedFields");
    return excludeNonExposedFields.accept(BOOLEAN_VISITOR, null);
  }

  private static final AnnotationValueVisitor<List<String>, Void> TYPE_NAME_ARRAY_VISITOR =
      new SimpleAnnotationValueVisitor6<List<String>, Void>() {
        @Override public List<String> visitArray(List<? extends AnnotationValue> list, Void p) {
          ImmutableList.Builder<String> modifiers = ImmutableList.builder();
          for (AnnotationValue annotationValue : list) {
            modifiers.add(annotationValue.accept(TO_TYPE, null).toString());
          }
          return modifiers.build();
        }
      };

  private static final AnnotationValueVisitor<TypeMirror, Void> TO_TYPE =
      new SimpleAnnotationValueVisitor6<TypeMirror, Void>() {
        @Override public TypeMirror visitType(TypeMirror type, Void p) {
          return type;
        }

        @Override protected TypeMirror defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  private static final AnnotationValueVisitor<Boolean, Void> BOOLEAN_VISITOR =
      new SimpleAnnotationValueVisitor6<Boolean, Void>() {
        @Override public Boolean visitBoolean(boolean value, Void p) {
          return value;
        }

        @Override protected Boolean defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  /** Returns true if a type implements Parcelable */
  static boolean isParcelable(Elements elements, Types types, TypeMirror type) {
    TypeMirror parcelableType = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
    return types.isAssignable(type, parcelableType);
  }

  /**
   * Boxes primitive types and replaces any type variables with their upper bounds as they will
   * not be able to be used from a static context.
   */
  static TypeMirror normalize(Types types, TypeMirror type) {
    TypeKind kind = type.getKind();
    if (kind.isPrimitive()) {
      return types.boxedClass((PrimitiveType) type).asType();
    } else {
      return type.accept(new SimpleTypeVisitor6<TypeMirror, Types>() {
        @Override public TypeMirror visitArray(ArrayType type, Types types) {
          return types.getArrayType(type.getComponentType().accept(this, types));
        }

        @Override public TypeMirror visitDeclared(DeclaredType type, Types types) {
          TypeElement element = MoreTypes.asTypeElement(type);
          List<? extends TypeMirror> args = type.getTypeArguments();
          TypeMirror[] strippedArgs = new TypeMirror[args.size()];
          for (int i = 0; i < args.size(); i++) {
            TypeMirror arg = args.get(i);
            strippedArgs[i] = arg.accept(this, types);
          }
          return types.getDeclaredType(element, strippedArgs);
        }

        @Override public TypeMirror visitWildcard(WildcardType type, Types types) {
          return types.getWildcardType(type.getExtendsBound().accept(this, types), null);
        }

        @Override public TypeMirror visitPrimitive(PrimitiveType type, Types types) {
          return type;
        }

        @Override public TypeMirror visitTypeVariable(TypeVariable type, Types types) {
          return type.getUpperBound().accept(this, types);
        }
      }, types);
    }
  }

  /** Finds an annotation with the given name on the given element, or null if not found. */
  @Nullable static AnnotationMirror getAnnotationWithSimpleName(Element element, String simpleName) {
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
