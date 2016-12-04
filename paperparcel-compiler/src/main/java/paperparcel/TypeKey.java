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
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

@SuppressWarnings("StaticInitializerReferencesSubClass")
abstract class TypeKey {
  private static final ClassKey OBJECT = ClassKey.get("java.lang.Object");

  /** Checks if {@code type} can be handled by this {@code TypeKey}. */
  abstract boolean isMatch(Types types, TypeMirror type);

  /**
   * <p>Matches any {@code TypeKey} parameter names to the associated {@link TypeMirror} values
   * derived from {@code target}.</p>
   *
   * <p>This method should only be called with a {@code target} that has matched via
   * {@link #isMatch(Types, TypeMirror)}.</p>
   */
  void mapTypeParamsToVars(Types types, TypeMirror target, Map<String, TypeMirror> outMap) {
  }

  /** Factory method for creating {@code TypeKey} instances from a {@link TypeMirror}. */
  static TypeKey get(TypeMirror type) {
    return type.accept(new SimpleTypeVisitor6<TypeKey, Void>() {
      @Override public TypeKey visitArray(ArrayType t, Void p) {
        switch (t.getKind()) {
          case BOOLEAN: return PrimitiveArrayKey.BOOLEAN_ARRAY;
          case BYTE: return PrimitiveArrayKey.BYTE_ARRAY;
          case SHORT: return PrimitiveArrayKey.SHORT_ARRAY;
          case INT: return PrimitiveArrayKey.INT_ARRAY;
          case LONG: return PrimitiveArrayKey.LONG_ARRAY;
          case CHAR: return PrimitiveArrayKey.CHAR_ARRAY;
          case FLOAT: return PrimitiveArrayKey.FLOAT_ARRAY;
          case DOUBLE: return PrimitiveArrayKey.DOUBLE_ARRAY;
          default: return ArrayKey.of(t.getComponentType().accept(this, p));
        }
      }

      @Override public TypeKey visitError(ErrorType t, Void p) {
        return visitDeclared(t, p);
      }

      @Override public TypeKey visitDeclared(DeclaredType t, Void p) {
        ClassKey rawType =
            ClassKey.get(((TypeElement) t.asElement()).getQualifiedName().toString());
        if (t.getTypeArguments().isEmpty()) {
          return rawType;
        }
        ImmutableList.Builder<TypeKey> typeArguments = ImmutableList.builder();
        for (TypeMirror mirror : t.getTypeArguments()) {
          typeArguments.add(mirror.accept(this, p));
        }
        return ParameterizedKey.get(rawType, typeArguments.build());
      }

      @Override public TypeKey visitTypeVariable(TypeVariable t, Void p) {
        TypeParameterElement element = (TypeParameterElement) t.asElement();
        ImmutableList.Builder<TypeKey> builder = ImmutableList.builder();
        for (TypeMirror bound : element.getBounds()) {
          TypeKey boundKey = bound.accept(this, p);
          if (!boundKey.equals(OBJECT)) {
            builder.add(boundKey);
          }
        }
        ImmutableList<TypeKey> bounds = builder.build();
        if (bounds.size() == 0) {
          return AnyKey.get(t.toString());
        } else {
          return BoundedKey.get(t.toString(), bounds);
        }
      }

      @Override protected TypeKey defaultAction(TypeMirror e, Void p) {
        throw new IllegalArgumentException("Unexpected type mirror: " + e);
      }
    }, null);
  }

  @AutoValue static abstract class AnyKey extends TypeKey {
    abstract String name();

    @Override boolean isMatch(Types types, TypeMirror type) {
      return true;
    }

    @Override
    void mapTypeParamsToVars(Types types, TypeMirror target, Map<String, TypeMirror> outMap) {
      outMap.put(name(), target);
    }

    static AnyKey get(String name) {
      return new AutoValue_TypeKey_AnyKey(name);
    }
  }

  static final class PrimitiveArrayKey extends TypeKey {
    static final PrimitiveArrayKey INT_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey BOOLEAN_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey FLOAT_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey DOUBLE_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey LONG_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey CHAR_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey BYTE_ARRAY = new PrimitiveArrayKey();
    static final PrimitiveArrayKey SHORT_ARRAY = new PrimitiveArrayKey();

    @Override boolean isMatch(Types types, TypeMirror type) {
      if (type.getKind() == TypeKind.ARRAY) {
        ArrayType arrayType = (ArrayType) type;
        switch (arrayType.getComponentType().getKind()) {
          case BOOLEAN: return this == BOOLEAN_ARRAY;
          case BYTE: return this == BYTE_ARRAY;
          case SHORT: return this == SHORT_ARRAY;
          case INT: return this == INT_ARRAY;
          case LONG: return this == LONG_ARRAY;
          case CHAR: return this == CHAR_ARRAY;
          case FLOAT: return this == FLOAT_ARRAY;
          case DOUBLE: return this == DOUBLE_ARRAY;
          default: return false;
        }
      }
      return false;
    }
  }

  @AutoValue static abstract class ArrayKey extends TypeKey {
    abstract TypeKey componentType();

    @Override boolean isMatch(Types types, TypeMirror type) {
      if (type.getKind() == TypeKind.ARRAY) {
        ArrayType arrayType = (ArrayType) type;
        return !arrayType.getComponentType().getKind().isPrimitive()
            && componentType().isMatch(types, arrayType.getComponentType());
      }
      return false;
    }

    @Override
    void mapTypeParamsToVars(Types types, TypeMirror target, Map<String, TypeMirror> outMap) {
      ArrayType targetArrayType = (ArrayType) target;
      TypeMirror targetComponentType = targetArrayType.getComponentType();
      componentType().mapTypeParamsToVars(types, targetComponentType, outMap);
    }

    static ArrayKey of(TypeKey componentType) {
      return new AutoValue_TypeKey_ArrayKey(componentType);
    }
  }

  @AutoValue static abstract class ClassKey extends TypeKey {
    abstract String name();

    @Override boolean isMatch(Types types, TypeMirror type) {
      return type.getKind() == TypeKind.DECLARED
          && ((TypeElement)((DeclaredType) type).asElement())
          .getQualifiedName().contentEquals(name());
    }

    static ClassKey get(String name) {
      return new AutoValue_TypeKey_ClassKey(name);
    }
  }

  @AutoValue static abstract class ParameterizedKey extends TypeKey {
    abstract ClassKey rawType();
    abstract ImmutableList<TypeKey> typeArguments();

    @Override boolean isMatch(Types types, TypeMirror type) {
      if (type.getKind() != TypeKind.DECLARED) {
        return false;
      }
      if (!rawType().isMatch(types, type)) {
        return false;
      }
      DeclaredType declaredType = (DeclaredType) type;
      if (typeArguments().size() != declaredType.getTypeArguments().size()) {
        return false;
      }
      for (int i = 0; i < typeArguments().size(); i++) {
        TypeMirror mirrorArgument = declaredType.getTypeArguments().get(i);
        TypeKey argument = typeArguments().get(i);
        if (!argument.isMatch(types, mirrorArgument)) {
          return false;
        }
      }
      return true;
    }

    @Override
    void mapTypeParamsToVars(Types types, TypeMirror target, Map<String, TypeMirror> outMap) {
      DeclaredType targetDeclaredType = (DeclaredType) target;
      for (int i = 0; i < typeArguments().size(); i++) {
        TypeMirror targetArgument = targetDeclaredType.getTypeArguments().get(i);
        TypeKey argument = typeArguments().get(i);
        argument.mapTypeParamsToVars(types, targetArgument, outMap);
      }
    }

    static ParameterizedKey get(ClassKey rawType, ImmutableList<TypeKey> typeArguments) {
      return new AutoValue_TypeKey_ParameterizedKey(rawType, typeArguments);
    }
  }

  @AutoValue
  static abstract class BoundedKey extends TypeKey {
    abstract String name();

    abstract ImmutableList<TypeKey> bounds();

    @Override boolean isMatch(Types types, TypeMirror type) {
      for (TypeKey bound : bounds()) {
        if (boundMirror(types, bound, type) == null) {
          return false;
        }
      }
      return true;
    }

    @Override
    void mapTypeParamsToVars(Types types, TypeMirror target, Map<String, TypeMirror> outMap) {
      for (TypeKey bound : bounds()) {
        TypeMirror boundMirror = boundMirror(types, bound, target);
        bound.mapTypeParamsToVars(types, boundMirror, outMap);
      }
      outMap.put(name(), target);
    }

    @Nullable
    TypeMirror boundMirror(Types types, TypeKey bound, TypeMirror type) {
      if (bound.isMatch(types, type)) {
        return type;
      }
      List<? extends TypeMirror> superTypes = types.directSupertypes(type);
      for (TypeMirror superType : superTypes) {
        if (superType.getKind() != TypeKind.NONE) {
          TypeMirror boundMirror = boundMirror(types, bound, superType);
          if (boundMirror != null) return boundMirror;
        }
      }
      return null;
    }

    static BoundedKey get(String name, ImmutableList<TypeKey> bounds) {
      return new AutoValue_TypeKey_BoundedKey(name, bounds);
    }
  }
}
