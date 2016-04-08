package nz.bradcampbell.paperparcel;

import static com.google.auto.common.MoreElements.getLocalAndInheritedMethods;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.util.ElementFilter.constructorsIn;

import nz.bradcampbell.paperparcel.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class DataClassValidator {
  private final ProcessingEnvironment processingEnv;
  private final Types typeUtil;

  private List<? extends VariableElement> fields;
  private InitializationStrategy initializationStrategy;

  public DataClassValidator(ProcessingEnvironment processingEnv, Types typeUtil) {
    this.processingEnv = processingEnv;
    this.typeUtil = typeUtil;
  }

  public List<? extends VariableElement> getFields() {
    return fields;
  }

  public InitializationStrategy getInitializationStrategy() {
    return initializationStrategy;
  }

  public void validate(TypeElement typeElement) throws IncompatibleTypeException {
    List<VariableElement> fieldElements = getFields(typeUtil, typeElement);
    List<ExecutableElement> allConstructors, visibleConstructors;

    // Attempt to find a constructor that could be used to instantiate the object with all of the required
    // fields
    List<ExecutableElement> constructors = allConstructors = constructorsIn(typeElement.getEnclosedElements());
    // Must not be private or protected
    constructors = visibleConstructors = filterNonVisibleElements(constructors);
    // Must have a parameter for all required variables
    constructors = filterExecutableElementsOfSize(constructors, fieldElements.size());

    boolean useConstructor = false;
    if (constructors.size() > 0) {
      for (ExecutableElement constructor : constructors) {
        try {
          fieldElements = getOrderedVariables(constructor, fieldElements);
          useConstructor = true;
          break;
        } catch (InvalidConstructorException e) {
          // Invalid constructor, try next
        }
      }
    }

    if (useConstructor) {
      this.fields = fieldElements;
      this.initializationStrategy = InitializationStrategy.CONSTRUCTOR;
      return;
    }

    // Could not find a constructor to use. Next, we'll try to find setter methods for all of the required
    // fields
    boolean useSetters = true;

    Iterable<ExecutableElement> methods = getLocalAndInheritedMethods(typeElement, processingEnv.getElementUtils());
    // Must not be private or protected
    methods = filterNonVisibleElements(methods);
    // Must have 1 parameter
    methods = filterExecutableElementsOfSize(methods, 1);

    for (VariableElement field : fieldElements) {
      ExecutableElement setter = findSetterForField(field, methods);
      if (setter == null) {
        useSetters = false;
        break;
      }
    }

    if (!useSetters) {
      throw new IncompatibleTypeException(typeElement);
    }

    // Ensure there is an empty constructor
    if (allConstructors.size() > 0 && filterExecutableElementsOfSize(visibleConstructors, 0).size() == 0) {
      throw new IncompatibleTypeException(typeElement);
    }

    this.fields = fieldElements;
    this.initializationStrategy = InitializationStrategy.SETTER;
  }

  private ExecutableElement findSetterForField(VariableElement field, Iterable<ExecutableElement> methods) {
    String setterName = "set" + StringUtils.capitalizeFirstCharacter(field.getSimpleName().toString());
    TypeMirror fieldType = field.asType();

    for (ExecutableElement method : methods) {

      // The parameter type must be assignable from the field type
      TypeMirror parameterType = method.getParameters().get(0).asType();
      if (processingEnv.getTypeUtils().isAssignable(parameterType, fieldType)) {

        // Must be named setX(...), where x is the property name
        if (method.getSimpleName().contentEquals(setterName)) {

          // Setter found.
          return method;
        }
      }
    }

    return null;
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

  private List<VariableElement> getOrderedVariables(ExecutableElement constructor, List<VariableElement> fieldElements)
      throws InvalidConstructorException {

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

    throw new InvalidConstructorException();
  }

  private static <T extends ExecutableElement> List<T> filterExecutableElementsOfSize(Iterable<T> list,
                                                                                      int expectedSize) {
    ArrayList<T> filteredList = new ArrayList<>();
    for (T e : list) {
      if (e.getParameters().size() == expectedSize) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  private static <T extends Element> List<T> filterNonVisibleElements(Iterable<T> list) {
    ArrayList<T> filteredList = new ArrayList<>();
    for (T e : list) {
      Set<Modifier> modifiers = e.getModifiers();
      if (!modifiers.contains(PRIVATE) && !modifiers.contains(PROTECTED)) {
        filteredList.add(e);
      }
    }
    return filteredList;
  }

  static class InvalidConstructorException extends Exception {
  }

  static class IncompatibleTypeException extends Exception {
    private TypeElement type;
    public IncompatibleTypeException(TypeElement type) {
      super(type.toString());
      this.type = type;
    }
    public TypeElement getType() {
      return type;
    }
  }
}
