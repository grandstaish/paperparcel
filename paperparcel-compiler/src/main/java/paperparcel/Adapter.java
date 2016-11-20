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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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

  /** All adapter dependencies required to instantiate this adapter */
  abstract ImmutableList<Adapter> dependencies();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  /** TypeName for this Adapter. May be a {@link ClassName} or {@link ParameterizedTypeName} */
  abstract TypeName typeName();

  /** TypeName for the type that this adapter is handling. */
  abstract TypeName adaptedTypeName();

  /** Returns true if this type adapter handles null values. */
  abstract boolean nullSafe();

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
      ImmutableList<AdapterRegistry.Entry> adapterEntries = adapterRegistry.getAdapterEntries();
      // Brute-force search of all adapters to see if any of them can produce this type.
      for (AdapterRegistry.Entry adapterEntry : adapterEntries) {
        TypeElement adapterElement = elements.getTypeElement(adapterEntry.qualifiedName());
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
        ImmutableList<Adapter> dependencies = findDependencies(adapterElement, resolvedAdapterType);
        if (dependencies == null) continue;
        TypeName typeName = TypeName.get(resolvedAdapterType);
        boolean isSingleton = Utils.isSingleton(types, adapterElement);
        TypeName adaptedTypeName = TypeName.get(resolvedAdaptedType);
        Adapter adapter = new AutoValue_Adapter(
            dependencies, isSingleton, typeName, adaptedTypeName, adapterEntry.nullSafe());
        adapterRegistry.registerAdapterFor(fieldTypeName, adapter);
        return adapter;
      }
      return null;
    }

    @Nullable private ImmutableList<Adapter> findDependencies(
        TypeElement adapterElement, DeclaredType resolvedAdapterType) {
      ImmutableList.Builder<Adapter> dependencies = new ImmutableList.Builder<>();
      Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(adapterElement);
      if (mainConstructor.isPresent()) {
        ExecutableType resolvedConstructorType =
            MoreTypes.asExecutable(types.asMemberOf(resolvedAdapterType, mainConstructor.get()));
        for (TypeMirror adapterDependencyType : resolvedConstructorType.getParameterTypes()) {
          TypeMirror dependencyAdaptedType =
              Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(adapterDependencyType));
          Adapter dependency = create(dependencyAdaptedType);
          if (dependency == null) return null;
          dependencies.add(dependency);
        }
      }
      return dependencies.build();
    }

    @Nullable private TypeMirror[] findTypeArguments(
        TypeElement adapterElement, TypeMirror adaptedType, TypeMirror fieldType) {
      if (adaptedType instanceof TypeVariable) {
        TypeMirror erased = types.erasure(adaptedType);
        if (types.isAssignable(fieldType, erased)) {
          return new TypeMirror[] { fieldType };
        }
        return null;
      } else {
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
    }

    @Nullable private TypeMirror findArgument(
        TypeParameterElement parameter, TypeMirror adaptedType, TypeMirror fieldType) {
      final String target = parameter.getSimpleName().toString();
      return adaptedType.accept(new SimpleTypeVisitor6<TypeMirror, TypeMirror>() {
        @Override
        public TypeMirror visitTypeVariable(TypeVariable paramType, TypeMirror argType) {
          if (target.contentEquals(paramType.toString())) {
            TypeMirror erased = types.erasure(paramType);
            if (types.isAssignable(argType, erased)) {
              return argType;
            }
          }
          return null;
        }

        @Override
        public TypeMirror visitArray(ArrayType paramType, TypeMirror argType) {
          if (argType instanceof ArrayType) {
            ArrayType cast = (ArrayType) argType;
            return paramType.getComponentType().accept(this, cast.getComponentType());
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
  }
}
