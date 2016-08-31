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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

import static paperparcel.Constants.TYPE_ADAPTER_CLASS_NAME;

/**
 * Represents all class information about a TypeAdapter. Instances of {@link AdapterDescriptor}
 * are cached across processing rounds, so must never contain {@link TypeMirror}s or
 * {@link Element}s as these types are not comparable across different processing rounds.
 */
@AutoValue
abstract class AdapterDescriptor {
  private enum Op {
    SKIP,
    SKIP_TYPE_VARIABLE,
    PROCESS
  }

  private static class Index {
    private int index;

    void increment() {
      index++;
    }

    int get() {
      return index;
    }
  }

  private ImmutableList<ImmutableList<Op>> typeParameterData = null;

  /**
   * Returns the type for which this TypeAdapter is responsible for handling. This type
   * is always erased.
   */
  abstract TypeName adaptedType();

  /** Returns the fully qualified class name for this TypeAdapter */
  abstract String adapterQualifiedName();

  /**
   * Returns true if this class is a singleton. Singletons are defined as per
   * {@link Utils#isSingleton(Types, TypeElement)}
   */
  abstract boolean isSingleton();

  /**
   * Returns all of the TypeMirrors that should be used as arguments for an instance of
   * this adapter when processing {@code type}
   */
  TypeMirror[] typeArgumentsRequiredForType(TypeMirror type) {
    Preconditions.checkNotNull(typeParameterData, "typeParameterData == null");
    TypeMirror[] result = new TypeMirror[typeParameterData.size()];
    for (int i = 0; i < typeParameterData.size(); i++) {
      final ImmutableList<Op> ops = typeParameterData.get(i);
      if (ops.size() == 0) {
        result[i] = type;
      } else {
        // Find the TypeMirror that should be used as the argument for the current type
        // parameter by following the operations recorded when parsing.
        result[i] = type.accept(new SimpleTypeVisitor6<TypeMirror, Index>() {
          @Override
          public TypeMirror visitArray(ArrayType type, Index index) {
            switch (ops.get(index.get())) {
              case PROCESS:
                return type;
              case SKIP:
                index.increment();
                return type.getComponentType().accept(this, index);
              default:
              case SKIP_TYPE_VARIABLE:
                index.increment();
                return null;
            }
          }

          @Override
          public TypeMirror visitDeclared(DeclaredType type, Index index) {
            switch (ops.get(index.get())) {
              case PROCESS:
                return type;
              case SKIP:
                index.increment();
                for (TypeMirror arg : type.getTypeArguments()) {
                  index.increment();
                  TypeMirror result = arg.accept(this, index);
                  if (result != null) return result;
                }
                return null;
              default:
              case SKIP_TYPE_VARIABLE:
                index.increment();
                return null;
            }
          }

          @Override
          public TypeMirror visitWildcard(WildcardType type, Index index) {
            type.getSuperBound().accept(this, index);
            type.getExtendsBound().accept(this, index);
            return null;
          }
        }, new Index());
      }
    }
    return result;
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
      TypeMirror typeAdapterType = elements.getTypeElement(TYPE_ADAPTER_CLASS_NAME).asType();
      TypeMirror adaptedType = Utils.getTypeArgumentsOfTypeFromType(
          types, adapterType, typeAdapterType).get(0);
      TypeName adaptedTypeName = TypeName.get(types.erasure(adaptedType));
      String adapterQualifiedName = adapterElement.getQualifiedName().toString();
      boolean singleton = Utils.isSingleton(types, adapterElement);
      AdapterDescriptor instance =
          new AutoValue_AdapterDescriptor(adaptedTypeName, adapterQualifiedName, singleton);
      instance.typeParameterData = getTypeParameterData(adapterElement, adaptedType);
      return instance;
    }

    private ImmutableList<ImmutableList<Op>> getTypeParameterData(
        final TypeElement element, TypeMirror adaptedType) {
      // If the adapted type is a type variable itself, we know that it must be the only one
      // thanks to the validation step. For resolving this argument, we should use the field's
      // full type, hence we don't need to do any processing of the type.
      TypeVariable maybeTypeVariable = asTypeVariableSafe(adaptedType);
      if (maybeTypeVariable != null) {
        return ImmutableList.of(ImmutableList.<Op>of());
      }
      // For each type parameter on the adapter element, search through the adapted type
      // until the usage of it is found. While searching, record the steps that were taken in
      // order to find the type parameter usage. These steps will be used later to determine
      // the type arguments needed for an instance of this adapter for the type of field it
      // is adapting.
      ImmutableList.Builder<ImmutableList<Op>> result = ImmutableList.builder();
      for (TypeParameterElement adapterParameter : element.getTypeParameters()) {
        final ImmutableList.Builder<Op> ops = ImmutableList.builder();
        final String target = adapterParameter.getSimpleName().toString();
        adaptedType.accept(new SimpleTypeVisitor6<Boolean, Void>() {
          @Override
          public Boolean visitTypeVariable(TypeVariable type, Void p) {
            if (target.contentEquals(type.toString())) {
              ops.add(Op.PROCESS);
              return true;
            } else {
              ops.add(Op.SKIP_TYPE_VARIABLE);
              return false;
            }
          }

          @Override
          public Boolean visitArray(ArrayType type, Void p) {
            ops.add(Op.SKIP);
            return type.getComponentType().accept(this, p);
          }

          @Override
          public Boolean visitDeclared(DeclaredType type, Void p) {
            ops.add(Op.SKIP);
            for (TypeMirror arg : type.getTypeArguments()) {
              ops.add(Op.SKIP);
              if (arg.accept(this, p)) return true;
            }
            return false;
          }

          @Override
          public Boolean visitWildcard(WildcardType type, Void p) {
            return type.getSuperBound().accept(this, p)
                || type.getExtendsBound().accept(this, p);
          }
        }, null);
        result.add(ops.build());
      }
      return result.build();
    }

    /**
     * Returns a {@link TypeVariable} if the {@link TypeMirror} represents a type variable
     * or null if not.
     */
    private static TypeVariable asTypeVariableSafe(TypeMirror maybeTypeVariable) {
      return maybeTypeVariable.accept(new SimpleTypeVisitor6<TypeVariable, Void>() {
        @Override public TypeVariable visitTypeVariable(TypeVariable type, Void p) {
          return type;
        }
      }, null);
    }
  }
}
