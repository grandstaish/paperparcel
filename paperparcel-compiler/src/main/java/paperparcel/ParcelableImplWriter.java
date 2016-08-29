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

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Responsible for creating a {@link TypeSpec.Builder} for the Parcelable read and write
 * implementations
 */
final class ParcelableImplWriter {
  private static final String FIELD_NAME = "data";
  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");

  private final ClassName name;
  private final ParcelableImplDescriptor descriptor;
  private final Map<TypeName, String> nameCache;

  ParcelableImplWriter(
      ClassName name,
      ParcelableImplDescriptor descriptor) {
    this.name = name;
    this.descriptor = descriptor;
    this.nameCache = Maps.newLinkedHashMap();
  }

  final TypeSpec.Builder write() {
    Set<AdapterGraph> emptySet = Sets.newLinkedHashSet();
    ClassName className = ClassName.get(descriptor.paperParcelClass().element());
    return TypeSpec.classBuilder(name)
        .addModifiers(FINAL)
        .addFields(adapterDependencies(descriptor.adapters().values(), emptySet))
        .addField(creator(className))
        .addMethod(writeToParcel(className))
        .addMethod(MethodSpec.constructorBuilder().addModifiers(PRIVATE).build());
  }

  private FieldSpec creator(ClassName className) {
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, className);
    ParameterSpec in = ParameterSpec.builder(PARCEL, "in").build();

    MethodSpec.Builder createFromParcel = MethodSpec.methodBuilder("createFromParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(className)
        .addParameter(in);
    if (descriptor.paperParcelClass().isSingleton()) {
      createFromParcel.addStatement("return $T.INSTANCE", className);
    } else {
      createFromParcel.addCode(readFields(in))
          .addCode(createModel(className))
          .addStatement("return $N", FIELD_NAME);
    }

    MethodSpec.Builder newArray = MethodSpec.methodBuilder("newArray")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(int.class, "size")
        .returns(ArrayTypeName.of(className))
        .addStatement("return new $T[size]", className);

    TypeSpec initializer = TypeSpec.anonymousClassBuilder("")
        .addSuperinterface(creatorOfClass)
        .addMethod(createFromParcel.build())
        .addMethod(newArray.build())
        .build();

    return FieldSpec.builder(creatorOfClass, "CREATOR", STATIC, FINAL)
        .initializer("$L", initializer)
        .build();
  }

  @SuppressWarnings("ConstantConditions")
  private CodeBlock readFields(ParameterSpec in) {
    CodeBlock.Builder block = CodeBlock.builder();
    ImmutableList<FieldDescriptor> fields = descriptor.paperParcelClass().fields();
    for (FieldDescriptor fieldDescriptor : fields) {
      AdapterGraph graph = descriptor.adapters().get(fieldDescriptor.normalizedType());
      TypeName fieldTypeName = TypeName.get(fieldDescriptor.type().get());
      CodeBlock adapterInstance;
      if (graph.adapter().isSingleton()) {
        adapterInstance = CodeBlock.of("$T.INSTANCE", graph.typeName());
      } else {
        adapterInstance = CodeBlock.of("$N", getName(graph.typeName()));
      }
      block.addStatement("$T $N = $L.readFromParcel($N)",
          fieldTypeName, fieldDescriptor.name(), adapterInstance, in);
    }
    return block.build();
  }

  private CodeBlock createModel(ClassName className) {
    CodeBlock.Builder block = CodeBlock.builder();
    PaperParcelDescriptor paperParcelClass = descriptor.paperParcelClass();
    WriteInfo writeInfo = paperParcelClass.writeInfo();
    Preconditions.checkNotNull(writeInfo);
    ImmutableList<FieldDescriptor> constructorFields = writeInfo.constructorFields();
    block.addStatement("$1T $2N = new $1T($3L)",
        className, FIELD_NAME, getConstructorParameterList(constructorFields));
    for (FieldDescriptor field : writeInfo.writableFields()) {
      block.addStatement("$1N.$2N = $2N", FIELD_NAME, field.name());
    }
    ImmutableSet<Map.Entry<FieldDescriptor, ExecutableElement>> fieldSetterEntries =
        writeInfo.setterMethodMap().entrySet();
    for (Map.Entry<FieldDescriptor, ExecutableElement> fieldSetterEntry : fieldSetterEntries) {
      block.addStatement("$N.$N($N)",
          FIELD_NAME,
          fieldSetterEntry.getValue().getSimpleName(),
          fieldSetterEntry.getKey().name());
    }
    return block.build();
  }

  private CodeBlock getConstructorParameterList(ImmutableList<FieldDescriptor> fields) {
    return CodeBlocks.join(FluentIterable.from(fields)
        .transform(new Function<FieldDescriptor, CodeBlock>() {
          @Override public CodeBlock apply(FieldDescriptor field) {
            return CodeBlock.of("$N", field.name());
          }
        }), ", ");
  }

  private MethodSpec writeToParcel(TypeName className) {
    ParameterSpec dest = ParameterSpec.builder(PARCEL, "dest").build();
    ParameterSpec flags = ParameterSpec.builder(int.class, "flags").build();
    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addModifiers(STATIC)
        .addParameter(className, FIELD_NAME)
        .addParameter(dest)
        .addParameter(flags);

    if (!descriptor.paperParcelClass().isSingleton()) {
      ReadInfo readInfo = descriptor.paperParcelClass().readInfo();
      Preconditions.checkNotNull(readInfo);
      ImmutableList<FieldDescriptor> readableFields = readInfo.readableFields();
      for (FieldDescriptor field : readableFields) {
        AdapterGraph graph = descriptor.adapters().get(field.normalizedType());
        CodeBlock adapterInstance = adapterInstance(graph);
        builder.addStatement("$L.writeToParcel($N.$N, $N, $N)",
            adapterInstance, FIELD_NAME, field.name(), dest, flags);
      }

      ImmutableSet<Map.Entry<FieldDescriptor, ExecutableElement>> fieldGetterEntries =
          readInfo.getterMethodMap().entrySet();
      for (Map.Entry<FieldDescriptor, ExecutableElement> fieldGetterEntry : fieldGetterEntries) {
        AdapterGraph graph = descriptor.adapters().get(fieldGetterEntry.getKey().normalizedType());
        CodeBlock adapterInstance = adapterInstance(graph);
        builder.addStatement("$L.writeToParcel($N.$N(), $N, $N)",
            adapterInstance, FIELD_NAME, fieldGetterEntry.getValue().getSimpleName(), dest, flags);
      }
    }

    return builder.build();
  }

  private CodeBlock adapterInstance(AdapterGraph graph) {
    CodeBlock adapterInstance;
    if (graph.adapter().isSingleton()) {
      adapterInstance = CodeBlock.of("$T.INSTANCE", graph.typeName());
    } else {
      adapterInstance = CodeBlock.of("$N", getName(graph.typeName()));
    }
    return adapterInstance;
  }

  /** Returns a list of all of the {@link FieldSpec}s that define the required TypeAdapters */
  private ImmutableList<FieldSpec> adapterDependencies(
      ImmutableCollection<AdapterGraph> graphs, Set<AdapterGraph> scoped) {
    ImmutableList.Builder<FieldSpec> adapterFields = new ImmutableList.Builder<>();
    for (AdapterGraph graph : graphs) {
      // Don't define the same adapter twice
      if (scoped.contains(graph)) {
        continue;
      }
      scoped.add(graph);
      if (!graph.adapter().isSingleton()) {
        // Add dependencies, then create and add the current adapter
        if (graph.dependencies().size() > 0) {
          adapterFields.addAll(adapterDependencies(graph.dependencies(), scoped));
        }
        // Construct the single instance of this type adapter
        String adapterName = getName(graph.typeName());
        CodeBlock parameters = getAdapterParameterList(graph.dependencies());
        FieldSpec.Builder adapterSpec =
            FieldSpec.builder(graph.typeName(), adapterName, PRIVATE, STATIC, FINAL)
                .initializer(CodeBlock.of("new $T($L)", graph.typeName(), parameters));
        adapterFields.add(adapterSpec.build());
      }
    }
    return adapterFields.build();
  }

  /**
   * Returns a comma-separated {@link CodeBlock} for all of the adapter instances in
   * {@code dependencies}.
   */
  private CodeBlock getAdapterParameterList(ImmutableList<AdapterGraph> dependencies) {
    return CodeBlocks.join(FluentIterable.from(dependencies)
        .transform(new Function<AdapterGraph, CodeBlock>() {
          @Override public CodeBlock apply(AdapterGraph graph) {
            CodeBlock instance;
            if (graph.adapter().isSingleton()) {
              instance = CodeBlock.of("$T.INSTANCE", graph.typeName());
            } else {
              instance = CodeBlock.of("$N", getName(graph.typeName()));
            }
            return instance;
          }
        })
        .toList(), ", ");
  }

  /**
   * Creates a name based on the given {@link TypeName}. Names are constants, so will use
   * {@link CaseFormat#UPPER_UNDERSCORE} formatting.
   */
  private String getName(TypeName typeName) {
    String name = nameCache.get(typeName);
    if (name != null) {
      return name;
    }
    name = getNameInternal(typeName);
    name = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    nameCache.put(typeName, name);
    return name;
  }

  private String getNameInternal(TypeName typeName) {
    String adapterName = null;
    if (typeName instanceof WildcardTypeName) {
      WildcardTypeName wildcardTypeName = (WildcardTypeName) typeName;
      String upperBoundsPart = "";
      String lowerBoundsPart = "";
      for (TypeName upperBound : wildcardTypeName.upperBounds) {
        upperBoundsPart += getNameInternal(upperBound);
      }
      for (TypeName lowerBound : wildcardTypeName.lowerBounds) {
        lowerBoundsPart += getNameInternal(lowerBound);
      }
      adapterName = upperBoundsPart + lowerBoundsPart;
    }
    if (typeName instanceof ArrayTypeName) {
      ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
      adapterName = getNameInternal(arrayTypeName.componentType) + "Array";
    }
    if (typeName instanceof ParameterizedTypeName) {
      ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
      String paramPart = "";
      for (TypeName param : parameterizedTypeName.typeArguments) {
        paramPart += getNameInternal(param);
      }
      adapterName = paramPart + parameterizedTypeName.rawType.simpleName();
    }
    if (typeName instanceof ClassName) {
      ClassName className = (ClassName) typeName;
      adapterName = Joiner.on("_").join(className.simpleNames());
    }
    if (adapterName == null) {
      throw new AssertionError();
    }
    return adapterName;
  }
}
