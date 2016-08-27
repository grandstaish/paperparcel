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
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import paperparcel.AdapterDescriptor.TypeParameter;

/**
 * Describes the {@link AdapterDescriptor} required for a particular field, and all of its
 * dependencies. Instances of {@link AdapterGraph} are cached across processing rounds, so must
 * never contain {@link TypeMirror}s or {@link Element}s as these types are not comparable
 * across different processing rounds.
 */
@AutoValue
abstract class AdapterGraph {

  /** All dependencies required to instantiate the adapter described by {@link #adapter()} */
  abstract ImmutableList<AdapterGraph> dependencies();

  /** The class information for the adapter required to handle {@link #typeName()} */
  abstract AdapterDescriptor adapter();

  /** TypeName for this AdapterGraph. May be a {@link ClassName} or {@link ParameterizedTypeName} */
  abstract TypeName typeName();

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

    AdapterGraph create(TypeMirror normalizedType) {
      TypeName normalizedTypeName = TypeName.get(normalizedType);
      Optional<AdapterGraph> cached = adapterRegistry.getGraph(normalizedTypeName);
      if (cached.isPresent()) {
        return cached.get();
      }
      TypeName parcelableTypeName = TypeName.get(
          Utils.getParcelableType(elements, types, normalizedType));
      AdapterDescriptor adapterDescriptor = adapterRegistry.getAdapter(parcelableTypeName).get();
      Name adapterQualifiedName = adapterDescriptor.adapterQualifiedName();
      TypeElement adapterElement = elements.getTypeElement(adapterQualifiedName);
      TypeMirror[] typeArguments = getTypeArguments(normalizedType, adapterDescriptor);
      DeclaredType resolvedAdapterType = types.getDeclaredType(adapterElement, typeArguments);
      ImmutableList.Builder<AdapterGraph> dependencies = new ImmutableList.Builder<>();
      Optional<ExecutableElement> mainConstructor = Utils.findLargestConstructor(adapterElement);
      if (mainConstructor.isPresent()) {
        ExecutableType resolvedConstructorType = MoreTypes.asExecutable(
            types.asMemberOf(resolvedAdapterType, mainConstructor.get()));
        TypeMirror typeAdapterType = elements.getTypeElement(Constants.TYPE_ADAPTER_CLASS_NAME).asType();
        for (TypeMirror adapterDependencyType : resolvedConstructorType.getParameterTypes()) {
          TypeMirror dependencyAdaptedType = Utils.getTypeArgumentsOfTypeFromType(
              types, adapterDependencyType, typeAdapterType).get(0);
          dependencies.add(create(dependencyAdaptedType));
        }
      }
      TypeName typeName = TypeName.get(resolvedAdapterType);
      AdapterGraph adapterGraph = new AutoValue_AdapterGraph(
          dependencies.build(), adapterDescriptor, typeName);
      adapterRegistry.registerGraph(normalizedTypeName, adapterGraph);
      return adapterGraph;
    }

    private TypeMirror[] getTypeArguments(TypeMirror type, AdapterDescriptor adapter) {
      ImmutableList<TypeParameter> typeParameters = adapter.typeParameters();
      TypeMirror[] adapterTypeArguments = new TypeMirror[typeParameters.size()];
      for (int i = 0; i < typeParameters.size(); i++) {
        TypeParameter parameter = typeParameters.get(i);
        int index = parameter.index();
        if (index != TypeParameter.NO_INDEX) {
          DeclaredType declaredFieldType = MoreTypes.asDeclared(type);
          adapterTypeArguments[i] = declaredFieldType.getTypeArguments().get(index);
        } else {
          adapterTypeArguments[i] = type;
        }
      }
      return adapterTypeArguments;
    }
  }
}
