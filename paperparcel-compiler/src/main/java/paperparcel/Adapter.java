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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
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
    static abstract class Param {
    }

    static class AdapterParam extends Param {
      final Adapter adapter;

      AdapterParam(Adapter adapter) {
        this.adapter = adapter;
      }
    }

    static class ClassParam extends Param {
      final TypeName className;

      ClassParam(TypeName className) {
        this.className = className;
      }
    }

    static class CreatorParam extends Param {
      @Nullable final ClassName creatorOwner;

      CreatorParam(@Nullable ClassName creatorOwner) {
        this.creatorOwner = creatorOwner;
      }
    }

    /** The ordered parameters of the primary constructor. */
    abstract ImmutableList<Param> constructorParameters();

    public static ConstructorInfo create(ImmutableList<Param> constructorParameters) {
      return new AutoValue_Adapter_ConstructorInfo(constructorParameters);
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
    @SuppressWarnings("ConstantConditions") // Already validated
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
      for (AdapterRegistry.Entry entry : adapterEntries) {
        if (entry.typeKey().isMatch(types, fieldType)) {

          Optional<ConstructorInfo> constructorInfo;
          TypeName typeName;
          Optional<String> singletonInstance;
          TypeName adaptedTypeName;

          if (entry instanceof AdapterRegistry.FieldEntry) {
            AdapterRegistry.FieldEntry fieldEntry = (AdapterRegistry.FieldEntry) entry;
            TypeElement enclosingClass = elements.getTypeElement(fieldEntry.enclosingClass());
            VariableElement adapterField = getField(enclosingClass, fieldEntry.fieldName());
            TypeMirror adapterType = adapterField.asType();
            TypeMirror adaptedType =
                Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(adapterType));
            constructorInfo = Optional.absent();
            typeName = ClassName.get(enclosingClass);
            singletonInstance = Optional.of(fieldEntry.fieldName());
            adaptedTypeName = TypeName.get(adaptedType);

          } else if (entry instanceof AdapterRegistry.ClassEntry) {
            AdapterRegistry.ClassEntry classEntry = (AdapterRegistry.ClassEntry) entry;
            TypeElement adapterElement = elements.getTypeElement(classEntry.qualifiedName());
            TypeMirror adapterType = adapterElement.asType();
            TypeMirror adaptedType =
                Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(adapterType));
            Map<String, TypeMirror> parametersToArguments =
                new HashMap<>(adapterElement.getTypeParameters().size());
            entry.typeKey().mapTypeParamsToVars(types, fieldType, parametersToArguments);
            TypeMirror[] adapterArguments = argumentsAsArray(parametersToArguments, adapterElement);
            DeclaredType resolvedAdapterType = types.getDeclaredType(adapterElement, adapterArguments);
            TypeMirror resolvedAdaptedType = Utils.getAdaptedType(elements, types, resolvedAdapterType);
            singletonInstance = Utils.isSingletonAdapter(elements, types, adapterElement, adaptedType)
                ? Optional.of("INSTANCE")
                : Optional.<String>absent();
            constructorInfo = singletonInstance.isPresent()
                ? Optional.<ConstructorInfo>absent()
                : getConstructorInfo(adapterElement, resolvedAdapterType);
            // Ensure we can construct this adapter. If not, continue the search.
            if (!singletonInstance.isPresent()
                && !constructorInfo.isPresent()) continue;
            typeName = TypeName.get(resolvedAdapterType);
            adaptedTypeName = TypeName.get(resolvedAdaptedType);

          } else {
            throw new IllegalArgumentException("Unexpected entry: " + entry);
          }

          // Create and cache the adapter
          Adapter adapter = new AutoValue_Adapter(
              constructorInfo, singletonInstance, typeName, adaptedTypeName, entry.nullSafe());
          adapterRegistry.registerAdapterFor(fieldTypeName, adapter);

          return adapter;
        }
      }

      return null;
    }

    @SuppressWarnings("ConstantConditions") // Already validated
    private Optional<ConstructorInfo> getConstructorInfo(
        TypeElement adapterElement, DeclaredType resolvedAdapterType) {

      ExecutableElement mainConstructor = Utils.findLargestPublicConstructor(adapterElement);
      if (mainConstructor == null) return Optional.absent();

      ImmutableList.Builder<ConstructorInfo.Param> parameterBuilder = ImmutableList.builder();

      ExecutableType resolvedConstructorType =
          MoreTypes.asExecutable(types.asMemberOf(resolvedAdapterType, mainConstructor));
      List<? extends TypeMirror> resolveParameterList = resolvedConstructorType.getParameterTypes();
      List<? extends VariableElement> parameters = mainConstructor.getParameters();

      for (int i = 0; i < parameters.size(); i++) {
        VariableElement dependencyElement = parameters.get(i);
        TypeMirror resolvedDependencyType = resolveParameterList.get(i);

        if (Utils.isAdapterType(dependencyElement, elements, types)) {
          TypeMirror dependencyAdaptedType =
              Utils.getAdaptedType(elements, types, MoreTypes.asDeclared(resolvedDependencyType));
          Adapter adapterDependency = create(dependencyAdaptedType);
          if (adapterDependency == null) {
            return Optional.absent();
          }
          parameterBuilder.add(new ConstructorInfo.AdapterParam(adapterDependency));

        } else if (Utils.isCreatorType(dependencyElement, elements, types)) {
          TypeMirror creatorArg =
              Utils.getCreatorArg(elements, types, MoreTypes.asDeclared(resolvedDependencyType));
          VariableElement creator = Utils.findCreator(elements, types, creatorArg);
          ClassName creatorOwner = null;
          if (creator != null) {
            creatorOwner = ClassName.get((TypeElement) creator.getEnclosingElement());
          }
          parameterBuilder.add(new ConstructorInfo.CreatorParam(creatorOwner));

        } else {
          TypeMirror classArg =
              Utils.getClassArg(elements, types, MoreTypes.asDeclared(resolvedDependencyType));
          TypeName classTypeName = TypeName.get(classArg);
          parameterBuilder.add(new ConstructorInfo.ClassParam(classTypeName));
        }
      }

      return Optional.of(ConstructorInfo.create(parameterBuilder.build()));
    }

    private TypeMirror[] argumentsAsArray(
        Map<String, TypeMirror> parametersToArguments, TypeElement adapterElement) {
      List<? extends TypeParameterElement> adapterParameters = adapterElement.getTypeParameters();
      TypeMirror[] adapterArguments = new TypeMirror[adapterParameters.size()];
      for (int i = 0; i < adapterParameters.size(); i++) {
        TypeParameterElement parameter = adapterParameters.get(i);
        adapterArguments[i] = parametersToArguments.get(parameter.getSimpleName().toString());
        if (adapterArguments[i] == null) {
          throw new AssertionError("Missing parameter information " + parameter);
        }
      }
      return adapterArguments;
    }

    private VariableElement getField(TypeElement element, String fieldName) {
      List<? extends Element> enclosedElements = element.getEnclosedElements();
      for (Element enclosedElement : enclosedElements) {
        if (enclosedElement instanceof VariableElement
            && enclosedElement.getSimpleName().contentEquals(fieldName)) {
          return (VariableElement) enclosedElement;
        }
      }
      throw new IllegalArgumentException(
          "No field found in " + element.getQualifiedName().toString() + " named " + fieldName);
    }
  }
}
