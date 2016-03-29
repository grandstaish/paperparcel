package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.getRawTypeName;
import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonParcelableProperty extends Property {
  private final List<Property> children;
  private final boolean isSingleton;
  private final TypeName instantiableTypeName;

  public NonParcelableProperty(List<Property> children, boolean isSingleton, TypeName instantiableTypeName,
                               boolean isNullable, TypeName typeName, boolean isInterface, String name,
                               @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.children = children;
    this.isSingleton = isSingleton;
    this.instantiableTypeName = instantiableTypeName;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, FieldSpec> typeAdapters) {
    if (isSingleton) {
      return literal("$T.INSTANCE", getRawTypeName(getTypeName()));
    }
    String initializer = "new $T(";
    Object[] params;
    int propertiesSize = children.size();
    params = new Object[propertiesSize + 1];
    TypeName typeName = instantiableTypeName;
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }
    for (int i = 0; i < propertiesSize; i++) {
      Property p = children.get(i);
      TypeName parameterTypeName = p.getTypeName();
      if (parameterTypeName instanceof WildcardTypeName) {
        ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
        TypeName[] originalParams = new TypeName[originalType.typeArguments.size()];
        originalParams = originalType.typeArguments.toArray(originalParams);
        originalParams[i] = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
        typeName = ParameterizedTypeName.get(originalType.rawType, originalParams);
      }
      params[i + 1] = p.readFromParcel(block, in, classLoader, typeAdapters);
      initializer += "$L";
      if (i != propertiesSize - 1) {
        initializer += ", ";
      }
    }
    params[0] = typeName;
    initializer += ")";
    return literal(initializer, params);
  }

  @Override
  protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags,
                                    CodeBlock sourceLiteral, Map<ClassName, FieldSpec> typeAdapters) {
    if (!isSingleton) {
      for (Property p : children) {
        p.writeToParcel(block, dest, flags, literal("$L.$N()", sourceLiteral, p.getAccessorMethodName()), typeAdapters);
      }
    }
  }

  @Override public boolean requiresClassLoader() {
    boolean requiresClassLoader = false;
    for (Property p : children) {
      requiresClassLoader |= p.requiresClassLoader();
    }
    return requiresClassLoader;
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    Set<ClassName> combined = new HashSet<>();
    for (Property p : children) {
      combined.addAll(p.requiredTypeAdapters());
    }
    return combined;
  }
}
