package nz.bradcampbell.paperparcel;

import com.google.auto.common.Visibility;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.io.File;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import nz.bradcampbell.paperparcel.FieldMatcher.AnyAnnotation;
import nz.bradcampbell.paperparcel.FieldMatcher.AnyClass;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.DataClass;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.model.properties.ArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.BigDecimalProperty;
import nz.bradcampbell.paperparcel.model.properties.BigIntegerProperty;
import nz.bradcampbell.paperparcel.model.properties.BooleanArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.BooleanProperty;
import nz.bradcampbell.paperparcel.model.properties.BundleProperty;
import nz.bradcampbell.paperparcel.model.properties.ByteArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.ByteProperty;
import nz.bradcampbell.paperparcel.model.properties.CharProperty;
import nz.bradcampbell.paperparcel.model.properties.CharSequenceProperty;
import nz.bradcampbell.paperparcel.model.properties.DateProperty;
import nz.bradcampbell.paperparcel.model.properties.DoubleProperty;
import nz.bradcampbell.paperparcel.model.properties.EnumProperty;
import nz.bradcampbell.paperparcel.model.properties.FileProperty;
import nz.bradcampbell.paperparcel.model.properties.FloatProperty;
import nz.bradcampbell.paperparcel.model.properties.IBinderProperty;
import nz.bradcampbell.paperparcel.model.properties.IntArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.IntProperty;
import nz.bradcampbell.paperparcel.model.properties.ListProperty;
import nz.bradcampbell.paperparcel.model.properties.LongArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.LongProperty;
import nz.bradcampbell.paperparcel.model.properties.MapProperty;
import nz.bradcampbell.paperparcel.model.properties.MathContextProperty;
import nz.bradcampbell.paperparcel.model.properties.ParcelableProperty;
import nz.bradcampbell.paperparcel.model.properties.PatternProperty;
import nz.bradcampbell.paperparcel.model.properties.PersistableBundleProperty;
import nz.bradcampbell.paperparcel.model.properties.SetProperty;
import nz.bradcampbell.paperparcel.model.properties.ShortProperty;
import nz.bradcampbell.paperparcel.model.properties.SizeFProperty;
import nz.bradcampbell.paperparcel.model.properties.SizeProperty;
import nz.bradcampbell.paperparcel.model.properties.SparseArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.SparseBooleanArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.StackTraceElementProperty;
import nz.bradcampbell.paperparcel.model.properties.StringArrayProperty;
import nz.bradcampbell.paperparcel.model.properties.StringProperty;
import nz.bradcampbell.paperparcel.model.properties.TypeAdapterProperty;
import nz.bradcampbell.paperparcel.model.properties.UriProperty;
import nz.bradcampbell.paperparcel.model.properties.UrlProperty;
import nz.bradcampbell.paperparcel.model.properties.UuidProperty;
import nz.bradcampbell.paperparcel.model.properties.WrapperProperty;
import nz.bradcampbell.paperparcel.utils.AnnotationUtils;
import nz.bradcampbell.paperparcel.utils.StringUtils;
import nz.bradcampbell.paperparcel.utils.TypeUtils;

import static com.google.auto.common.MoreElements.getLocalAndInheritedMethods;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.OBJECT;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static nz.bradcampbell.paperparcel.FieldMatcher.ANY_NAME;
import static nz.bradcampbell.paperparcel.utils.AnnotationUtils.getAnnotation;
import static nz.bradcampbell.paperparcel.utils.AnnotationUtils.isFieldRequired;
import static nz.bradcampbell.paperparcel.utils.StringUtils.startsWithVowel;
import static nz.bradcampbell.paperparcel.utils.TypeUtils.isSingleton;

public class DataClassParser {
  public static final TypeName STRING = ClassName.get("java.lang", "String");
  public static final TypeName MAP = ClassName.get("java.util", "Map");
  public static final TypeName LIST = ClassName.get("java.util", "List");
  public static final TypeName SET = ClassName.get("java.util", "Set");
  public static final TypeName BOOLEAN_ARRAY = ArrayTypeName.of(boolean.class);
  public static final TypeName BYTE_ARRAY = ArrayTypeName.of(byte.class);
  public static final TypeName INT_ARRAY = ArrayTypeName.of(int.class);
  public static final TypeName LONG_ARRAY = ArrayTypeName.of(long.class);
  public static final TypeName STRING_ARRAY = ArrayTypeName.of(String.class);
  public static final TypeName SPARSE_ARRAY = ClassName.get("android.util", "SparseArray");
  public static final TypeName SPARSE_BOOLEAN_ARRAY =
      ClassName.get("android.util", "SparseBooleanArray");
  public static final TypeName BUNDLE = ClassName.get("android.os", "Bundle");
  public static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable");
  public static final TypeName PARCELABLE_ARRAY = ArrayTypeName.of(PARCELABLE);
  public static final TypeName CHAR_SEQUENCE = ClassName.get("java.lang", "CharSequence");
  public static final TypeName IBINDER = ClassName.get("android.os", "IBinder");
  public static final TypeName OBJECT_ARRAY = ArrayTypeName.of(OBJECT);
  public static final TypeName PERSISTABLE_BUNDLE =
      ClassName.get("android.os", "PersistableBundle");
  public static final TypeName SIZE = ClassName.get("android.util", "Size");
  public static final TypeName SIZEF = ClassName.get("android.util", "SizeF");
  public static final TypeName ENUM = ClassName.get("java.lang", "Enum");
  public static final TypeName BOXED_INT = INT.box();
  public static final TypeName BOXED_LONG = LONG.box();
  public static final TypeName BOXED_BYTE = BYTE.box();
  public static final TypeName BOXED_BOOLEAN = BOOLEAN.box();
  public static final TypeName BOXED_FLOAT = FLOAT.box();
  public static final TypeName BOXED_CHAR = CHAR.box();
  public static final TypeName BOXED_DOUBLE = DOUBLE.box();
  public static final TypeName BOXED_SHORT = SHORT.box();
  public static final TypeName TYPE_ADAPTER = TypeName.get(TypeAdapter.class);
  public static final TypeName DATE = TypeName.get(Date.class);
  public static final TypeName BIG_INTEGER = TypeName.get(BigInteger.class);
  public static final TypeName BIG_DECIMAL = TypeName.get(BigDecimal.class);
  public static final TypeName MATH_CONTEXT = TypeName.get(MathContext.class);
  public static final TypeName UUID = ClassName.get("java.util", "UUID");
  public static final TypeName FILE = TypeName.get(File.class);
  public static final TypeName STACK_TRACE_ELEMENT = TypeName.get(StackTraceElement.class);
  public static final TypeName URL = ClassName.get("java.net", "URL");
  public static final TypeName URI = ClassName.get("java.net", "URI");
  public static final TypeName PATTERN = TypeName.get(Pattern.class);

  private static final Set<TypeName> VALID_TYPES =
      ImmutableSet.of(STRING, MAP, LIST, SET, BOOLEAN_ARRAY, BYTE_ARRAY, INT_ARRAY, LONG_ARRAY,
          STRING_ARRAY, SPARSE_ARRAY, SPARSE_BOOLEAN_ARRAY, BUNDLE, PARCELABLE, PARCELABLE_ARRAY,
          CHAR_SEQUENCE, IBINDER, OBJECT_ARRAY, PERSISTABLE_BUNDLE, SIZE, SIZEF, ENUM, INT,
          BOXED_INT, LONG, BOXED_LONG, BYTE, BOXED_BYTE, BOOLEAN, BOXED_BOOLEAN, FLOAT, BOXED_FLOAT,
          CHAR, BOXED_CHAR, DOUBLE, BOXED_DOUBLE, SHORT, BOXED_SHORT, TYPE_ADAPTER, DATE,
          BIG_INTEGER, BIG_DECIMAL, MATH_CONTEXT, UUID, FILE, STACK_TRACE_ELEMENT, URL, URI,
          PATTERN);

  private static final Pattern KT_9609_BUG_NAME_FORMAT = Pattern.compile("arg(\\d+)");

  private final ProcessingEnvironment processingEnv;
  private final Types typeUtil;
  private final Elements elementUtils;

  private final Map<TypeName, Adapter> defaultAdapters;
  private final Map<ClassName, ClassName> wrappers;
  private final Map<ClassName, ClassName> delegates;

  public DataClassParser(ProcessingEnvironment processingEnv,
      Map<TypeName, Adapter> defaultAdapters, Map<ClassName, ClassName> wrappers,
      Map<ClassName, ClassName> delegates) {
    this.processingEnv = processingEnv;
    this.delegates = delegates;
    this.typeUtil = processingEnv.getTypeUtils();
    this.elementUtils = processingEnv.getElementUtils();
    this.defaultAdapters = defaultAdapters;
    this.wrappers = wrappers;
  }

  public Set<DataClass> parseDataClasses(Set<TypeElement> unprocessedTypes, boolean isLastRound) {
    Set<DataClass> dataClasses = new LinkedHashSet<>();
    for (Iterator<TypeElement> iterator = unprocessedTypes.iterator(); iterator.hasNext(); ) {
      TypeElement element = iterator.next();
      DataClass dataClass;
      try {
        dataClass = createParcel(element);
        dataClasses.add(dataClass);
        iterator.remove();
      } catch (UnknownPropertyTypeException e) {
        // Only log an error in the last round as the unknown type might
        // be known in a later processing round
        if (isLastRound) {
          String aOrAn = startsWithVowel(e.element.asType().toString()) ? "an " : "a ";
          processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR,
                  "PaperParcel does not know how to process "
                      + element.toString()
                      + " because the "
                      + e.element.toString()
                      + " field is "
                      + aOrAn
                      +  e.element.asType().toString()
                      + " and "
                      + e.unknownType.toString()
                      + " is not a supported PaperParcel type. Define a TypeAdapter<"
                      + e.unknownType.toString()
                      + "> to add support for "
                      + e.unknownType.toString()
                      + " objects. Alternatively you can exclude the field by making it static,"
                      + " transient, or using the ExcludeFields annotation on "
                      + element.toString(),
                  e.element);
        }
      } catch (NonReadablePropertyException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "PaperParcel cannot read from the field named \""
                    + e.element.toString()
                    + "\" which was found when processing "
                    + element.toString()
                    + ". The field must either be non-private, or have a getter method with no"
                    + " arguments and have one of the following names: "
                    + possibleGetterNames(e.element.toString())
                    + ". Alternatively you can exclude the field by making it static, transient,"
                    + " or using the ExcludeFields annotation on "
                    + element.toString(),
                e.element);
      } catch (NonWritablePropertyException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "PaperParcel cannot write to the field named \""
                    + e.element.toString()
                    + "\" which was found when processing "
                    + element.toString()
                    + ". The field must either be have a constructor argument named "
                    + e.element.toString()
                    + ", be non-private, or have a setter method with one "
                    + e.element.asType().toString()
                    + " parameter and have one of the following names: "
                    + possibleSetterNames(e.element.toString())
                    + ". Alternatively you can exclude the field by making it static, transient,"
                    + " or using the ExcludeFields annotation on "
                    + element.toString(),
                e.element);
      } catch (NoVisibleConstructorException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "PaperParcel requires at least one non-private constructor, but could not find "
                    + "one in " + element.toString(),
                element);
      } catch (UnsatisfiableConstructorException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "PaperParcel cannot satisfy constructor "
                + e.constructor
                + ". PaperParcel was able to find "
                + e.fieldsToPassToConstructorCount
                + " arguments, but needed "
                + e.constructor.getParameters().size()
                + ". The missing arguments were "
                + e.missingArguments,
                e.constructor);
      } catch (DuplicateFieldNameException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "PaperParcel cannot process "
                + element.toString()
                + " because it has two non-ignored fields named \""
                + e.first.toString()
                + "\". The first can be found in "
                + e.first.getEnclosingElement().toString()
                + " and the second can be found in "
                + e.second.getEnclosingElement().toString(),
                e.first);
      }
    }
    return dataClasses;
  }

  /**
   * Create a Parcel wrapper for the given data class
   *
   * @param typeElement The data class
   */
  private DataClass createParcel(TypeElement typeElement)
      throws UnknownPropertyTypeException, NonReadablePropertyException,
      NonWritablePropertyException, NoVisibleConstructorException,
      UnsatisfiableConstructorException, DuplicateFieldNameException {
    ClassName className = ClassName.get(typeElement);
    ClassName wrappedClassName = wrappers.get(className);
    ClassName delegateClassName = delegates.get(className);

    List<Property> properties = new ArrayList<>();
    Set<Adapter> requiredTypeAdapters = new HashSet<>();
    Map<TypeName, Adapter> typeAdapters = new HashMap<>();

    boolean requiresClassLoader = false;
    boolean isSingleton = isSingleton(typeUtil, typeElement);

    // If the class is a singleton, we don't need to read/write variables. We can just use
    // the static instance.
    if (!isSingleton) {

      // Override the type adapters with the current element preferences
      applyTypeAdapters(getLocalOrInheritedAnnotationAsMap(TypeAdapters.class, typeElement),
          typeAdapters);

      // Get exclusions
      List<FieldMatcher> fieldMatchers = getFieldMatchers(
          getLocalOrInheritedAnnotation(ExcludeFields.class, typeElement));

      for (FieldInfo fieldInfo : getValidFields(typeElement, fieldMatchers)) {

        // Get the variable scoped type adapters
        Map<TypeName, Adapter> variableScopedTypeAdapters = new LinkedHashMap<>(typeAdapters);
        Map<String, Object> annotation = getAnnotation(TypeAdapters.class, fieldInfo.element);
        if (annotation == null) {
          annotation = getAnnotation(TypeAdapters.class, fieldInfo.getterMethod);
        }
        applyTypeAdapters(annotation, variableScopedTypeAdapters);

        String name = fieldInfo.element.getSimpleName().toString();

        // A field is considered "nullable" when it is a non-primitive and not annotated
        // with @NonNull or @NotNull
        boolean isPrimitive = fieldInfo.element.asType().getKind().isPrimitive();
        boolean annotatedWithNonNull =
            isFieldRequired(fieldInfo.getterMethod) || isFieldRequired(fieldInfo.element);
        boolean isNullable = !isPrimitive && !annotatedWithNonNull;

        // Parse the property 
        Property property;
        try {
          TypeMirror type = fieldInfo.element.asType();
          property = parseProperty(type, typeElement.asType(), isNullable, name,
              variableScopedTypeAdapters);
        } catch (UnknownTypeException e) {
          throw new UnknownPropertyTypeException(fieldInfo.element, e.unknownType);
        }

        requiredTypeAdapters.addAll(property.requiredTypeAdapters());
        requiresClassLoader |= property.requiresClassLoader();

        String setterMethodName = null;
        if (fieldInfo.setterMethod != null) {
          setterMethodName = fieldInfo.setterMethod.getSimpleName().toString();
        }

        String getterMethodName = null;
        if (fieldInfo.getterMethod != null) {
          getterMethodName = fieldInfo.getterMethod.getSimpleName().toString();
        }

        property.setVisible(fieldInfo.visible);
        property.setConstructorPosition(fieldInfo.constructorIndex);
        property.setSetterMethodName(setterMethodName);
        property.setGetterMethodName(getterMethodName);

        properties.add(property);
      }
    }

    return new DataClass(properties, className.packageName(), wrappedClassName, className,
        delegateClassName, requiresClassLoader, requiredTypeAdapters, isSingleton);
  }

  private List<FieldInfo> getValidFields(final TypeElement typeElement,
      final List<FieldMatcher> fieldMatchers)
      throws NonReadablePropertyException, NonWritablePropertyException,
      NoVisibleConstructorException, UnsatisfiableConstructorException,
      DuplicateFieldNameException {
    final ImmutableSet<ExecutableElement> methods =
        getLocalAndInheritedMethods(typeElement, elementUtils);

    final FluentIterable<FieldInfo> readableFields =
        FluentIterable.from(getLocalAndInheritedFields(typeElement))
            .filter(new Predicate<VariableElement>() {
              @Override public boolean apply(VariableElement input) {
                Set<Modifier> modifiers = input.getModifiers();
                return !modifiers.contains(STATIC) && !modifiers.contains(TRANSIENT);
              }
            })
            .filter(new Predicate<VariableElement>() {
              @Override public boolean apply(VariableElement input) {
                // Remove field immediately if annotated with @ExcludeField
                if (AnnotationUtils.hasAnnotation(input, ExcludeField.class)) {
                  return false;
                }
                // Remove all fields that match the rules set in each the field matchers
                for (FieldMatcher fieldMatcher : fieldMatchers) {
                  boolean matches = fieldMatcher.name().equals(ANY_NAME)
                      || input.getSimpleName().contentEquals(fieldMatcher.name());

                  TypeMirror declaring = null;
                  try {
                    fieldMatcher.declaringClass();
                  } catch (MirroredTypeException mte) {
                    declaring = mte.getTypeMirror();
                  }
                  assert declaring != null;
                  matches &= AnyClass.class.getCanonicalName().equals(declaring.toString())
                      || declaring.toString().equals(input.getEnclosingElement().toString());

                  TypeMirror type = null;
                  try {
                    fieldMatcher.type();
                  } catch (MirroredTypeException mte) {
                    type = mte.getTypeMirror();
                  }
                  assert type != null;
                  matches &= AnyClass.class.getCanonicalName().equals(type.toString())
                      || type.toString().equals(input.asType().toString());

                  TypeMirror annotation = null;
                  try {
                    fieldMatcher.annotation();
                  } catch (MirroredTypeException mte) {
                    annotation = mte.getTypeMirror();
                  }
                  assert annotation != null;
                  matches &= AnyAnnotation.class.getCanonicalName().equals(annotation.toString())
                      || AnnotationUtils.hasAnnotation(input, annotation);

                  if (matches) {
                    return false;
                  }
                }
                // Keep the remaining fields
                return true;
              }
            })
            .transform(new Function<VariableElement, FieldInfo>() {
              @Override public FieldInfo apply(final VariableElement field) {
                boolean visible = Visibility.ofElement(field) != Visibility.PRIVATE;
                ExecutableElement getterMethod = getGetterMethod(methods, field);
                return new FieldInfo(visible, getterMethod, field);
              }
            });

    Ordering<ExecutableElement> mostParametersOrdering = new Ordering<ExecutableElement>() {
      @Override public int compare(ExecutableElement left, ExecutableElement right) {
        return Ints.compare(left.getParameters().size(), right.getParameters().size());
      }
    };

    FluentIterable<ExecutableElement> visibleConstructors =
        FluentIterable.from(constructorsIn(typeElement.getEnclosedElements()))
            .filter(new Predicate<ExecutableElement>() {
              @Override public boolean apply(ExecutableElement input) {
                return Visibility.ofElement(input) != Visibility.PRIVATE;
              }
            });

    if (visibleConstructors.size() == 0) {
      throw new NoVisibleConstructorException();
    }

    // Main constructor being the constructor with the most parameters
    final ExecutableElement mainConstructor = mostParametersOrdering.max(visibleConstructors);

    final FluentIterable<ParameterInfo> constructorParameterInfo =
        FluentIterable.from(mainConstructor.getParameters())
            .transform(new Function<VariableElement, ParameterInfo>() {
              @Override public ParameterInfo apply(VariableElement input) {
                String name = input.getSimpleName().toString();

                // Temporary workaround for https://youtrack.jetbrains.com/issue/KT-9609
                Matcher nameMatcher = KT_9609_BUG_NAME_FORMAT.matcher(name);
                if (nameMatcher.matches()) {
                  int offset = readableFields.size() - mainConstructor.getParameters().size();
                  int index = Integer.valueOf(nameMatcher.group(1)) + offset;
                  name = readableFields.get(index).element.getSimpleName().toString();
                }

                return new ParameterInfo(name, input.asType(), input);
              }
            });

    List<FieldInfo> result =
        readableFields.transform(new Function<FieldInfo, FieldInfo>() {
          @Override public FieldInfo apply(FieldInfo input) {
            ExecutableElement setterMethod = getSetterMethod(methods, input.element);
            VariableElement param =
                getConstructorParameter(constructorParameterInfo, input.element);
            int constructorIndex =
                param == null ? -1 : mainConstructor.getParameters().indexOf(param);
            return new FieldInfo(input.visible, input.getterMethod, input.element,
                setterMethod, constructorIndex);
          }
        }).toList();

    Map<String, VariableElement> nameToFieldMap = new HashMap<>();

    int mainConstructorSize = mainConstructor.getParameters().size();
    int fieldsToPassToConstructorCount = 0;
    VariableElement[] fieldsToPassToConstructor = new VariableElement[mainConstructorSize];

    for (FieldInfo fieldInfo : result) {
      // Ensure that the field can be read
      if (!fieldInfo.visible && fieldInfo.getterMethod == null) {
        throw new NonReadablePropertyException(fieldInfo.element);
      }
      // Ensure that the field can be written
      if (!fieldInfo.visible
          && fieldInfo.constructorIndex == -1
          && fieldInfo.setterMethod == null) {
        throw new NonWritablePropertyException(fieldInfo.element);
      }
      // Keep track of constructor arguments for later validation
      if (fieldInfo.constructorIndex != -1) {
        fieldsToPassToConstructor[fieldInfo.constructorIndex] = fieldInfo.element;
        fieldsToPassToConstructorCount++;
      }
      // Ensure there are no duplicate variable names. This can happen with inheritance.
      String variableName = fieldInfo.element.toString();
      if (nameToFieldMap.containsKey(variableName)) {
        throw new DuplicateFieldNameException(nameToFieldMap.get(variableName), fieldInfo.element);
      }
      nameToFieldMap.put(variableName, fieldInfo.element);
    }

    // Ensure that the main constructor can be satisfied
    int constructorArgumentSizeDifference = mainConstructorSize - fieldsToPassToConstructorCount;
    if (constructorArgumentSizeDifference > 0) {
      List<VariableElement> missingArguments = new ArrayList<>(constructorArgumentSizeDifference);
      List<? extends VariableElement> constructorArguments = mainConstructor.getParameters();
      for (int i = 0; i < constructorArguments.size(); i++) {
        if (fieldsToPassToConstructor[i] == null) {
          missingArguments.add(constructorArguments.get(i));
        }
      }
      throw new UnsatisfiableConstructorException(fieldsToPassToConstructorCount,
          mainConstructor, missingArguments);
    }

    return result;
  }

  private List<VariableElement> getLocalAndInheritedFields(TypeElement el) {
    List<VariableElement> variables = fieldsIn(el.getEnclosedElements());
    TypeMirror superType = el.getSuperclass();
    if (superType.getKind() != TypeKind.NONE) {
      variables.addAll(getLocalAndInheritedFields((TypeElement) typeUtil.asElement(superType)));
    }
    return variables;
  }

  private VariableElement getConstructorParameter(FluentIterable<ParameterInfo> parameterInfo,
      final VariableElement field) {
    return parameterInfo.filter(new Predicate<ParameterInfo>() {
      @Override public boolean apply(ParameterInfo input) {
        return input.name.equals(field.getSimpleName().toString()) && typeUtil.isAssignable(
            input.type, field.asType());
      }
    }).transform(new Function<ParameterInfo, VariableElement>() {
      @Override public VariableElement apply(ParameterInfo input) {
        return input.element;
      }
    }).first().orNull();
  }

  private ExecutableElement getSetterMethod(Set<ExecutableElement> methods,
      final VariableElement field) {
    final Set<String> possibleSetterNames = possibleSetterNames(field.getSimpleName().toString());
    return FluentIterable.from(methods).filter(new Predicate<ExecutableElement>() {
      @Override public boolean apply(ExecutableElement input) {
        return possibleSetterNames.contains(input.getSimpleName().toString());
      }
    }).filter(new Predicate<ExecutableElement>() {
      @Override public boolean apply(ExecutableElement input) {
        List<? extends VariableElement> parameters = input.getParameters();
        return parameters.size() == 1
            && typeUtil.isAssignable(parameters.get(0).asType(), field.asType());
      }
    }).first().orNull();
  }

  private ExecutableElement getGetterMethod(Set<ExecutableElement> methods,
      final VariableElement field) {
    final Set<String> possibleGetterNames = possibleGetterNames(field.getSimpleName().toString());
    return FluentIterable.from(methods).filter(new Predicate<ExecutableElement>() {
      @Override public boolean apply(ExecutableElement input) {
        return possibleGetterNames.contains(input.getSimpleName().toString())
            && input.getReturnType().toString().equals(field.asType().toString());
      }
    }).filter(new Predicate<ExecutableElement>() {
      @Override public boolean apply(ExecutableElement input) {
        return input.getParameters().size() == 0;
      }
    }).first().orNull();
  }

  private Set<String> possibleGetterNames(String name) {
    Set<String> possibleGetterNames = new LinkedHashSet<>(3);
    possibleGetterNames.add(name);
    possibleGetterNames.add("is" + StringUtils.capitalizeFirstCharacter(name));
    possibleGetterNames.add("has" + StringUtils.capitalizeFirstCharacter(name));
    possibleGetterNames.add("get" + StringUtils.capitalizeFirstCharacter(name));
    return possibleGetterNames;
  }

  private Set<String> possibleSetterNames(String name) {
    Set<String> possibleSetterNames = new LinkedHashSet<>(2);
    possibleSetterNames.add(name);
    possibleSetterNames.add("set" + StringUtils.capitalizeFirstCharacter(name));
    return possibleSetterNames;
  }

  private <T extends Annotation> T getLocalOrInheritedAnnotation(Class<T> annotationType,
      TypeElement element) {
    while (element != null) {
      T annotation = element.getAnnotation(annotationType);
      if (annotation != null) return annotation;
      element = (TypeElement) typeUtil.asElement(element.getSuperclass());
    }
    return null;
  }

  private Map<String, Object> getLocalOrInheritedAnnotationAsMap(Class<?> annotationType,
      TypeElement element) {
    while (element != null) {
      Map<String, Object> annotation = getAnnotation(annotationType, element);
      if (annotation != null) return annotation;
      element = (TypeElement) typeUtil.asElement(element.getSuperclass());
    }
    return null;
  }

  private List<FieldMatcher> getFieldMatchers(ExcludeFields annotation) {
    List<FieldMatcher> fieldMatchers = new ArrayList<>();
    if (annotation != null) {
      Collections.addAll(fieldMatchers, annotation.value());
    }
    return fieldMatchers;
  }

  private void applyTypeAdapters(Map<String, Object> annotation,
      Map<TypeName, Adapter> typeAdapters) {
    if (annotation != null) {
      Object[] typeAdaptersArray = (Object[]) annotation.get("value");
      for (Object o : typeAdaptersArray) {
        DeclaredType ta = (DeclaredType) o;
        TypeElement typeElement = (TypeElement) typeUtil.asElement(ta);
        TypeName typeAdapterType = TypeUtils.getTypeAdapterType(typeUtil, ta);
        boolean singleton = isSingleton(typeUtil, typeElement);
        typeAdapters.put(typeAdapterType, new Adapter(singleton, ClassName.get(typeElement)));
      }
    }
  }

  private Property parseProperty(TypeMirror variable, TypeMirror dataClass, boolean isNullable,
      String name, Map<TypeName, Adapter> typeAdapterMap) throws UnknownTypeException {

    variable = getActualTypeParameter(variable, dataClass);

    TypeMirror erasedType = typeUtil.erasure(variable);

    TypeName parcelableTypeName = getParcelableType(erasedType);

    TypeMirror type = variable;
    if (type instanceof WildcardType) {
      type = ((WildcardType) variable).getExtendsBound();
    }

    TypeName erasedTypeName = TypeName.get(erasedType);
    Adapter typeAdapter = typeAdapterMap.get(erasedTypeName);
    if (typeAdapter == null) {
      typeAdapter = defaultAdapters.get(erasedTypeName);
    }
    if (typeAdapter != null) {
      parcelableTypeName = TYPE_ADAPTER;
    }

    TypeName typeName = TypeName.get(variable);

    if (STRING.equals(parcelableTypeName)) {
      return new StringProperty(isNullable, typeName, name);
    } else if (INT.equals(parcelableTypeName)) {
      return new IntProperty(false, typeName, name);
    } else if (BOXED_INT.equals(parcelableTypeName)) {
      return new IntProperty(isNullable, typeName, name);
    } else if (LONG.equals(parcelableTypeName)) {
      return new LongProperty(false, typeName, name);
    } else if (BOXED_LONG.box().equals(parcelableTypeName)) {
      return new LongProperty(isNullable, typeName, name);
    } else if (BYTE.equals(parcelableTypeName)) {
      return new ByteProperty(false, typeName, name);
    } else if (BOXED_BYTE.equals(parcelableTypeName)) {
      return new ByteProperty(isNullable, typeName, name);
    } else if (BOOLEAN.equals(parcelableTypeName)) {
      return new BooleanProperty(false, typeName, name);
    } else if (BOXED_BOOLEAN.equals(parcelableTypeName)) {
      return new BooleanProperty(isNullable, typeName, name);
    } else if (FLOAT.equals(parcelableTypeName)) {
      return new FloatProperty(false, typeName, name);
    } else if (BOXED_FLOAT.equals(parcelableTypeName)) {
      return new FloatProperty(isNullable, typeName, name);
    } else if (CHAR.equals(parcelableTypeName)) {
      return new CharProperty(false, typeName, name);
    } else if (BOXED_CHAR.equals(parcelableTypeName)) {
      return new CharProperty(isNullable, typeName, name);
    } else if (DOUBLE.equals(parcelableTypeName)) {
      return new DoubleProperty(false, typeName, name);
    } else if (BOXED_DOUBLE.equals(parcelableTypeName)) {
      return new DoubleProperty(isNullable, typeName, name);
    } else if (SHORT.equals(parcelableTypeName)) {
      return new ShortProperty(false, typeName, name);
    } else if (BOXED_SHORT.equals(parcelableTypeName)) {
      return new ShortProperty(isNullable, typeName, name);
    } else if (MAP.equals(parcelableTypeName)) {
      boolean isInterface = TypeUtils.isInterface(typeUtil, erasedType);
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property keyProperty =
          parseProperty(typeArguments.get(0), dataClass, true, name + "Key", typeAdapterMap);
      Property valueProperty =
          parseProperty(typeArguments.get(1), dataClass, true, name + "Value", typeAdapterMap);
      return new MapProperty(keyProperty, valueProperty, isInterface, isNullable, typeName, name);
    } else if (LIST.equals(parcelableTypeName)) {
      boolean isInterface = TypeUtils.isInterface(typeUtil, erasedType);
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument =
          parseProperty(typeArguments.get(0), dataClass, true, name + "Item", typeAdapterMap);
      return new ListProperty(typeArgument, isInterface, isNullable, typeName, name);
    } else if (SET.equals(parcelableTypeName)) {
      boolean isInterface = TypeUtils.isInterface(typeUtil, erasedType);
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument =
          parseProperty(typeArguments.get(0), dataClass, true, name + "Item", typeAdapterMap);
      return new SetProperty(typeArgument, isInterface, isNullable, typeName, name);
    } else if (BOOLEAN_ARRAY.equals(parcelableTypeName)) {
      return new BooleanArrayProperty(isNullable, typeName, name);
    } else if (BYTE_ARRAY.equals(parcelableTypeName)) {
      return new ByteArrayProperty(isNullable, typeName, name);
    } else if (INT_ARRAY.equals(parcelableTypeName)) {
      return new IntArrayProperty(isNullable, typeName, name);
    } else if (LONG_ARRAY.equals(parcelableTypeName)) {
      return new LongArrayProperty(isNullable, typeName, name);
    } else if (STRING_ARRAY.equals(parcelableTypeName)) {
      return new StringArrayProperty(isNullable, typeName, name);
    } else if (SPARSE_ARRAY.equals(parcelableTypeName)) {
      List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
      Property typeArgument =
          parseProperty(typeArguments.get(0), dataClass, true, name + "Value", typeAdapterMap);
      return new SparseArrayProperty(typeArgument, isNullable, typeName, name);
    } else if (SPARSE_BOOLEAN_ARRAY.equals(parcelableTypeName)) {
      return new SparseBooleanArrayProperty(isNullable, typeName, name);
    } else if (BUNDLE.equals(parcelableTypeName)) {
      return new BundleProperty(isNullable, typeName, name);
    } else if (PARCELABLE.equals(parcelableTypeName)) {
      return new ParcelableProperty(isNullable, typeName, name);
    } else if (OBJECT_ARRAY.equals(parcelableTypeName)) {
      TypeMirror componentType = ((ArrayType) type).getComponentType();
      Property componentProperty =
          parseProperty(componentType, dataClass, true, name + "Component", typeAdapterMap);
      return new ArrayProperty(componentProperty, typeName, isNullable, name);
    } else if (CHAR_SEQUENCE.equals(parcelableTypeName)) {
      return new CharSequenceProperty(isNullable, typeName, name);
    } else if (IBINDER.equals(parcelableTypeName)) {
      return new IBinderProperty(isNullable, typeName, name);
    } else if (ENUM.equals(parcelableTypeName)) {
      return new EnumProperty(isNullable, typeName, name);
    } else if (PERSISTABLE_BUNDLE.equals(parcelableTypeName)) {
      return new PersistableBundleProperty(isNullable, typeName, name);
    } else if (SIZE.equals(parcelableTypeName)) {
      return new SizeProperty(isNullable, typeName, name);
    } else if (SIZEF.equals(parcelableTypeName)) {
      return new SizeFProperty(isNullable, typeName, name);
    } else if (TYPE_ADAPTER.equals(parcelableTypeName)) {
      return new TypeAdapterProperty(typeAdapter, isNullable, typeName, name);
    } else if (DATE.equals(parcelableTypeName)) {
      return new DateProperty(isNullable, typeName, name);
    } else if (BIG_INTEGER.equals(parcelableTypeName)) {
      return new BigIntegerProperty(isNullable, typeName, name);
    } else if (BIG_DECIMAL.equals(parcelableTypeName)) {
      return new BigDecimalProperty(isNullable, typeName, name);
    } else if (MATH_CONTEXT.equals(parcelableTypeName)) {
      return new MathContextProperty(isNullable, typeName, name);
    } else if (UUID.equals(parcelableTypeName)) {
      return new UuidProperty(isNullable, typeName, name);
    } else if (FILE.equals(parcelableTypeName)) {
      return new FileProperty(isNullable, typeName, name);
    } else if (STACK_TRACE_ELEMENT.equals(parcelableTypeName)) {
      return new StackTraceElementProperty(isNullable, typeName, name);
    } else if (URL.equals(parcelableTypeName)) {
      return new UrlProperty(isNullable, typeName, name);
    } else if (URI.equals(parcelableTypeName)) {
      return new UriProperty(isNullable, typeName, name);
    } else if (PATTERN.equals(parcelableTypeName)) {
      return new PatternProperty(isNullable, typeName, name);
    } else if (typeName instanceof ClassName && wrappers.containsKey(typeName)) {
      return new WrapperProperty(wrappers.get(typeName), isNullable, typeName, name);
    } else {
      throw new UnknownTypeException(erasedType);
    }
  }

  private TypeMirror getActualTypeParameter(TypeMirror variable, TypeMirror dataClass) {
    // The element associated, or null
    Element element = typeUtil.asElement(variable);

    // Find and replace type parameter arguments with the provided type in the data class.
    // e.g. a variable defined as ExampleClass<Integer>, this will replace type T with Integer
    // when processing ExampleClassIntegerParcel.java.
    if (element != null && element.getKind() == ElementKind.TYPE_PARAMETER) {
      TypeElement dataClassElement = (TypeElement) typeUtil.asElement(dataClass);
      List<? extends TypeParameterElement> typeParameterElements =
          dataClassElement.getTypeParameters();
      int numTypeParams = typeParameterElements.size();
      for (int i = 0; i < numTypeParams; i++) {
        TypeParameterElement p = typeParameterElements.get(i);
        if (p.equals(element)) {
          variable = ((DeclaredType) dataClass).getTypeArguments().get(i);
          break;
        }
      }
    }

    return variable;
  }

  private TypeName getParcelableType(TypeMirror typeMirror) {
    while (typeMirror.getKind() != TypeKind.NONE) {
      TypeElement type = (TypeElement) typeUtil.asElement(typeMirror);
      TypeName typeName = TypeName.get(typeMirror);
      if (typeName instanceof ParameterizedTypeName) {
        typeName = ((ParameterizedTypeName) typeName).rawType;
      }
      if (typeName.isPrimitive() || VALID_TYPES.contains(typeName)) {
        return typeName;
      }
      if (typeName instanceof ArrayTypeName) {
        return OBJECT_ARRAY;
      }
      for (TypeMirror interfaceType : type.getInterfaces()) {
        TypeName inherited = getParcelableType(interfaceType);
        if (inherited != null) {
          return inherited;
        }
      }
      typeMirror = type.getSuperclass();
    }
    return null;
  }

  public static class DuplicateFieldNameException extends Exception {
    final VariableElement first;
    final VariableElement second;

    public DuplicateFieldNameException(VariableElement first, VariableElement second) {
      this.first = first;
      this.second = second;
    }
  }

  public static class UnsatisfiableConstructorException extends Exception {
    final int fieldsToPassToConstructorCount;
    final ExecutableElement constructor;
    final List<VariableElement> missingArguments;

    public UnsatisfiableConstructorException(int fieldsToPassToConstructorCount,
        ExecutableElement constructor, List<VariableElement> missingArguments) {
      this.fieldsToPassToConstructorCount = fieldsToPassToConstructorCount;
      this.constructor = constructor;
      this.missingArguments = missingArguments;
    }
  }

  public static class NoVisibleConstructorException extends Exception {
  }

  public static class UnknownTypeException extends Exception {
    final TypeMirror unknownType;

    UnknownTypeException(TypeMirror unknownType) {
      this.unknownType = unknownType;
    }
  }

  public static class UnknownPropertyTypeException extends Exception {
    final VariableElement element;
    final TypeMirror unknownType;

    UnknownPropertyTypeException(VariableElement element, TypeMirror unknownType) {
      this.element = element;
      this.unknownType = unknownType;
    }
  }

  public static class NonReadablePropertyException extends Exception {
    final VariableElement element;

    NonReadablePropertyException(VariableElement element) {
      this.element = element;
    }
  }

  public static class NonWritablePropertyException extends Exception {
    final VariableElement element;

    NonWritablePropertyException(VariableElement element) {
      this.element = element;
    }
  }

  private static class FieldInfo {
    public final boolean visible;
    public final ExecutableElement getterMethod;
    public final VariableElement element;
    public final ExecutableElement setterMethod;
    public final int constructorIndex;

    public FieldInfo(boolean visible, ExecutableElement getterMethod, VariableElement field) {
      this(visible, getterMethod, field, null, -1);
    }

    public FieldInfo(boolean visible, ExecutableElement getterMethod,
        VariableElement element, ExecutableElement setterMethod, int constructorIndex) {
      this.visible = visible;
      this.getterMethod = getterMethod;
      this.element = element;
      this.setterMethod = setterMethod;
      this.constructorIndex = constructorIndex;
    }
  }

  private static class ParameterInfo {
    private final String name;
    private final TypeMirror type;
    private final VariableElement element;

    public ParameterInfo(String name, TypeMirror type, VariableElement element) {
      this.name = name;
      this.type = type;
      this.element = element;
    }
  }
}
