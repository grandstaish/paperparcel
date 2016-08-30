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

import com.google.auto.common.MoreTypes;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.lang.model.util.Types;

/**
 * Represents all class information about a TypeAdapter. Instances of {@link AdapterDescriptor}
 * are cached across processing rounds, so must never contain {@link TypeMirror}s or
 * {@link Element}s as these types are not comparable across different processing rounds.
 */
@AutoValue
abstract class AdapterDescriptor {
  /**
   * Returns the type for which this TypeAdapter is responsible for handling. This type
   * is always erased.
   */
  abstract TypeName adaptedType();

  /** Returns the fully qualified class name for this TypeAdapter */
  abstract String adapterQualifiedName();

  /**
   * Returns all type parameter information pertaining to this TypeAdapter, or an empty
   * list if there are none.
   */
  abstract ImmutableList<TypeParameter> typeParameters();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  /** Represents the purpose of a single type parameter on this adapter */
  @AutoValue
  static abstract class TypeParameter {
    static final int NO_INDEX = -1;

    /** The simple name of the type parameter */
    abstract String name();

    /** TODO(brad): define index */
    abstract int index();

    static TypeParameter create(String name, int index) {
      return new AutoValue_AdapterDescriptor_TypeParameter(name, index);
    }
  }

  static final class Factory {
    private final Elements elements;
    private final Types types;

    Factory(
        Elements elements,
        Types types) {
      this.elements = elements;
      this.types = types;
    }

    AdapterDescriptor fromAdapterElement(TypeElement adapterElement) {
      DeclaredType adapterType = MoreTypes.asDeclared(adapterElement.asType());
      TypeMirror typeAdapterType = elements.getTypeElement(Constants.TYPE_ADAPTER_CLASS_NAME).asType();
      TypeMirror adaptedType = Utils.getTypeArgumentsOfTypeFromType(
          types, adapterType, typeAdapterType).get(0);
      ImmutableList<TypeVariable> adaptedTypeTypeVariables =
          adaptedType.accept(TYPE_VAR_VISITOR, null);
      List<? extends TypeParameterElement> adapterTypeParameters = adapterElement.getTypeParameters();
      Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(adapterElement);
      ImmutableList<TypeParameter> typeParameters = mainConstructor.isPresent()
          ? getTypeParameters(adapterTypeParameters, adaptedTypeTypeVariables, mainConstructor.get())
          : ImmutableList.<TypeParameter>of();
      boolean singleton = Utils.isSingleton(types, adapterElement);
      return new AutoValue_AdapterDescriptor(
          TypeName.get(types.erasure(adaptedType)),
          adapterElement.getQualifiedName().toString(),
          typeParameters,
          singleton);
    }

    /**
     * Gets the list of {@link TypeParameter}s for this TypeAdapter. Returns an empty
     * list if there are none.
     */
    private ImmutableList<TypeParameter> getTypeParameters(
        List<? extends TypeParameterElement> adapterTypeParameters,
        ImmutableList<TypeVariable> adaptedTypeTypeVariables,
        ExecutableElement mainConstructor) {
      ImmutableList.Builder<TypeParameter> typeParameters = new ImmutableList.Builder<>();
      for (TypeParameterElement adapterParameter : adapterTypeParameters) {
        final String parameterName = adapterParameter.getSimpleName().toString();
        if (isUsedInConstructor(mainConstructor, adapterParameter)) {
          int index = AdapterDescriptor.TypeParameter.NO_INDEX;
          for (int i = 0; i < adaptedTypeTypeVariables.size(); i++) {
            TypeVariable typeVariable = adaptedTypeTypeVariables.get(i);
            if (parameterName.contentEquals(typeVariable.toString())) {
              index = i;
              break;
            }
          }
          typeParameters.add(TypeParameter.create(parameterName, index));
        } else {
          typeParameters.add(TypeParameter.create(parameterName, TypeParameter.NO_INDEX));
        }
      }
      return typeParameters.build();
    }

    /**
     * Returns true if the given {@link TypeParameterElement} is used anywhere in the given
     * constructor, false otherwise
     */
    private boolean isUsedInConstructor(
        ExecutableElement constructor, TypeParameterElement typeParameterElement) {
      for (VariableElement parameter : constructor.getParameters()) {
        Name name = typeParameterElement.getSimpleName();
        ImmutableList<TypeVariable> typeVariables =
            parameter.asType().accept(TYPE_VAR_VISITOR, null);
        Optional<TypeVariable> match = FluentIterable.from(typeVariables)
            .firstMatch(matchesName(name));
        if (match.isPresent()) {
          return true;
        }
      }
      return false;
    }

    private static Predicate<TypeVariable> matchesName(final Name name) {
      return new Predicate<TypeVariable>() {
        @Override public boolean apply(TypeVariable input) {
          return name.contentEquals(input.toString());
        }
      };
    }

    /**
     * Traverses all sub types of a type to find any {@link TypeVariable} instances and returns
     * them in a list.
     */
    private static final TypeVisitor<ImmutableList<TypeVariable>, Void> TYPE_VAR_VISITOR =
        new SimpleTypeVisitor7<ImmutableList<TypeVariable>, Void>() {
          @Override protected ImmutableList<TypeVariable> defaultAction(TypeMirror t, Void p) {
            throw new AssertionError();
          }

          @Override public ImmutableList<TypeVariable> visitPrimitive(PrimitiveType t, Void p) {
            return ImmutableList.of();
          }

          @Override public ImmutableList<TypeVariable> visitTypeVariable(TypeVariable t, Void p) {
            return ImmutableList.of(t);
          }

          @Override public ImmutableList<TypeVariable> visitArray(ArrayType t, Void p) {
            return t.getComponentType().accept(this, p);
          }

          @Override public ImmutableList<TypeVariable> visitDeclared(DeclaredType t, Void p) {
            ImmutableList.Builder<TypeVariable> result = new ImmutableList.Builder<>();
            for (TypeMirror arg : t.getTypeArguments()) {
              result.addAll(arg.accept(this, p));
            }
            return result.build();
          }

          @Override public ImmutableList<TypeVariable> visitWildcard(WildcardType t, Void p) {
            ImmutableList.Builder<TypeVariable> result = new ImmutableList.Builder<>();
            result.addAll(t.getSuperBound().accept(this, p));
            result.addAll(t.getExtendsBound().accept(this, p));
            return result.build();
          }
        };
  }
}
