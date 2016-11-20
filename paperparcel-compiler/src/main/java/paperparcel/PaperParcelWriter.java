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

import android.support.annotation.NonNull;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Responsible for creating a {@link TypeSpec.Builder} for the Parcelable read and write
 * implementations
 */
final class PaperParcelWriter {
  private static final ClassName PARCEL = ClassName.get("android.os", "Parcel");
  private static final ClassName UTILS = ClassName.get("paperparcel.internal", "Utils");

  private final AdapterNameGenerator adapterNames = new AdapterNameGenerator();

  private final ClassName name;
  private final PaperParcelDescriptor descriptor;

  PaperParcelWriter(
      ClassName name,
      PaperParcelDescriptor descriptor) {
    this.name = name;
    this.descriptor = descriptor;
  }

  final TypeSpec.Builder write() {
    ClassName className = ClassName.get(descriptor.element());
    return TypeSpec.classBuilder(name)
        .addModifiers(FINAL)
        .addFields(adapterDependencies(descriptor.adapters().values()))
        .addField(creator(className))
        .addMethod(writeToParcel(className))
        .addMethod(MethodSpec.constructorBuilder().addModifiers(PRIVATE).build());
  }

  private FieldSpec creator(ClassName className) {
    UniqueNameSet readNames = new UniqueNameSet();

    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, className);

    ParameterSpec in = ParameterSpec.builder(PARCEL, readNames.getUniqueName("in")).build();
    MethodSpec.Builder createFromParcel = MethodSpec.methodBuilder("createFromParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(className)
        .addParameter(in);

    if (descriptor.isSingleton()) {
      createFromParcel.addStatement("return $T.INSTANCE", className);
    } else {
      // Read the fields from the parcel
      ImmutableMap<FieldDescriptor, FieldSpec> fieldMap = readFields(in, readNames);
      for (FieldSpec field : fieldMap.values()) {
        createFromParcel.addStatement("$T $N = $L", field.type, field.name, field.initializer);
      }
      // Re-construct the model and return
      FieldSpec model = initModel(className, readNames, fieldMap);
      createFromParcel.addStatement("$T $N = $L", model.type, model.name, model.initializer)
          .addCode(setFields(model, fieldMap))
          .addStatement("return $N", model.name);
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
        .addAnnotation(NonNull.class)
        .build();
  }

  private ImmutableMap<FieldDescriptor, FieldSpec> readFields(
      ParameterSpec in, UniqueNameSet readNames) {
    ReadInfo readInfo = descriptor.readInfo();
    Preconditions.checkNotNull(readInfo);

    ImmutableMap.Builder<FieldDescriptor, FieldSpec> result = ImmutableMap.builder();

    // Read the fields in the exact same order that they were written to the Parcel. Currently
    // directly readable fields first, then all fields that are read via getters, and finally
    // all fields that require reflection.
    ImmutableList<FieldDescriptor> combined = ImmutableList.<FieldDescriptor>builder()
        .addAll(readInfo.readableFields())
        .addAll(readInfo.getterMethodMap().keySet())
        .build();

    for (FieldDescriptor field : combined) {
      String fieldName = readNames.getUniqueName(field.name());
      result.put(field, readField(fieldName, field, in));
    }

    return result.build();
  }

  @SuppressWarnings("ConstantConditions")
  private FieldSpec readField(String fieldName, FieldDescriptor field, ParameterSpec in) {
    TypeName fieldTypeName = TypeName.get(field.type().get());

    FieldSpec.Builder builder = FieldSpec.builder(fieldTypeName, fieldName);

    if (fieldTypeName.isPrimitive()) {
      if (TypeName.BOOLEAN.equals(fieldTypeName)) {
        builder.initializer("$N.readInt() == 1", in);
      } else if (TypeName.INT.equals(fieldTypeName)) {
        builder.initializer("$N.readInt()", in);
      } else if (TypeName.LONG.equals(fieldTypeName)) {
        builder.initializer("$N.readLong()", in);
      } else if (TypeName.DOUBLE.equals(fieldTypeName)) {
        builder.initializer("$N.readDouble()", in);
      } else if (TypeName.FLOAT.equals(fieldTypeName)) {
        builder.initializer("$N.readFloat()", in);
      } else if (TypeName.CHAR.equals(fieldTypeName)) {
        builder.initializer("(char) $N.readInt()", in);
      } else if (TypeName.BYTE.equals(fieldTypeName)) {
        builder.initializer("$N.readByte()", in);
      } else if (TypeName.SHORT.equals(fieldTypeName)) {
        builder.initializer("(short) $N.readInt()", in);
      } else {
        throw new IllegalArgumentException("Unknown primitive type: " + fieldTypeName);
      }
    } else {
      Adapter adapter = descriptor.adapters().get(field);
      CodeBlock adapterInstance = adapterInstance(adapter);
      builder.initializer("$L.readFromParcel($N)", adapterInstance, in);
    }

    return builder.build();
  }

  private FieldSpec initModel(
      final ClassName className,
      final UniqueNameSet readNames,
      final ImmutableMap<FieldDescriptor, FieldSpec> fieldMap) {

    WriteInfo writeInfo = descriptor.writeInfo();
    Preconditions.checkNotNull(writeInfo);

    ImmutableList<FieldDescriptor> constructorFields = writeInfo.constructorFields();
    CodeBlock constructorParameterList = CodeBlocks.join(FluentIterable.from(constructorFields)
        .transform(new Function<FieldDescriptor, CodeBlock>() {
          @Override public CodeBlock apply(FieldDescriptor field) {
            return CodeBlock.of("$N", fieldMap.get(field));
          }
        }), ", ");

    CodeBlock initializer;
    if (writeInfo.isConstructorVisible()) {
      initializer = CodeBlock.of("new $T($L)", className, constructorParameterList);
    } else {
      // Constructor is private, init via reflection
      CodeBlock constructorArgClassList = CodeBlocks.join(FluentIterable.from(constructorFields)
          .transform(new Function<FieldDescriptor, CodeBlock>() {
            @Override public CodeBlock apply(FieldDescriptor field) {
              return CodeBlock.of("$T.class", rawTypeFrom(field.type().get()));
            }
          }), ", ");
      initializer = CodeBlock.of("$T.init($T.class, new Class[] { $L }, new Object[] { $L })",
          UTILS, className, constructorArgClassList, constructorParameterList);
    }

    return FieldSpec.builder(className, readNames.getUniqueName("data"))
        .initializer(initializer)
        .build();
  }

  private CodeBlock setFields(FieldSpec model, ImmutableMap<FieldDescriptor, FieldSpec> fieldMap) {
    CodeBlock.Builder block = CodeBlock.builder();

    WriteInfo writeInfo = descriptor.writeInfo();
    Preconditions.checkNotNull(writeInfo);

    // Write directly
    for (FieldDescriptor field : writeInfo.writableFields()) {
      if (field.isVisible()) {
        block.addStatement("$N.$N = $N", model.name, field.name(), fieldMap.get(field));
      } else {
        // Field isn't visible, write via reflection
        TypeName enclosingClass = rawTypeFrom(field.element().getEnclosingElement().asType());
        block.addStatement("$T.writeField($N, $T.class, $N, $S)",
            UTILS, fieldMap.get(field), enclosingClass, model.name, field.name());
      }
    }

    // Write via setters
    ImmutableSet<Map.Entry<FieldDescriptor, ExecutableElement>> fieldSetterEntries =
        writeInfo.setterMethodMap().entrySet();
    for (Map.Entry<FieldDescriptor, ExecutableElement> fieldSetterEntry : fieldSetterEntries) {
      Name setterName = fieldSetterEntry.getValue().getSimpleName();
      FieldDescriptor field = fieldSetterEntry.getKey();
      block.addStatement("$N.$N($N)", model.name, setterName, fieldMap.get(field));
    }

    return block.build();
  }

  private MethodSpec writeToParcel(TypeName className) {
    ParameterSpec data = ParameterSpec.builder(className, "data")
        .addAnnotation(NonNull.class)
        .build();

    ParameterSpec dest = ParameterSpec.builder(PARCEL, "dest")
        .addAnnotation(NonNull.class)
        .build();

    ParameterSpec flags = ParameterSpec.builder(int.class, "flags")
        .build();

    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addModifiers(STATIC)
        .addParameter(data)
        .addParameter(dest)
        .addParameter(flags);

    if (!descriptor.isSingleton()) {
      ReadInfo readInfo = descriptor.readInfo();
      Preconditions.checkNotNull(readInfo);

      ImmutableList<FieldDescriptor> readableFields = readInfo.readableFields();
      for (FieldDescriptor field : readableFields) {
        if (field.isVisible()) {
          CodeBlock accessorBlock = CodeBlock.of("$N.$N", data, field.name());
          writeField(builder, field, accessorBlock, dest, flags);
        } else {
          // Field isn't visible, read via reflection.
          TypeName type = rawTypeFrom(field.type().get());
          TypeName enclosingClass = rawTypeFrom(field.element().getEnclosingElement().asType());
          CodeBlock accessorBlock = CodeBlock.of("$T.readField($T.class, $T.class, $N, $S)",
              UTILS, type, enclosingClass, data, field.name());
          writeField(builder, field, accessorBlock, dest, flags);
        }
      }

      ImmutableSet<Map.Entry<FieldDescriptor, ExecutableElement>> fieldGetterEntries =
          readInfo.getterMethodMap().entrySet();
      for (Map.Entry<FieldDescriptor, ExecutableElement> fieldGetterEntry : fieldGetterEntries) {
        FieldDescriptor field = fieldGetterEntry.getKey();
        Name accessorMethodName = fieldGetterEntry.getValue().getSimpleName();
        CodeBlock accessorBlock = CodeBlock.of("$N.$N()", data, accessorMethodName);
        writeField(builder, field, accessorBlock, dest, flags);
      }
    }

    return builder.build();
  }

  @SuppressWarnings("ConstantConditions")
  private void writeField(
      MethodSpec.Builder builder,
      FieldDescriptor field,
      CodeBlock accessorBlock,
      ParameterSpec dest,
      ParameterSpec flags) {
    TypeName fieldTypeName = TypeName.get(field.type().get());
    if (fieldTypeName.isPrimitive()) {
      if (TypeName.BOOLEAN.equals(fieldTypeName)) {
        builder.addStatement("$N.writeInt($L ? 1 : 0)", dest, accessorBlock);
      } else if (TypeName.INT.equals(fieldTypeName)) {
        builder.addStatement("$N.writeInt($L)", dest, accessorBlock);
      } else if (TypeName.LONG.equals(fieldTypeName)) {
        builder.addStatement("$N.writeLong($L)", dest, accessorBlock);
      } else if (TypeName.DOUBLE.equals(fieldTypeName)) {
        builder.addStatement("$N.writeDouble($L)", dest, accessorBlock);
      } else if (TypeName.FLOAT.equals(fieldTypeName)) {
        builder.addStatement("$N.writeFloat($L)", dest, accessorBlock);
      } else if (TypeName.CHAR.equals(fieldTypeName)) {
        builder.addStatement("$N.writeInt($L)", dest, accessorBlock);
      } else if (TypeName.BYTE.equals(fieldTypeName)) {
        builder.addStatement("$N.writeByte($L)", dest, accessorBlock);
      } else if (TypeName.SHORT.equals(fieldTypeName)) {
        builder.addStatement("$N.writeInt($L)", dest, accessorBlock);
      } else {
        throw new IllegalArgumentException("Unknown primitive type: " + fieldTypeName);
      }
    } else {
      Adapter adapter = descriptor.adapters().get(field);
      CodeBlock adapterInstance = adapterInstance(adapter);
      builder.addStatement("$L.writeToParcel($L, $N, $N)", adapterInstance, accessorBlock, dest,
          flags);
    }
  }

  private CodeBlock adapterInstance(Adapter adapter) {
    CodeBlock adapterInstance;
    if (adapter.isSingleton()) {
      adapterInstance = CodeBlock.of("$T.INSTANCE", adapter.typeName());
    } else {
      adapterInstance = CodeBlock.of("$T.$N", name, adapterNames.getName(adapter.typeName()));
    }
    return adapterInstance;
  }

  private ImmutableList<FieldSpec> adapterDependencies(ImmutableCollection<Adapter> adapters) {
    Set<Adapter> emptySet = Sets.newLinkedHashSet();
    return adapterDependenciesInternal(adapters, emptySet);
  }

  /** Returns a list of all of the {@link FieldSpec}s that define the required TypeAdapters */
  private ImmutableList<FieldSpec> adapterDependenciesInternal(
      ImmutableCollection<Adapter> adapters, Set<Adapter> scoped) {
    ImmutableList.Builder<FieldSpec> adapterFields = new ImmutableList.Builder<>();
    for (Adapter adapter : adapters) {
      // Don't define the same adapter twice
      if (scoped.contains(adapter)) {
        continue;
      }
      scoped.add(adapter);
      if (!adapter.isSingleton()) {
        // Add dependencies, then create and add the current adapter
        if (adapter.dependencies().size() > 0) {
          adapterFields.addAll(adapterDependenciesInternal(adapter.dependencies(), scoped));
        }
        // Construct the single instance of this type adapter
        String adapterName = adapterNames.getName(adapter.typeName());
        CodeBlock parameters = getAdapterParameterList(adapter.dependencies());
        FieldSpec.Builder adapterSpec =
            FieldSpec.builder(adapter.typeName(), adapterName, STATIC, FINAL)
                .initializer(CodeBlock.of("new $T($L)", adapter.typeName(), parameters));
        adapterFields.add(adapterSpec.build());
      }
    }
    return adapterFields.build();
  }

  /**
   * Returns a comma-separated {@link CodeBlock} for all of the adapter instances in
   * {@code dependencies}.
   */
  private CodeBlock getAdapterParameterList(ImmutableList<Adapter> dependencies) {
    return CodeBlocks.join(FluentIterable.from(dependencies)
        .transform(new Function<Adapter, CodeBlock>() {
          @Override public CodeBlock apply(Adapter adapter) {
            return adapterInstance(adapter);
          }
        })
        .toList(), ", ");
  }

  private TypeName rawTypeFrom(TypeMirror typeMirror) {
    TypeName typeName = TypeName.get(typeMirror);
    if (typeName instanceof ParameterizedTypeName) {
      return ((ParameterizedTypeName) typeName).rawType;
    }
    return typeName;
  }
}
