package nz.bradcampbell.dataparcel;

import android.os.Parcelable;
import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;
import com.squareup.javapoet.*;
import nz.bradcampbell.dataparcel.internal.DataClass;
import nz.bradcampbell.dataparcel.internal.Property;
import nz.bradcampbell.dataparcel.internal.PropertyCreator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

import static javax.lang.model.element.Modifier.*;
import static nz.bradcampbell.dataparcel.internal.PropertyCreator.createProperty;
import static nz.bradcampbell.dataparcel.internal.PropertyCreator.isValidType;

@AutoService(Processor.class)
public class DataParcelProcessor extends AbstractProcessor {
  private static final String NULLABLE_ANNOTATION_NAME = "Nullable";

  public static final String DATA_VARIABLE_NAME = "data";

  private Elements elementUtils;
  private Filer filer;
  private Types typeUtil;
  private Map<String, DataClass> parcels = new HashMap<String, DataClass>();

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    elementUtils = env.getElementUtils();
    filer = env.getFiler();
    typeUtil = env.getTypeUtils();

    // TODO: use DI instead of static instance
    PropertyCreator.init(typeUtil);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(DataParcel.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
    if (annotations.isEmpty()) {
      return true;
    }
    for (Element element : roundEnvironment.getElementsAnnotatedWith(DataParcel.class)) {
      if (!(element instanceof TypeElement)) {
        error("@DataParcel applies to a type, " + element.getSimpleName() + " is a " + element.getKind(), element);
        continue;
      }
      TypeElement el = (TypeElement) element;
      createParcel(el);
    }
    for (DataClass p : parcels.values()) {
      try {
        generateJavaFileFor(p).writeTo(filer);
      } catch (IOException e) {
        throw new RuntimeException("An error occurred while writing to filer.", e);
      }
    }
    return true;
  }

  private void createParcel(TypeElement typeElement) {
    if (parcels.containsKey(typeElement.getQualifiedName().toString())) return;
    String classPackage = getPackageName(typeElement);
    String className = ClassName.get(typeElement).simpleName() + "Parcel";
    List<Property> properties = new ArrayList<Property>();
    List<VariableElement> variableElements = getFields(typeElement);
    List<TypeElement> variableDataParcelDependencies = new ArrayList<TypeElement>();
    for (int i = 0; i < variableElements.size(); i++) {
      VariableElement variableElement = variableElements.get(i);
      boolean isNullable = !isFieldRequired(variableElement);
      TypeName parsedTypeName = parseParameterTypes(variableElement.asType(), variableDataParcelDependencies);
      Property property = createProperty(variableElement.asType(), isNullable, "component" + (i + 1), parsedTypeName);
      properties.add(property);
    }
    parcels.put(typeElement.getQualifiedName().toString(), new DataClass(properties, classPackage, className, typeElement));
    // Build parcel dependencies
    for (TypeElement requiredParcel : variableDataParcelDependencies) {
      createParcel(requiredParcel);
    }
  }

  private TypeName parseParameterTypes(TypeMirror type, List<TypeElement> variableDataParcelDependencies) {
    TypeName typeName = ClassName.get(typeUtil.erasure(type));
    boolean isParcelable = isValidType(typeUtil, type);
    TypeName result;
    if (isParcelable) {
      if (type instanceof DeclaredType) {
        DeclaredType declaredType = (DeclaredType) type;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        int numTypeArgs = typeArguments.size();
        if (numTypeArgs > 0) {
          TypeName[] args = new TypeName[numTypeArgs];
          for (int i = 0; i < args.length; i++) {
            args[i] = parseParameterTypes(typeArguments.get(i), variableDataParcelDependencies);
          }
          result = ParameterizedTypeName.get((ClassName) typeName, args);
        } else {
          result = typeName;
        }
      } else {
        result = typeName;
      }
    } else {
      // This is (one of) the reason(s) it is not parcelable. Assume it contains a data object as a parameter
      TypeElement requiredElement = (TypeElement) typeUtil.asElement(type);
      variableDataParcelDependencies.add(requiredElement);
      String packageName = elementUtils.getPackageOf(requiredElement).getQualifiedName().toString();
      String className = requiredElement.getSimpleName().toString() + "Parcel";
      result = ClassName.get(packageName, className);
    }
    return result;
  }

  private List<VariableElement> getFields(TypeElement el) {
    List<? extends Element> enclosedElements = el.getEnclosedElements();
    List<VariableElement> variables = new ArrayList<VariableElement>();
    for (Element e : enclosedElements) {
      if (e instanceof VariableElement && !e.getModifiers().contains(STATIC)) {
        variables.add((VariableElement) e);
      }
    }
    return variables;
  }

  private void error(String message, Element element) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }

  private String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

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
    return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
  }

  private JavaFile generateJavaFileFor(DataClass dataClass) {
    TypeSpec.Builder o = TypeSpec.classBuilder(dataClass.getWrapperClassName().simpleName())
        .addModifiers(PUBLIC)
        .addSuperinterface(Parcelable.class)
        .addField(generateCreator(dataClass))
        .addField(generateContentsField(dataClass))
        .addMethod(generateWrapMethod(dataClass))
        .addMethod(generateContentsConstructor(dataClass))
        .addMethod(generateParcelConstructor(dataClass))
        .addMethod(generateGetter(dataClass))
        .addMethod(generateDescribeContents())
        .addMethod(generateWriteToParcel(dataClass));
    return JavaFile.builder(dataClass.getClassPackage(), o.build()).build();
  }

  private FieldSpec generateCreator(DataClass dataClass) {
    ClassName className = dataClass.getWrapperClassName();
    ClassName creator = ClassName.get("android.os", "Parcelable", "Creator");
    TypeName creatorOfClass = ParameterizedTypeName.get(creator, className);
    return FieldSpec
        .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
        .initializer(CodeBlock.builder()
            .beginControlFlow("new $T()", ParameterizedTypeName.get(creator, className))
            .beginControlFlow("@$T public $T createFromParcel($T in)", ClassName.get(Override.class), className,
                ClassName.get(android.os.Parcel.class))
            .addStatement("return new $T(in)", className)
            .endControlFlow()
            .beginControlFlow("@$T public $T[] newArray($T size)", ClassName.get(Override.class), className, int.class)
            .addStatement("return new $T[size]", className)
            .endControlFlow()
            .unindent()
            .add("}")
            .build())
        .build();
  }

  private FieldSpec generateContentsField(DataClass dataClass) {
    return FieldSpec.builder(dataClass.getDataClassTypeName(), DATA_VARIABLE_NAME, PRIVATE, FINAL).build();
  }

  private MethodSpec generateWrapMethod(DataClass dataClass) {
    ClassName className = dataClass.getWrapperClassName();
    return MethodSpec.methodBuilder("wrap")
        .addModifiers(PUBLIC, STATIC, FINAL)
        .addParameter(dataClass.getDataClassTypeName(), DATA_VARIABLE_NAME)
        .addStatement("return new $T($N)", className, DATA_VARIABLE_NAME)
        .returns(className)
        .build();
  }

  private MethodSpec generateContentsConstructor(DataClass dataClass) {
    return MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(dataClass.getDataClassTypeName(), DATA_VARIABLE_NAME)
        .addStatement("this.$N = $N", DATA_VARIABLE_NAME, DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateParcelConstructor(DataClass dataClass) {
    ParameterSpec in = ParameterSpec
        .builder(ClassName.get("android.os", "Parcel"), "in")
        .build();
    MethodSpec.Builder builder = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(in);
    List<String> paramNames = new ArrayList<String>();
    for (Property p : dataClass.getDataClassProperties()) {
      builder.addCode(p.readFromParcel(in));
      paramNames.add(p.getName());
    }
    builder.addStatement("this.$N = new $T($N)", DATA_VARIABLE_NAME, dataClass.getDataClassTypeName(),
        Joiner.on(", ").join(paramNames));
    return builder.build();
  }

  private MethodSpec generateGetter(DataClass dataClass) {
    return MethodSpec.methodBuilder("getContents")
        .addModifiers(PUBLIC)
        .returns(dataClass.getDataClassTypeName())
        .addStatement("return $N", DATA_VARIABLE_NAME)
        .build();
  }

  private MethodSpec generateDescribeContents() {
    return MethodSpec.methodBuilder("describeContents")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(int.class)
        .addStatement("return 0")
        .build();
  }

  private MethodSpec generateWriteToParcel(DataClass dataClass) {
    ParameterSpec dest = ParameterSpec
        .builder(ClassName.get("android.os", "Parcel"), "dest")
        .build();
    MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(dest)
        .addParameter(int.class, "flags");
    for (Property p : dataClass.getDataClassProperties()) {
      builder.addCode(p.writeToParcel(dest));
    }
    return builder.build();
  }
}
