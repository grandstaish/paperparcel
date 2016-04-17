package nz.bradcampbell.paperparcel.utils;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import nz.bradcampbell.paperparcel.TypeAdapter;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class TypeUtils {
  private TypeUtils() {
    // No instances.
  }

  public static boolean isInterface(Types typeUtil, TypeMirror typeMirror) {
    Element typeElement = typeUtil.asElement(typeMirror);
    return typeElement != null && typeElement.getKind() == ElementKind.INTERFACE;
  }

  public static boolean hasTypeArguments(TypeElement typeElement) {
    TypeMirror type = typeElement.asType();
    if (type instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) type;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() > 0) {
        return true;
      }
    }
    return false;
  }

  /** Returns a string for the raw type of {@code type}. Primitive types are always boxed. */
  public static String rawTypeToString(TypeMirror type, char innerClassSeparator) {
    if (!(type instanceof DeclaredType)) {
      throw new IllegalArgumentException("Unexpected type: " + type);
    }
    StringBuilder result = new StringBuilder();
    DeclaredType declaredType = (DeclaredType) type;
    rawTypeToString(result, (TypeElement) declaredType.asElement(), innerClassSeparator);
    return result.toString();
  }

  public static void rawTypeToString(StringBuilder result, TypeElement type,
      char innerClassSeparator) {
    String packageName = getPackage(type).getQualifiedName().toString();
    String qualifiedName = type.getQualifiedName().toString();
    if (packageName.isEmpty()) {
      result.append(qualifiedName.replace('.', innerClassSeparator));
    } else {
      result.append(packageName);
      result.append('.');
      result.append(
          qualifiedName.substring(packageName.length() + 1).replace('.', innerClassSeparator));
    }
  }

  public static PackageElement getPackage(Element type) {
    while (type.getKind() != ElementKind.PACKAGE) {
      type = type.getEnclosingElement();
    }
    return (PackageElement) type;
  }

  public static TypeName getTypeAdapterType(Types typeUtil, DeclaredType typeAdapter) {
    TypeName typeAdapterTypeName = TypeName.get(TypeAdapter.class);
    List<? extends TypeMirror> interfaces =
        ((TypeElement) typeUtil.asElement(typeAdapter)).getInterfaces();
    for (TypeMirror intf : interfaces) {
      TypeName typeName = TypeName.get(intf);
      if (typeName instanceof ParameterizedTypeName) {
        ParameterizedTypeName paramTypeName = (ParameterizedTypeName) typeName;
        if (paramTypeName.rawType.equals(typeAdapterTypeName)) {
          return paramTypeName.typeArguments.get(0);
        }
      }
    }
    return null;
  }

  /**
   * A singleton is defined by a class with a public static final field named "INSTANCE" with a type
   * assignable from
   * the class itself
   *
   * @param typeUtils Type utils
   * @param el The data class
   * @return true if the class is a singleton, false otherwise
   */
  public static boolean isSingleton(Types typeUtils, TypeElement el) {
    List<? extends Element> enclosedElements = el.getEnclosedElements();
    for (Element e : enclosedElements) {
      Set<Modifier> modifiers = e.getModifiers();
      if (e instanceof VariableElement
          && modifiers.contains(STATIC)
          && modifiers.contains(PUBLIC)
          && modifiers.contains(FINAL)) {
        VariableElement variableElement = (VariableElement) e;
        if (variableElement.getSimpleName().contentEquals("INSTANCE")) {
          return typeUtils.isAssignable(el.asType(), e.asType());
        }
      }
    }
    return false;
  }
}
