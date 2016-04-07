package nz.bradcampbell.paperparcel;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

public class FieldExtractor {
  private Types typeUtil;

  public FieldExtractor(Types typeUtil) {
    this.typeUtil = typeUtil;
  }

  public List<? extends VariableElement> requiredFields(TypeElement typeElement) {
    // Get all members
    List<VariableElement> fieldElements = getFields(typeUtil, typeElement);

    // Get constructor
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
    constructors = filterNonVisibleElements(constructors);
    constructors = filterExecutableElementsOfSize(constructors, fieldElements.size());
    if (constructors.size() == 0) {
      throw new IllegalStateException("Could not find an appropriate constructor on " + typeElement.toString() + ". " +
                                      "There were " + fieldElements.size() + " member variables, but no visible " +
                                      "constructors with that many elements.");
    }
    if (constructors.size() > 1) {
      throw new IllegalStateException(typeElement.toString() + " has more than one valid constructor. PaperParcel " +
                                      "requires only one constructor.");
    }
    ExecutableElement primaryConstructor = constructors.get(0);

    return getOrderedVariables(primaryConstructor, fieldElements, typeElement);
  }

  private static List<VariableElement> getFields(Types typeUtils, TypeElement el) {
    List<? extends Element> enclosedElements = el.getEnclosedElements();
    List<VariableElement> variables = new ArrayList<>();
    for (Element e : enclosedElements) {
      Set<Modifier> modifiers = e.getModifiers();
      if (e instanceof VariableElement && !modifiers.contains(STATIC) && !modifiers.contains(TRANSIENT)) {
        variables.add((VariableElement) e);
      }
    }
    TypeMirror superType = el.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      variables.addAll(getFields(typeUtils, (TypeElement) typeUtils.asElement(superType)));
    }
    return variables;
  }

  private List<VariableElement> getOrderedVariables(
      ExecutableElement constructor, List<VariableElement> fieldElements, TypeElement typeElement) {

    List<? extends VariableElement> params = constructor.getParameters();

    // Attempt to match constructor params by name
    Map<Name, VariableElement> fieldNamesToFieldMap = new HashMap<>(fieldElements.size());
    for (VariableElement field : fieldElements) {
      fieldNamesToFieldMap.put(field.getSimpleName(), field);
    }
    boolean canUseConstructorArguments = true;
    List<VariableElement> orderedFields = new ArrayList<>(fieldElements.size());
    for (VariableElement param : params) {
      VariableElement field = fieldNamesToFieldMap.get(param.getSimpleName());
      if (field == null) {
        canUseConstructorArguments = false;
        break;
      }
      orderedFields.add(field);
    }
    if (canUseConstructorArguments) {
      return orderedFields;
    }

    // Attempt to match constructor params by order (support for https://youtrack.jetbrains.com/issue/KT-9609)
    boolean areParametersInOrder = true;
    for (int i = 0; i < params.size(); i++) {
      VariableElement param = params.get(i);
      VariableElement field = fieldElements.get(i);
      if (!typeUtil.isAssignable(field.asType(), param.asType())) {
        areParametersInOrder = false;
        break;
      }
    }
    if (areParametersInOrder) {
      return fieldElements;
    }

    throw new NoValidConstructorFoundException(
        "No valid constructor found while processing " + typeElement.getQualifiedName() + ". The constructor parameters" +
        " and member variables need to be in the same order.");
  }

  private static <T extends ExecutableElement> List<T> filterExecutableElementsOfSize(List<T> list, int expectedSize) {
    ArrayList<T> filteredList = new ArrayList<>(list.size());
    for (T e : list) {
      if (e.getParameters().size() == expectedSize) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  private static <T extends Element> List<T> filterNonVisibleElements(List<T> list) {
    ArrayList<T> filteredList = new ArrayList<>(list.size());
    for (T e : list) {
      Set<Modifier> modifiers = e.getModifiers();
      if (!modifiers.contains(PRIVATE) && !modifiers.contains(PROTECTED)) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  static class NoValidConstructorFoundException extends IllegalStateException {
    NoValidConstructorFoundException(String message) {
      super(message);
    }
  }

}
