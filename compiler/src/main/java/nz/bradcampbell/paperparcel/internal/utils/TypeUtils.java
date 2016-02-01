package nz.bradcampbell.paperparcel.internal.utils;

import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

public class TypeUtils {
  private TypeUtils() {
    // No instances.
  }

  public static String generateWrappedTypeName(TypeElement typeElement, TypeMirror typeMirror) {
    String innerHash = "";

    // Add a hashcode of the full string type name in between "{ClassName}" and "Parcel"
    if (hasTypeArguments(typeMirror)) {
      StringBuilder sb = new StringBuilder();
      typeToString(typeMirror, sb, '$');
      String typeString = sb.toString();
      innerHash = Long.toString(typeString.hashCode()).replace('-', '_');
    }

    return typeElement.getSimpleName().toString() + innerHash + "Parcel";
  }

  public static boolean hasTypeArguments(TypeMirror type) {
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

  public static void rawTypeToString(StringBuilder result, TypeElement type, char innerClassSeparator) {
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

  public static String getPackageName(TypeElement type) {
    return getPackage(type).getQualifiedName().toString();
  }

  public static PackageElement getPackage(Element type) {
    while (type.getKind() != ElementKind.PACKAGE) {
      type = type.getEnclosingElement();
    }
    return (PackageElement) type;
  }

  /**
   * Gets a list of all non-static member variables of a TypeElement
   *
   * @param el The data class
   * @return A list of non-static member variables. Cannot be null.
   */
  public static List<VariableElement> getFields(Types typeUtils, TypeElement el) {
    List<? extends Element> enclosedElements = el.getEnclosedElements();
    List<VariableElement> variables = new ArrayList<>();
    for (Element e : enclosedElements) {
      if (e instanceof VariableElement && !e.getModifiers().contains(STATIC)) {
        variables.add((VariableElement) e);
      }
    }
    TypeMirror superType = el.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      variables.addAll(getFields(typeUtils, (TypeElement) typeUtils.asElement(superType)));
    }
    return variables;
  }

  /**
   * Appends a string for {@code type} to {@code result}. Primitive types are
   * always boxed.
   *
   * @param innerClassSeparator either '.' or '$', which will appear in a
   *     class name like "java.lang.Map.Entry" or "java.lang.Map$Entry".
   *     Use '.' for references to existing types in code. Use '$' to define new
   *     class names and for strings that will be used by runtime reflection.
   */
  public static void typeToString(final TypeMirror type, final StringBuilder result, final char innerClassSeparator) {
    type.accept(new SimpleTypeVisitor6<Void, Void>() {
      @Override public Void visitDeclared(DeclaredType declaredType, Void v) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        rawTypeToString(result, typeElement, innerClassSeparator);
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (!typeArguments.isEmpty()) {
          result.append("<");
          for (int i = 0; i < typeArguments.size(); i++) {
            if (i != 0) {
              result.append(", ");
            }
            typeToString(typeArguments.get(i), result, innerClassSeparator);
          }
          result.append(">");
        }
        return null;
      }
      @Override public Void visitPrimitive(PrimitiveType primitiveType, Void v) {
        result.append(box((PrimitiveType) type));
        return null;
      }
      @Override public Void visitArray(ArrayType arrayType, Void v) {
        TypeMirror type = arrayType.getComponentType();
        if (type instanceof PrimitiveType) {
          result.append(type.toString()); // Don't box, since this is an array.
        } else {
          typeToString(arrayType.getComponentType(), result, innerClassSeparator);
        }
        result.append("[]");
        return null;
      }
      @Override public Void visitTypeVariable(TypeVariable typeVariable, Void v) {
        result.append(typeVariable.asElement().getSimpleName());
        return null;
      }
      @Override public Void visitError(ErrorType errorType, Void v) {
        // Error type found, a type may not yet have been generated, but we need the type
        // so we can generate the correct code in anticipation of the type being available
        // to the compiler.

        // Paramterized types which don't exist are returned as an error type whose name is "<any>"
        if ("<any>".equals(errorType.toString())) {
          throw new RuntimeException(
              "Type reported as <any> is likely a not-yet generated parameterized type.");
        }
        result.append(errorType.toString());
        return null;
      }
      @Override protected Void defaultAction(TypeMirror typeMirror, Void v) {
        throw new UnsupportedOperationException(
            "Unexpected TypeKind " + typeMirror.getKind() + " for "  + typeMirror);
      }
    }, null);
  }

  static TypeName box(PrimitiveType primitiveType) {
    switch (primitiveType.getKind()) {
      case BYTE:
        return ClassName.get(Byte.class);
      case SHORT:
        return ClassName.get(Short.class);
      case INT:
        return ClassName.get(Integer.class);
      case LONG:
        return ClassName.get(Long.class);
      case FLOAT:
        return ClassName.get(Float.class);
      case DOUBLE:
        return ClassName.get(Double.class);
      case BOOLEAN:
        return ClassName.get(Boolean.class);
      case CHAR:
        return ClassName.get(Character.class);
      case VOID:
        return ClassName.get(Void.class);
      default:
        throw new AssertionError();
    }
  }
}
