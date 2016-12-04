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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

@SuppressWarnings("StaticInitializerReferencesSubClass")
abstract class TypeKey {
  private static final ClassKey OBJECT = ClassKey.get("java.lang.Object");

  /**
   *
   */
  abstract boolean isMatch(Elements elements, Types types, TypeMirror type);

  /**
   *
   */
  abstract Map<String, TypeMirror> parametersToArgumentsMap(
      Elements elements, Types types, TypeMirror target);

  /**
   *
   */
  static TypeKey get(TypeMirror type) {
    return type.accept(new SimpleTypeVisitor6<TypeKey, Void>() {
      @Override public TypeKey visitArray(ArrayType t, Void p) {
        switch (t.getKind()) {
          case BOOLEAN:
            return PrimitiveArrayKey.BOOLEAN_ARRAY;
          case BYTE:
            return PrimitiveArrayKey.BYTE_ARRAY;
          case SHORT:
            return PrimitiveArrayKey.SHORT_ARRAY;
          case INT:
            return PrimitiveArrayKey.INT_ARRAY;
          case LONG:
            return PrimitiveArrayKey.LONG_ARRAY;
          case CHAR:
            return PrimitiveArrayKey.CHAR_ARRAY;
          case FLOAT:
            return PrimitiveArrayKey.FLOAT_ARRAY;
          case DOUBLE:
            return PrimitiveArrayKey.DOUBLE_ARRAY;
          default:
            return ArrayKey.of(t.getComponentType().accept(this, p));
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

  @AutoValue
  static abstract class AnyKey extends TypeKey {
    abstract String name();

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      return true;
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      return Collections.singletonMap(name(), target);
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

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      if (type.getKind() == TypeKind.ARRAY) {
        ArrayType arrayType = (ArrayType) type;
        switch (arrayType.getComponentType().getKind()) {
          case BOOLEAN:
            return this == BOOLEAN_ARRAY;
          case BYTE:
            return this == BYTE_ARRAY;
          case SHORT:
            return this == SHORT_ARRAY;
          case INT:
            return this == INT_ARRAY;
          case LONG:
            return this == LONG_ARRAY;
          case CHAR:
            return this == CHAR_ARRAY;
          case FLOAT:
            return this == FLOAT_ARRAY;
          case DOUBLE:
            return this == DOUBLE_ARRAY;
          default:
            return false;
        }
      }
      return false;
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      return new HashMap<>(0);
    }

    private PrimitiveArrayKey() {
    }
  }

  @AutoValue
  static abstract class ArrayKey extends TypeKey {
    abstract TypeKey componentType();

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      if (type.getKind() == TypeKind.ARRAY) {
        ArrayType arrayType = (ArrayType) type;
        return !arrayType.getComponentType().getKind().isPrimitive()
            && componentType().isMatch(elements, types, arrayType.getComponentType());
      }
      return false;
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      ArrayType targetArrayType = (ArrayType) target;
      TypeMirror targetComponentType = targetArrayType.getComponentType();
      return componentType().parametersToArgumentsMap(elements, types, targetComponentType);
    }

    static ArrayKey of(TypeKey componentType) {
      return new AutoValue_TypeKey_ArrayKey(componentType);
    }
  }

  @AutoValue
  static abstract class ClassKey extends TypeKey {
    abstract String name();

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      return type.getKind() == TypeKind.DECLARED
          && ((TypeElement)((DeclaredType) type).asElement())
          .getQualifiedName().contentEquals(name());
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      return new HashMap<>(0);
    }

    static ClassKey get(String name) {
      return new AutoValue_TypeKey_ClassKey(name);
    }
  }

  @AutoValue
  static abstract class ParameterizedKey extends TypeKey {
    abstract ClassKey rawType();
    abstract ImmutableList<TypeKey> typeArguments();

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      if (type.getKind() != TypeKind.DECLARED) {
        return false;
      }
      if (!rawType().isMatch(elements, types, type)) {
        return false;
      }
      DeclaredType declaredType = (DeclaredType) type;
      if (typeArguments().size() != declaredType.getTypeArguments().size()) {
        return false;
      }
      for (int i = 0; i < typeArguments().size(); i++) {
        TypeMirror mirrorArgument = declaredType.getTypeArguments().get(i);
        TypeKey argument = typeArguments().get(i);
        if (!argument.isMatch(elements, types, mirrorArgument)) {
          return false;
        }
      }
      return true;
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      Map<String, TypeMirror> result = new HashMap<>(typeArguments().size());
      DeclaredType targetDeclaredType = (DeclaredType) target;
      for (int i = 0; i < typeArguments().size(); i++) {
        TypeMirror targetArgument = targetDeclaredType.getTypeArguments().get(i);
        TypeKey argument = typeArguments().get(i);
        result.putAll(argument.parametersToArgumentsMap(elements, types, targetArgument));
      }
      return result;
    }

    static ParameterizedKey get(ClassKey rawType, ImmutableList<TypeKey> typeArguments) {
      return new AutoValue_TypeKey_ParameterizedKey(rawType, typeArguments);
    }
  }

  @AutoValue
  static abstract class BoundedKey extends TypeKey {
    abstract String name();
    abstract ImmutableList<TypeKey> bounds();

    @Override boolean isMatch(Elements elements, Types types, TypeMirror type) {
      for (TypeKey bound : bounds()) {
        if (!types.isAssignable(type, toMirror(elements, types, bound))) {
          return false;
        }
      }
      return true;
    }

    @Override
    Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target) {
      Map<String, TypeMirror> result = new HashMap<>();
      for (TypeKey bound : bounds()) {
        result.putAll(parametersToArgumentsMap(elements, types, target, bound));
      }
      result.put(name(), target);
      return result;
    }

    static BoundedKey get(String name, ImmutableList<TypeKey> bounds) {
      return new AutoValue_TypeKey_BoundedKey(name, bounds);
    }

    private static TypeMirror toMirror(Elements elements, Types types, TypeKey typeKey) {
      TypeMirror mirror = toMirrorInternal(elements, types, typeKey);
      if (mirror.getKind() == TypeKind.WILDCARD) {
        mirror = elements.getTypeElement("java.lang.Object").asType();
      }
      return mirror;
    }

    private static TypeMirror toMirrorInternal(Elements elements, Types types, TypeKey typeKey) {
      if (typeKey instanceof ClassKey) {
        return elements.getTypeElement(((ClassKey) typeKey).name()).asType();

      } else if (typeKey instanceof ParameterizedKey) {
        ParameterizedKey cast = (ParameterizedKey) typeKey;
        TypeMirror[] arguments = new TypeMirror[cast.typeArguments().size()];
        ImmutableList<TypeKey> typeArguments = cast.typeArguments();
        for (int i = 0; i < typeArguments.size(); i++) {
          TypeKey key = typeArguments.get(i);
          if (key instanceof AnyKey) {
            arguments[i] = types.getWildcardType(null, null);
          } else {
            arguments[i] = toMirrorInternal(elements, types, key);
          }
        }
        TypeElement element = elements.getTypeElement(cast.rawType().name());
        return types.getDeclaredType(element, arguments);

      } else if (typeKey instanceof ArrayKey) {
        ArrayKey cast = (ArrayKey) typeKey;
        if (cast.componentType() instanceof AnyKey) {
          return types.getArrayType(
              types.getWildcardType(elements.getTypeElement(OBJECT.name()).asType(), null));
        } else {
          return types.getArrayType(toMirrorInternal(elements, types, cast.componentType()));
        }

      } else if (typeKey instanceof PrimitiveArrayKey) {
        if (typeKey == PrimitiveArrayKey.BOOLEAN_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.BOOLEAN));
        } else if (typeKey == PrimitiveArrayKey.BYTE_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.BYTE));
        } else if (typeKey == PrimitiveArrayKey.CHAR_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.CHAR));
        } else if (typeKey == PrimitiveArrayKey.DOUBLE_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.DOUBLE));
        } else if (typeKey == PrimitiveArrayKey.FLOAT_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.FLOAT));
        } else if (typeKey == PrimitiveArrayKey.INT_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.INT));
        } else if (typeKey == PrimitiveArrayKey.LONG_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.LONG));
        } else if (typeKey == PrimitiveArrayKey.SHORT_ARRAY) {
          return types.getArrayType(types.getPrimitiveType(TypeKind.SHORT));
        } else {
          throw new IllegalArgumentException("Unexpected primitive key: " + typeKey);
        }

      } else if (typeKey instanceof AnyKey) {
        return types.getWildcardType(null, null);

      } else if (typeKey instanceof BoundedKey) {
        BoundedKey cast = (BoundedKey) typeKey;
        for (TypeKey key : cast.bounds()) {
          if (!key.isMatch(elements, types, toMirrorInternal(elements, types, key))) {
            return types.getNoType(TypeKind.NONE);
          }
        }
        return types.getWildcardType(null, null);

      } else {
        throw new IllegalArgumentException("Unexpected type key: " + typeKey);
      }
    }

    private static Map<String, TypeMirror> parametersToArgumentsMap(
        Elements elements, Types types, TypeMirror target, TypeKey bound) {

      if (bound instanceof AnyKey) {
        return Collections.singletonMap(((AnyKey) bound).name(), target);
      }

      if (bound instanceof BoundedKey) {
        return bound.parametersToArgumentsMap(elements, types, target);
      }

      if (bound instanceof ParameterizedKey) {
        ParameterizedKey parameterizedKey = (ParameterizedKey) bound;
        Map<String, TypeMirror> result = new HashMap<>();
        for (int i = 0; i < parameterizedKey.typeArguments().size(); i++) {
          TypeKey arg = parameterizedKey.typeArguments().get(i);
          if (arg.isMatch(elements, types, target)) {
            result.putAll(arg.parametersToArgumentsMap(elements, types, target));
          }
        }
        if (parameterizedKey.isMatch(elements, types, target)) {
          result.putAll(parameterizedKey.parametersToArgumentsMap(elements, types, target));
        }
        return result;
      }

      if (bound instanceof ClassKey) {
        return new HashMap<>(0);
      }

      if (bound instanceof PrimitiveArrayKey) {
        return new HashMap<>(0);
      }

      if (bound instanceof ArrayKey) {
        return new HashMap<>(0);
      }

      throw new IllegalArgumentException("Unknown TypeKey: " + bound);
    }
  }
}
