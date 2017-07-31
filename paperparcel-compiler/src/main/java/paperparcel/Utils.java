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
import com.google.auto.common.Visibility;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
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

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreTypes.asDeclared;
import static com.google.common.base.Preconditions.checkState;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.fieldsIn;

/** A grab bag of shared utility methods with no home. FeelsBadMan. */
final class Utils {
  private static final String TYPE_ADAPTER_CLASS_NAME = "paperparcel.TypeAdapter";
  private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";
  private static final String PARCELABLE_CREATOR_CLASS_NAME = "android.os.Parcelable.Creator";

  private static final Ordering<ExecutableElement> PARAMETER_COUNT_ORDER =
      new Ordering<ExecutableElement>() {
        @Override public int compare(
            ExecutableElement left, ExecutableElement right) {
          return Ints.compare(left.getParameters().size(),
              right.getParameters().size());
        }
      };

  private static final Predicate<ExecutableElement> FILTER_NON_PUBLIC =
      new Predicate<ExecutableElement>() {
        @Override public boolean apply(ExecutableElement executableElement) {
          return Visibility.ofElement(executableElement) == Visibility.PUBLIC;
        }
      };

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

  private static final AnnotationValueVisitor<ImmutableList<String>, Void> TYPE_NAME_ARRAY_VISITOR =
      new SimpleAnnotationValueVisitor6<ImmutableList<String>, Void>() {
        @Override public ImmutableList<String> visitArray(List<? extends AnnotationValue> list, Void p) {
          ImmutableList.Builder<String> modifiers = ImmutableList.builder();
          for (AnnotationValue annotationValue : list) {
            modifiers.add(annotationValue.accept(TO_TYPE, null).toString());
          }
          return modifiers.build();
        }
      };

  static final AnnotationValueVisitor<TypeMirror, Void> TO_TYPE =
      new SimpleAnnotationValueVisitor6<TypeMirror, Void>() {
        @Override public TypeMirror visitType(TypeMirror type, Void p) {
          return type;
        }

        @Override protected TypeMirror defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  static final AnnotationValueVisitor<AnnotationMirror, Void> TO_ANNOTATION =
      new SimpleAnnotationValueVisitor6<AnnotationMirror, Void>() {
        @Override public AnnotationMirror visitAnnotation(AnnotationMirror annotation, Void p) {
          return annotation;
        }

        @Override protected AnnotationMirror defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  static final AnnotationValueVisitor<Boolean, Void> TO_BOOLEAN =
      new SimpleAnnotationValueVisitor6<Boolean, Void>() {
        @Override public Boolean visitBoolean(boolean value, Void p) {
          return value;
        }

        @Override protected Boolean defaultAction(Object ignore, Void p) {
          throw new IllegalArgumentException();
        }
      };

  /**
   * Returns the public constructor in a given class with the largest number of arguments, or
   * null if there are no public constructors.
   */
  @Nullable static ExecutableElement findLargestPublicConstructor(TypeElement typeElement) {
    List<ExecutableElement> constructors =
        FluentIterable.from(ElementFilter.constructorsIn(typeElement.getEnclosedElements()))
            .filter(FILTER_NON_PUBLIC)
            .toList();

    if (constructors.size() == 0) {
      return null;
    }

    return PARAMETER_COUNT_ORDER.max(constructors);
  }

  /** Returns all of the constructors in a {@link TypeElement} that PaperParcel can use. */
  static ImmutableList<ExecutableElement> orderedConstructorsIn(
      TypeElement element, List<String> reflectAnnotations) {
    List<ExecutableElement> allConstructors =
        ElementFilter.constructorsIn(element.getEnclosedElements());

    List<ExecutableElement> visibleConstructors = new ArrayList<>(allConstructors.size());
    for (ExecutableElement constructor : allConstructors) {
      if (Visibility.ofElement(constructor) != Visibility.PRIVATE) {
        visibleConstructors.add(constructor);
      }
    }
    Collections.sort(visibleConstructors, PARAMETER_COUNT_ORDER.reverse());

    List<ExecutableElement> reflectConstructors = new ArrayList<>();
    if (reflectAnnotations.size() > 0) {
      for (ExecutableElement constructor : allConstructors) {
        if (Visibility.ofElement(constructor) == Visibility.PRIVATE
          && usesAnyAnnotationsFrom(constructor, reflectAnnotations)) {
          reflectConstructors.add(constructor);
        }
      }
      Collections.sort(reflectConstructors, PARAMETER_COUNT_ORDER.reverse());
    }

    return ImmutableList.<ExecutableElement>builder()
        .addAll(visibleConstructors)
        .addAll(reflectConstructors)
        .build();
  }

  /** Returns true if {@code element} is a {@code TypeAdapter} type. */
  static boolean isAdapterType(Element element, Elements elements, Types types) {
    TypeMirror typeAdapterType = types.getDeclaredType(
        elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME),
        types.getWildcardType(null, null));
    return types.isAssignable(element.asType(), typeAdapterType);
  }

  /** Returns true if {@code element} is a {@code Parcelable.Creator} type. */
  static boolean isCreatorType(Element element, Elements elements, Types types) {
    TypeMirror creatorType = types.getDeclaredType(
        elements.getTypeElement(PARCELABLE_CREATOR_CLASS_NAME),
        types.getWildcardType(null, null));
    return types.isAssignable(element.asType(), creatorType);
  }

  /** Returns true if {@code element} is a {@link Class} type. */
  static boolean isClassType(Element element, Elements elements, Types types) {
    TypeMirror classType = types.getDeclaredType(
        elements.getTypeElement(Class.class.getName()),
        types.getWildcardType(null, null));
    return types.isAssignable(element.asType(), classType);
  }

  /**
   * Returns the {@link TypeMirror} argument found in a given {@code TypeAdapter} type.
   */
  static TypeMirror getAdaptedType(Elements elements, Types types, DeclaredType adapterType) {
    TypeElement typeAdapterElement = elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME);
    TypeParameterElement param = typeAdapterElement.getTypeParameters().get(0);
    return paramAsMemberOf(types, adapterType, param);
  }

  /**
   * Returns the {@link TypeMirror} argument found in a given {@code Parcelable.Creator} type.
   */
  static TypeMirror getCreatorArg(Elements elements, Types types, DeclaredType creatorType) {
    TypeElement creatorElement = elements.getTypeElement(PARCELABLE_CREATOR_CLASS_NAME);
    TypeParameterElement param = creatorElement.getTypeParameters().get(0);
    return paramAsMemberOf(types, creatorType, param);
  }

  /**
   * Returns the {@link TypeMirror} argument found in a given {@link Class} type.
   */
  static TypeMirror getClassArg(Elements elements, Types types, DeclaredType classType) {
    TypeElement classElement = elements.getTypeElement(Class.class.getName());
    TypeParameterElement param = classElement.getTypeParameters().get(0);
    return paramAsMemberOf(types, classType, param);
  }

  /**
   * A custom implementation for getting the resolved value of a {@link TypeParameterElement}
   * from a {@link DeclaredType}.
   *
   * Usually this can be resolved using {@link Types#asMemberOf(DeclaredType, Element)}, but the
   * Jack compiler implementation currently does not work with {@link TypeParameterElement}s.
   * See https://code.google.com/p/android/issues/detail?id=231164.
   */
  private static TypeMirror paramAsMemberOf(
      Types types, DeclaredType type, TypeParameterElement param) {
    TypeMirror resolved = paramAsMemberOfImpl(types, type, param);
    checkState(resolved != null, "Could not resolve parameter: " + param);
    return resolved;
  }

  @Nullable private static TypeMirror paramAsMemberOfImpl(
      Types types, DeclaredType type, TypeParameterElement param) {
    TypeElement paramEnclosingElement = (TypeElement) param.getEnclosingElement();
    TypeElement typeAsElement = (TypeElement) type.asElement();
    if (paramEnclosingElement.equals(typeAsElement)) {
      List<? extends TypeParameterElement> typeParamElements = typeAsElement.getTypeParameters();
      for (int i = 0; i < typeParamElements.size(); i++) {
        TypeParameterElement typeParamElement = typeParamElements.get(i);
        if (typeParamElement.equals(param)) {
          List<? extends TypeMirror> typeArguments = type.getTypeArguments();
          if (typeArguments.isEmpty()) {
            return types.erasure(param.asType());
          } else {
            return type.getTypeArguments().get(i);
          }
        }
      }
    }
    List<? extends TypeMirror> superTypes = types.directSupertypes(type);
    for (TypeMirror superType : superTypes) {
      if (superType.getKind() == TypeKind.DECLARED) {
        TypeMirror result = paramAsMemberOfImpl(types, (DeclaredType) superType, param);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  /** If {@code type} has a {@code Parcelable.Creator} field instance, return it. */
  @Nullable static VariableElement findCreator(Elements elements, Types types, TypeMirror type) {
    if (type.getKind() != TypeKind.DECLARED) {
      return null;
    }
    DeclaredType declaredType = (DeclaredType) type;
    TypeElement typeElement = (TypeElement) declaredType.asElement();
    return findCreator(elements, types, typeElement);
  }

  /** If {@code subject} has a {@code Parcelable.Creator} field instance, return it. */
  @Nullable static VariableElement findCreator(
      Elements elements, Types types, TypeElement subject) {

    TypeMirror creatorType = types.getDeclaredType(
        elements.getTypeElement(PARCELABLE_CREATOR_CLASS_NAME),
        types.getWildcardType(null, null));

    List<? extends Element> members = elements.getAllMembers(subject);
    for (VariableElement field : ElementFilter.fieldsIn(members)) {
      if (field.getSimpleName().contentEquals("CREATOR")
          && types.isAssignable(field.asType(), creatorType)
          && field.getModifiers().contains(Modifier.STATIC)
          && field.getModifiers().contains(Modifier.PUBLIC)) {
        return field;
      }
    }

    return null;
  }

  /**
   * A singleton is defined by a class with a public static final field named "INSTANCE"
   * with a type assignable from itself.
   */
  static boolean isSingleton(Types types, TypeElement element) {
    return isSingleton(types, element, element.asType());
  }

  /**
   * A singleton is defined by a class with a public static final field named "INSTANCE"
   * with a type assignable from a {@code TypeAdapter} of {@code adaptedType}.
   */
  static boolean isSingletonAdapter(
      Elements elements, Types types, TypeElement element, TypeMirror adaptedType) {
    TypeElement typeAdapterElement = elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME);
    DeclaredType typeAdapterType = types.getDeclaredType(typeAdapterElement, adaptedType);
    return isSingleton(types, element, typeAdapterType);
  }

  private static boolean isSingleton(Types types, TypeElement element, TypeMirror assignableType) {
    for (Element e : ElementFilter.fieldsIn(element.getEnclosedElements())) {
      Set<Modifier> modifiers = e.getModifiers();
      if (modifiers.contains(STATIC) && modifiers.contains(PUBLIC)) {
        if (e.getSimpleName().contentEquals("INSTANCE")) {
          return types.isAssignable(e.asType(), assignableType);
        }
      }
    }
    return false;
  }

  /** Returns all non-excluded fields on a {@link PaperParcel} annotated {@link TypeElement}. */
  static ImmutableList<VariableElement> getFieldsToParcel(
      TypeElement element, OptionsDescriptor options) {
    Optional<AnnotationMirror> paperParcelMirror =
        MoreElements.getAnnotationMirror(element, PaperParcel.class);
    if (paperParcelMirror.isPresent()) {
      ImmutableList.Builder<VariableElement> fields = ImmutableList.builder();
      getFieldsToParcelImpl(element, options, fields, new HashSet<Name>());
      return fields.build();
    } else {
      throw new IllegalArgumentException("element must be annotated with @PaperParcel");
    }
  }

  private static void getFieldsToParcelImpl(
      TypeElement element,
      OptionsDescriptor options,
      ImmutableList.Builder<VariableElement> fields,
      Set<Name> seenFieldNames) {
    for (VariableElement variable : fieldsIn(element.getEnclosedElements())) {
      if (!excludeViaModifiers(variable, options.excludeModifiers())
          && !usesAnyAnnotationsFrom(variable, options.excludeAnnotationNames())
          && !seenFieldNames.contains(variable.getSimpleName())
          && (!options.excludeNonExposedFields()
          || usesAnyAnnotationsFrom(variable, options.exposeAnnotationNames()))) {
        fields.add(variable);
        seenFieldNames.add(variable.getSimpleName());
      }
    }
    TypeMirror superType = element.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      TypeElement superElement = asType(asDeclared(superType).asElement());
      getFieldsToParcelImpl(superElement, options, fields, seenFieldNames);
    }
  }

  static Optional<OptionsDescriptor> getOptions(TypeElement element) {
    Optional<AnnotationMirror> optionsMirror = findOptionsMirror(element);
    Optional<OptionsDescriptor> result;
    if (optionsMirror.isPresent()) {
      result = Optional.of(parseOptions(optionsMirror.get()));
    } else {
      result = Optional.absent();
    }
    return result;
  }

  static OptionsDescriptor getModuleOptions(AnnotationMirror processorConfig) {
    AnnotationValue optionsAnnotationValue =
        AnnotationMirrors.getAnnotationValue(processorConfig, "options");
    AnnotationMirror optionsMirror = optionsAnnotationValue.accept(TO_ANNOTATION, null);
    return parseOptions(optionsMirror);
  }

  private static OptionsDescriptor parseOptions(AnnotationMirror optionsMirror) {
    ImmutableList<Set<Modifier>> excludeModifiers = getExcludeModifiers(optionsMirror);
    ImmutableList<String> excludeAnnotationNames = getExcludeAnnotations(optionsMirror);
    ImmutableList<String> exposeAnnotationNames = getExposeAnnotations(optionsMirror);
    boolean excludeNonExposedFields = getExcludeNonExposedFields(optionsMirror);
    ImmutableList<String> reflectAnnotations = getReflectAnnotations(optionsMirror);
    boolean allowSerializable = getAllowSerializable(optionsMirror);
    return OptionsDescriptor.create(optionsMirror, excludeModifiers, excludeAnnotationNames,
        exposeAnnotationNames, excludeNonExposedFields, reflectAnnotations, allowSerializable);
  }

  static boolean usesAnyAnnotationsFrom(Element element, List<String> annotationNames) {
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

  /** Returns true if a type implements Parcelable */
  static boolean isParcelable(Elements elements, Types types, TypeMirror type) {
    TypeMirror parcelableType = elements.getTypeElement(PARCELABLE_CLASS_NAME).asType();
    return types.isAssignable(type, parcelableType);
  }

  /** Returns true if {@code typeMirror} is a raw type. */
  static boolean isRawType(TypeMirror typeMirror) {
    Set<TypeParameterElement> visited = new HashSet<>();
    return typeMirror.accept(CheckRawTypesVisitor.INSTANCE, visited);
  }

  private static class CheckRawTypesVisitor
      extends SimpleTypeVisitor6<Boolean, Set<TypeParameterElement>> {
    private static final CheckRawTypesVisitor INSTANCE = new CheckRawTypesVisitor();

    @Override public Boolean visitDeclared(DeclaredType t, Set<TypeParameterElement> visited) {
      int expected = asType(t.asElement()).getTypeParameters().size();
      int actual = t.getTypeArguments().size();
      boolean raw = expected != actual;
      if (!raw) {
        for (int i = 0; i < t.getTypeArguments().size(); i++) {
          raw = t.getTypeArguments().get(i).accept(this, visited);
          if (raw) break;
        }
      }
      return raw;
    }

    @Override public Boolean visitArray(ArrayType t, Set<TypeParameterElement> visited) {
      return t.getComponentType().accept(this, visited);
    }

    @Override public Boolean visitTypeVariable(TypeVariable t, Set<TypeParameterElement> visited) {
      TypeParameterElement element = (TypeParameterElement) t.asElement();
      if (visited.contains(element)) return false;
      visited.add(element);
      for (TypeMirror bound : element.getBounds()) {
        if (bound.accept(this, visited)) return true;
      }
      return false;
    }

    @Override protected Boolean defaultAction(TypeMirror t, Set<TypeParameterElement> visited) {
      return false;
    }
  }

  /**
   * <p>Use this when you want a field's type that can be used from a static context (e.g. where
   * the type vars are no longer available).</p>
   *
   * <p>Must never be called on types that have recursive type arguments, e.g.
   * {@literal T extends Comparable<T>}.</p>
   */
  static TypeMirror replaceTypeVariablesWithUpperBounds(Types types, TypeMirror type) {
    return type.accept(UpperBoundSubstitutingVisitor.INSTANCE, types);
  }

  private static class UpperBoundSubstitutingVisitor
      extends SimpleTypeVisitor6<TypeMirror, Types> {
    private static final UpperBoundSubstitutingVisitor INSTANCE = new UpperBoundSubstitutingVisitor();

    @Override public TypeMirror visitTypeVariable(TypeVariable type, Types types) {
      return type.getUpperBound().accept(this, types);
    }

    @Override public TypeMirror visitWildcard(WildcardType type, Types types) {
      if (type.getExtendsBound() != null) {
        return type.getExtendsBound().accept(this, types);
      } else {
        return type.getSuperBound().accept(this, types);
      }
    }

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

    @Override public TypeMirror visitPrimitive(PrimitiveType type, Types types) {
      return type;
    }

    @Override protected TypeMirror defaultAction(TypeMirror type, Types types) {
      throw new IllegalArgumentException("Unexpected type: " + type);
    }
  }

  /** Returns the complete set of type variable names used in {@code type}. */
  static Set<String> getTypeVariableNames(TypeMirror type) {
    Set<String> names = new HashSet<>();
    type.accept(TypeVariableNameVisitor.INSTANCE, names);
    return names;
  }

  private static class TypeVariableNameVisitor extends SimpleTypeVisitor6<Void, Set<String>> {
    private static final TypeVariableNameVisitor INSTANCE = new TypeVariableNameVisitor();

    @Override public Void visitTypeVariable(TypeVariable type, Set<String> visited) {
      if (visited.contains(type.toString())) return null;
      visited.add(type.toString());
      TypeParameterElement element = (TypeParameterElement) type.asElement();
      for (TypeMirror bound : element.getBounds()) {
        bound.accept(this, visited);
      }
      return null;
    }

    @Override public Void visitArray(ArrayType type, Set<String> visited) {
      type.getComponentType().accept(this, visited);
      return null;
    }

    @Override public Void visitDeclared(DeclaredType type, Set<String> visited) {
      for (TypeMirror arg : type.getTypeArguments()) {
        arg.accept(this, visited);
      }
      return null;
    }

    @Override public Void defaultAction(TypeMirror type, Set<String> visited) {
      throw new IllegalArgumentException("Unexpected type: " + type);
    }
  }

  /** Returns true if {@code typeMirror} contains any wildcards. */
  static boolean containsWildcards(TypeMirror typeMirror) {
    Set<TypeParameterElement> visited = new HashSet<>();
    return typeMirror.accept(CheckWildcardsVisitor.INSTANCE, visited);
  }

  private static class CheckWildcardsVisitor
      extends SimpleTypeVisitor6<Boolean, Set<TypeParameterElement>> {
    private static final CheckWildcardsVisitor INSTANCE = new CheckWildcardsVisitor();

    @Override public Boolean visitArray(ArrayType type, Set<TypeParameterElement> visited) {
      return type.getComponentType().accept(this, visited);
    }

    @Override public Boolean visitDeclared(DeclaredType type, Set<TypeParameterElement> visited) {
      for (TypeMirror arg : type.getTypeArguments()) {
        if (arg.accept(this, visited)) return true;
      }
      return false;
    }

    @Override public Boolean visitTypeVariable(TypeVariable t, Set<TypeParameterElement> visited) {
      TypeParameterElement element = (TypeParameterElement) t.asElement();
      if (visited.contains(element)) return false;
      visited.add(element);
      for (TypeMirror mirror : element.getBounds()) {
        if (mirror.accept(this, visited)) return true;
      }
      return false;
    }

    @Override public Boolean visitWildcard(WildcardType t, Set<TypeParameterElement> visited) {
      return true;
    }

    @Override protected Boolean defaultAction(TypeMirror t, Set<TypeParameterElement> visited) {
      return false;
    }
  }

  /** Returns true if {@code typeMirror} contains any intersection types. */
  static boolean containsIntersection(TypeMirror typeMirror) {
    return typeMirror.accept(CheckIntersectionVisitor.INSTANCE, null);
  }

  private static class CheckIntersectionVisitor extends SimpleTypeVisitor6<Boolean, Void> {
    private static final CheckIntersectionVisitor INSTANCE = new CheckIntersectionVisitor();

    @Override public Boolean visitArray(ArrayType type, Void p) {
      return type.getComponentType().accept(this, p);
    }

    @Override public Boolean visitDeclared(DeclaredType type, Void p) {
      for (TypeMirror arg : type.getTypeArguments()) {
        if (arg.accept(this, p)) return true;
      }
      return false;
    }

    @Override public Boolean visitTypeVariable(TypeVariable t, Void p) {
      return t.getUpperBound() != null
          && t.getUpperBound().getKind().name().equals("INTERSECTION");
    }

    @Override protected Boolean defaultAction(TypeMirror t, Void p) {
      return false;
    }
  }

  /**
   * Returns {@code true} if {@code typeMirror} has recursive type arguments, e.g.
   * {@literal T extends Comparable<T>}.
   */
  static boolean hasRecursiveTypeParameter(TypeMirror typeMirror) {
    Set<TypeParameterElement> visited = new HashSet<>();
    return typeMirror.accept(CheckRecursiveTypeVisitor.INSTANCE, visited);
  }

  private static class CheckRecursiveTypeVisitor
      extends SimpleTypeVisitor6<Boolean, Set<TypeParameterElement>> {
    private static final CheckRecursiveTypeVisitor INSTANCE = new CheckRecursiveTypeVisitor();

    @Override public Boolean visitArray(ArrayType type, Set<TypeParameterElement> visited) {
      return type.getComponentType().accept(this, visited);
    }

    @Override public Boolean visitDeclared(DeclaredType type, Set<TypeParameterElement> visited) {
      for (TypeMirror arg : type.getTypeArguments()) {
        if (arg.accept(this, visited)) return true;
      }
      return false;
    }

    @Override public Boolean visitTypeVariable(TypeVariable t, Set<TypeParameterElement> visited) {
      TypeParameterElement element = (TypeParameterElement) t.asElement();
      if (visited.contains(element)) return true;
      visited.add(element);
      for (TypeMirror mirror : element.getBounds()) {
        if (mirror.accept(this, visited)) return true;
      }
      return false;
    }

    @Override protected Boolean defaultAction(TypeMirror t, Set<TypeParameterElement> visited) {
      return false;
    }
  }

  /** Finds an annotation with the given name on the given element, or null if not found. */
  @Nullable static AnnotationMirror getAnnotationWithSimpleName(Element element, String name) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (name.equals(annotationName)) {
        return mirror;
      }
    }
    return null;
  }

  private static Optional<AnnotationMirror> findOptionsMirror(TypeElement element) {
    Optional<AnnotationMirror> result =
        MoreElements.getAnnotationMirror(element, PaperParcel.Options.class);
    if (!result.isPresent()) {
      ImmutableSet<? extends AnnotationMirror> annotatedAnnotations =
          AnnotationMirrors.getAnnotatedAnnotations(element, PaperParcel.Options.class);
      if (annotatedAnnotations.size() > 1) {
        throw new IllegalStateException("PaperParcel options applied twice.");
      } else if (annotatedAnnotations.size() == 1) {
        AnnotationMirror annotatedAnnotation = annotatedAnnotations.iterator().next();
        result = MoreElements.getAnnotationMirror(
            annotatedAnnotation.getAnnotationType().asElement(), PaperParcel.Options.class);
      } else {
        TypeMirror superType = element.getSuperclass();
        if (superType.getKind() != TypeKind.NONE) {
          TypeElement superElement = asType(asDeclared(superType).asElement());
          result = findOptionsMirror(superElement);
        }
      }
    }
    return result;
  }

  private static boolean excludeViaModifiers(
      VariableElement variableElement, List<Set<Modifier>> modifiers) {
    Set<Modifier> fieldModifiers = variableElement.getModifiers();
    for (Set<Modifier> excludeModifiers : modifiers) {
      if (fieldModifiers.containsAll(excludeModifiers)) {
        return true;
      }
    }
    return false;
  }

  private static ImmutableList<Set<Modifier>> getExcludeModifiers(AnnotationMirror mirror) {
    AnnotationValue excludeFieldsWithModifiers =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeModifiers");
    return convertModifiers(excludeFieldsWithModifiers.accept(INT_ARRAY_VISITOR, null));
  }

  private static ImmutableList<Set<Modifier>> convertModifiers(List<Integer> intModifiersArray) {
    ImmutableList.Builder<Set<Modifier>> result = ImmutableList.builder();
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
    return result.build();
  }

  private static ImmutableList<String> getExcludeAnnotations(AnnotationMirror mirror) {
    AnnotationValue excludeAnnotationNames =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeAnnotations");
    return excludeAnnotationNames.accept(TYPE_NAME_ARRAY_VISITOR, null);
  }

  private static ImmutableList<String> getExposeAnnotations(AnnotationMirror mirror) {
    AnnotationValue exposeAnnotationNames =
        AnnotationMirrors.getAnnotationValue(mirror, "exposeAnnotations");
    return exposeAnnotationNames.accept(TYPE_NAME_ARRAY_VISITOR, null);
  }

  private static boolean getExcludeNonExposedFields(AnnotationMirror mirror) {
    AnnotationValue excludeNonExposedFields =
        AnnotationMirrors.getAnnotationValue(mirror, "excludeNonExposedFields");
    return excludeNonExposedFields.accept(TO_BOOLEAN, null);
  }

  private static ImmutableList<String> getReflectAnnotations(AnnotationMirror mirror) {
    AnnotationValue exposeAnnotationNames =
        AnnotationMirrors.getAnnotationValue(mirror, "reflectAnnotations");
    return exposeAnnotationNames.accept(TYPE_NAME_ARRAY_VISITOR, null);
  }

  private static boolean getAllowSerializable(AnnotationMirror mirror) {
    AnnotationValue allowSerializable =
        AnnotationMirrors.getAnnotationValue(mirror, "allowSerializable");
    return allowSerializable.accept(TO_BOOLEAN, null);
  }

  private Utils() {}
}
