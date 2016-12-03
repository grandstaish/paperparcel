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
import com.google.auto.common.MoreTypes;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

/**
 * Describes the TypeAdapter required for a particular field, and all of its
 * dependencies. Instances of {@link Adapter} are cached across processing rounds, so must
 * never contain {@link TypeMirror}s or {@link Element}s as these types are not comparable
 * across different processing rounds.
 */
@AutoValue
abstract class Adapter {
  /**
   * The ordered parameter names of the primary constructor, or null if this adapter has a
   * singleton instance (defined by {@link #singletonInstance()}.
   */
  abstract Optional<ConstructorInfo> constructorInfo();

  /**
   * An optional that may contain the name of a singleton instance field of this adapter. The
   * field will be enclosed in {@link #typeName()}.
   */
  abstract Optional<String> singletonInstance();

  /** TypeName for this Adapter. May be a {@link ClassName} or {@link ParameterizedTypeName} */
  abstract TypeName typeName();

  /** TypeName for the type that this adapter is handling. */
  abstract TypeName adaptedTypeName();

  /** Returns true if this type adapter handles null values. */
  abstract boolean nullSafe();

  @AutoValue
  static abstract class ConstructorInfo {

    /** The ordered parameter names of the primary constructor. */
    abstract ImmutableList<String> constructorParameterNames();

    /**
     * All adapter dependencies required to instantiate this adapter indexed by its constructor
     * parameter name.
     */
    abstract ImmutableMap<String, Adapter> adapterDependencies();

    /**
     * All class dependencies required to instantiate this adapter indexed by its constructor
     * parameter name.
     */
    abstract ImmutableMap<String, TypeName> classDependencies();

    public static ConstructorInfo create(
        ImmutableList<String> constructorParameterNames,
        ImmutableMap<String, Adapter> adapterDependencies,
        ImmutableMap<String, TypeName> classDependencies) {
      return new AutoValue_Adapter_ConstructorInfo(
          constructorParameterNames,
          adapterDependencies,
          classDependencies);
    }
  }

  static final class Factory {
    private final Elements elements;
    private final Types types;
    private final AdapterRegistry adapterRegistry;

    Factory(
        Elements elements,
        Types types,
        AdapterRegistry adapterRegistry) {
      this.elements = elements;
      this.types = types;
      this.adapterRegistry = adapterRegistry;
    }

    /**
     * Factory for creating an Adapter instance for {@code fieldType}. {@code fieldType} must not
     * be a primitive type. If {@code fieldType} is an unknown type, this method returns null.
     */
    @Nullable Adapter create(TypeMirror fieldType) {
      if (fieldType.getKind().isPrimitive()) {
        throw new IllegalArgumentException("Primitive types do not need a TypeAdapter.");
      }

      TypeName fieldTypeName = TypeName.get(fieldType);
      final Optional<Adapter> cached = adapterRegistry.getAdapterFor(fieldTypeName);
      if (cached.isPresent()) {
        return cached.get();
      }

      List<AdapterRegistry.Entry> adapterEntries = adapterRegistry.getEntries();

      Optional<ConstructorInfo> constructorInfo;
      TypeName typeName;
      Optional<String> singletonInstance;
      TypeName adaptedTypeName;

      // Brute-force search of all adapters to see if any of them can produce this type.
      for (AdapterRegistry.Entry adapterEntry : adapterEntries) {

        if (adapterEntry instanceof AdapterRegistry.FieldEntry) {
          final AdapterRegistry.FieldEntry fieldEntry = (AdapterRegistry.FieldEntry) adapterEntry;
          TypeElement enclosingClass = elements.getTypeElement(fieldEntry.enclosingClass());
          Optional<VariableElement> adapterFieldOptional =
              getField(enclosingClass, fieldEntry.fieldName());
          if (!adapterFieldOptional.isPresent()) continue;
          VariableElement adapterField = adapterFieldOptional.get();
          TypeMirror adapterType = adapterField.asType();
          TypeMirror adaptedType =
              Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(adapterType));
          if (adaptedType == null || !types.isSameType(adaptedType, fieldType)) continue;
          constructorInfo = Optional.absent();
          typeName = ClassName.get(enclosingClass);
          singletonInstance = Optional.of(fieldEntry.fieldName());
          adaptedTypeName = TypeName.get(adaptedType);

        } else if (adapterEntry instanceof AdapterRegistry.ClassEntry) {
          AdapterRegistry.ClassEntry classEntry = (AdapterRegistry.ClassEntry) adapterEntry;
          TypeElement adapterElement = elements.getTypeElement(classEntry.qualifiedName());
          TypeMirror adapterType = adapterElement.asType();
          TypeMirror adaptedType =
              Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(adapterType));
          TypeMirror[] typeArguments = findTypeArguments(adapterElement, adaptedType, fieldType);
          if (typeArguments == null
              || adapterElement.getTypeParameters().size() != typeArguments.length) continue;
          DeclaredType resolvedAdapterType = types.getDeclaredType(adapterElement, typeArguments);
          TypeMirror resolvedAdaptedType = Utils.getAdaptedType(elements, types, resolvedAdapterType);
          if (resolvedAdaptedType == null
              || !types.isSameType(resolvedAdaptedType, fieldType)) continue;
          singletonInstance = Utils.isSingletonAdapter(elements, types, adapterElement, adaptedType)
              ? Optional.of("INSTANCE")
              : Optional.<String>absent();
          constructorInfo = singletonInstance.isPresent()
              ? Optional.<ConstructorInfo>absent()
              : getConstructorInfo(adapterElement, resolvedAdapterType);
          if (!singletonInstance.isPresent() && !constructorInfo.isPresent()) continue;
          typeName = TypeName.get(resolvedAdapterType);
          adaptedTypeName = TypeName.get(resolvedAdaptedType);

        } else {
          throw new AssertionError("Unknown AdapterRegistry.Entry: " + adapterEntry);
        }

        // Create and cache the adapter
        Adapter adapter = new AutoValue_Adapter(
            constructorInfo, singletonInstance, typeName, adaptedTypeName, adapterEntry.nullSafe());
        adapterRegistry.registerAdapterFor(fieldTypeName, adapter);

        return adapter;
      }
      return null;
    }

    @SuppressWarnings("ConstantConditions") // Already validated
    private Optional<ConstructorInfo> getConstructorInfo(
        TypeElement adapterElement, DeclaredType resolvedAdapterType) {

      ImmutableList.Builder<String> parameterNames = ImmutableList.builder();
      ImmutableMap.Builder<String, Adapter> adapterDependencies = new ImmutableMap.Builder<>();
      ImmutableMap.Builder<String, TypeName> classDependencies = new ImmutableMap.Builder<>();

      Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(adapterElement);
      if (!mainConstructor.isPresent()) return Optional.absent();

      ExecutableType resolvedConstructorType =
          MoreTypes.asExecutable(types.asMemberOf(resolvedAdapterType, mainConstructor.get()));
      List<? extends TypeMirror> resolveParameterList = resolvedConstructorType.getParameterTypes();
      List<? extends VariableElement> parameters = mainConstructor.get().getParameters();

      for (int i = 0; i < parameters.size(); i++) {
        VariableElement dependencyElement = parameters.get(i);
        TypeMirror resolvedDependencyType = resolveParameterList.get(i);
        String parameterName = dependencyElement.getSimpleName().toString();
        parameterNames.add(parameterName);

        if (Utils.isAdapterType(dependencyElement, elements, types)) {
          TypeMirror dependencyAdaptedType =
              Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(resolvedDependencyType));
          Adapter adapterDependency = create(dependencyAdaptedType);
          if (adapterDependency == null) return Optional.absent();
          adapterDependencies.put(parameterName, adapterDependency);

        } else {
          TypeMirror dependencyClassType =
              Utils.getClassType(elements, types, MoreTypes.asDeclared(resolvedDependencyType));
          TypeName dependencyClassTypeName = TypeName.get(dependencyClassType);
          classDependencies.put(parameterName, dependencyClassTypeName);

        }
      }

      return Optional.of(ConstructorInfo.create(
          parameterNames.build(),
          adapterDependencies.build(),
          classDependencies.build()));
    }

    @Nullable private TypeMirror[] findTypeArguments(
        TypeElement adapterElement, TypeMirror adaptedType, TypeMirror fieldType) {
      List<? extends TypeParameterElement> parameters = adapterElement.getTypeParameters();
      TypeMirror[] typeArguments = new TypeMirror[parameters.size()];
      for (int i = 0; i < parameters.size(); i++) {
        TypeParameterElement adapterParameter = parameters.get(i);
        TypeMirror arg = findArgument(adapterParameter, adaptedType, fieldType);
        if (arg == null) return null;
        typeArguments[i] = arg;
      }
      return typeArguments;
    }

    @Nullable private TypeMirror findArgument(
        TypeParameterElement parameter, TypeMirror adaptedType, TypeMirror fieldType) {
      final String target = parameter.getSimpleName().toString();
      return adaptedType.accept(new SimpleTypeVisitor6<TypeMirror, TypeMirror>() {

        @Override
        public TypeMirror visitTypeVariable(TypeVariable paramType, TypeMirror argType) {
          TypeMirror upperBound = paramType.getUpperBound();
          boolean isIntersectionType = IntersectionCompat.isIntersectionType(upperBound);

          if (target.contentEquals(paramType.toString())) {
            // This is the one we're looking for. Check to see if it is assignable to the argType.
            if (isIntersectionType) {
              if (IntersectionCompat.isAssignableToIntersectionType(
                  types, argType, upperBound, target, argType)) {
                return argType;
              }

            } else {
              TypeMirror wildcardedUpperBound =
                  Utils.substituteTypeVariables(types, upperBound, target, argType);
              if (types.isAssignable(argType, wildcardedUpperBound)) {
                return argType;
              }
            }
          } else {

            // Not the TypeVariable we're looking for, check if it contains the target.
            ImmutableSet<String> containedTypeVars = Utils.getTypeVariableNames(upperBound);
            if (containedTypeVars.contains(target)) {

              TypeMirror rawUpperBound = types.erasure(upperBound);
              TypeMirror rawArgType = types.erasure(argType);

              if (types.isSameType(rawUpperBound, rawArgType)) {
                // Try to run it through this visitor again and resolve the parameters that way.
                if (isIntersectionType) {
                  List<? extends TypeMirror> bounds = IntersectionCompat.getBounds(upperBound);
                  for (TypeMirror bound : bounds) {
                    TypeMirror result = bound.accept(this, argType);
                    if (result != null) return result;
                  }

                } else {
                  return upperBound.accept(this, argType);
                }

              } else {
                // Try to substitute the variable and then check it's assignability.
                if (isIntersectionType) {
                  if (IntersectionCompat.isAssignableToIntersectionType(
                      types, argType, upperBound, target, argType)) {
                    return argType;
                  }

                } else {
                  TypeMirror substitutedUpperBound =
                      Utils.substituteTypeVariables(types, upperBound, target, argType);
                  if (types.isAssignable(argType, substitutedUpperBound)) {
                    return argType;
                  }
                }
              }
            }
          }

          return null;
        }

        @Override
        public TypeMirror visitArray(ArrayType paramType, TypeMirror argType) {
          if (argType instanceof ArrayType) {
            ArrayType cast = (ArrayType) argType;
            TypeMirror componentType = cast.getComponentType();
            if (componentType.getKind().isPrimitive()) return null;
            return paramType.getComponentType().accept(this, componentType);
          }
          return null;
        }

        @Override
        public TypeMirror visitDeclared(DeclaredType paramType, TypeMirror argType) {
          if (argType instanceof DeclaredType) {
            DeclaredType cast = (DeclaredType) argType;
            List<? extends TypeMirror> paramArgs = paramType.getTypeArguments();
            List<? extends TypeMirror> castArgs = cast.getTypeArguments();
            if (paramArgs.size() != castArgs.size()) {
              return null;
            }
            for (int i = 0; i < paramArgs.size(); i++) {
              TypeMirror paramArg = paramArgs.get(i);
              TypeMirror castArg = castArgs.get(i);
              TypeMirror result = paramArg.accept(this, castArg);
              if (result != null) return result;
            }
          }
          return null;
        }

        @Override
        public TypeMirror visitWildcard(WildcardType paramType, TypeMirror argType) {
          TypeMirror result = null;
          if (argType instanceof WildcardType) {
            WildcardType cast = (WildcardType) argType;
            if (paramType.getSuperBound() != null && cast.getSuperBound() != null) {
              result = paramType.getSuperBound().accept(this, cast.getSuperBound());
            } else if (paramType.getExtendsBound() != null && cast.getExtendsBound() != null) {
              result = paramType.getExtendsBound().accept(this, cast.getExtendsBound());
            }
          }
          return result;
        }
      }, fieldType);
    }

    private Optional<VariableElement> getField(TypeElement element, final String fieldName) {
      List<? extends Element> enclosedElements = element.getEnclosedElements();
      for (Element enclosedElement : enclosedElements) {
        if (enclosedElement instanceof VariableElement
            && enclosedElement.getSimpleName().contentEquals(fieldName)) {
          return Optional.of((VariableElement) enclosedElement);
        }
      }
      return Optional.absent();
    }
  }
}
