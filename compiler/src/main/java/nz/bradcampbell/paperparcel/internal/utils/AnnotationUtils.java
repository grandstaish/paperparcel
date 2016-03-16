package nz.bradcampbell.paperparcel.internal.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

public class AnnotationUtils {
  private static final String NON_NULL_ANNOTATION_NAME = "NonNull";
  private static final String NOT_NULL_ANNOTATION_NAME = "NotNull";

  private static final AnnotationValueVisitor<Object, Void> VALUE_EXTRACTOR =
      new SimpleAnnotationValueVisitor6<Object, Void>() {
        @Override public Object visitString(String s, Void p) {
          if ("<error>".equals(s)) {
            throw new RuntimeException("Unknown type returned as <error>.");
          } else if ("<any>".equals(s)) {
            throw new RuntimeException("Unknown type returned as <any>.");
          }
          return s;
        }
        @Override public Object visitType(TypeMirror t, Void p) {
          return t;
        }
        @Override protected Object defaultAction(Object o, Void v) {
          return o;
        }
        @Override public Object visitArray(List<? extends AnnotationValue> values, Void v) {
          Object[] result = new Object[values.size()];
          for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).accept(this, null);
          }
          return result;
        }
      };

  public static boolean hasAnnotationWithName(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isFieldRequired(Element element) {
    return hasAnnotationWithName(element, NOT_NULL_ANNOTATION_NAME)
           || hasAnnotationWithName(element, NON_NULL_ANNOTATION_NAME);
  }

  /**
   * Returns the annotation on {@code element} formatted as a Map. This returns
   * a Map rather than an instance of the annotation interface to work-around
   * the fact that Class and Class[] fields won't work at code generation time.
   * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5089128
   */
  public static Map<String, Object> getAnnotation(Class<?> annotationType, Element element) {
    for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
      if (!TypeUtils.rawTypeToString(annotation.getAnnotationType(), '$')
          .equals(annotationType.getName())) {
        continue;
      }

      Map<String, Object> result = new LinkedHashMap<>();
      for (Method m : annotationType.getMethods()) {
        result.put(m.getName(), m.getDefaultValue());
      }
      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e
          : annotation.getElementValues().entrySet()) {
        String name = e.getKey().getSimpleName().toString();
        Object value = e.getValue().accept(VALUE_EXTRACTOR, null);
        Object defaultValue = result.get(name);
        if (!lenientIsInstance(defaultValue.getClass(), value)) {
          throw new IllegalStateException(String.format(
              "Value of %s.%s is a %s but expected a %s\n    value: %s",
              annotationType, name, value.getClass().getName(), defaultValue.getClass().getName(),
              value instanceof Object[] ? Arrays.toString((Object[]) value) : value));
        }
        result.put(name, value);
      }
      return result;
    }
    return null; // Annotation not found.
  }

  /**
   * Returns true if {@code value} can be assigned to {@code expectedClass}.
   * Like {@link Class#isInstance} but more lenient for {@code Class<?>} values.
   */
  private static boolean lenientIsInstance(Class<?> expectedClass, Object value) {
    if (expectedClass.isArray()) {
      Class<?> componentType = expectedClass.getComponentType();
      if (!(value instanceof Object[])) {
        return false;
      }
      for (Object element : (Object[]) value) {
        if (!lenientIsInstance(componentType, element)) return false;
      }
      return true;
    } else if (expectedClass == Class.class) {
      return value instanceof TypeMirror;
    } else {
      return expectedClass == value.getClass();
    }
  }
}
