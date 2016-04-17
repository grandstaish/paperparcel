package nz.bradcampbell.paperparcel;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import nz.bradcampbell.paperparcel.model.Adapter;
import nz.bradcampbell.paperparcel.model.DataClass;
import nz.bradcampbell.paperparcel.utils.TypeUtils;

import static nz.bradcampbell.paperparcel.PaperParcels.DELEGATE_SUFFIX;
import static nz.bradcampbell.paperparcel.PaperParcels.WRAPPER_SUFFIX;
import static nz.bradcampbell.paperparcel.utils.TypeUtils.hasTypeArguments;
import static nz.bradcampbell.paperparcel.utils.TypeUtils.isSingleton;

/**
 * An annotation processor that creates Parcelable wrappers for all Kotlin data classes annotated
 * with @PaperParcel
 */
@AutoService(Processor.class) public class PaperParcelProcessor extends AbstractProcessor {
  public static final String DATA_VARIABLE_NAME = "data";

  private Filer filer;
  private Types typeUtil;
  private Elements elementUtils;

  private Map<TypeName, Adapter> defaultAdapters;
  private Set<TypeElement> unprocessedTypes;
  private Map<ClassName, ClassName> wrappers;
  private Map<ClassName, ClassName> delegates;

  private DataClassParser dataClassParser;
  private WrapperGenerator wrapperGenerator;
  private DelegateGenerator delegateGenerator;

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    types.add(PaperParcel.class.getCanonicalName());
    types.add(DefaultAdapter.class.getCanonicalName());
    types.add(TypeAdapters.class.getCanonicalName());
    return types;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    typeUtil = env.getTypeUtils();
    elementUtils = env.getElementUtils();
    filer = env.getFiler();

    defaultAdapters = new LinkedHashMap<>();
    unprocessedTypes = new LinkedHashSet<>();
    wrappers = new LinkedHashMap<>();
    delegates = new LinkedHashMap<>();

    dataClassParser = new DataClassParser(processingEnv, defaultAdapters, wrappers, delegates);
    wrapperGenerator = new WrapperGenerator();
    delegateGenerator = new DelegateGenerator();
  }

  @Override public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnvironment) {
    findDefaultAdapters(roundEnvironment);
    findPaperParcels(roundEnvironment);

    boolean isLastRound = roundEnvironment.processingOver();

    // Parse models
    Set<DataClass> newDataClasses = dataClassParser.parseDataClasses(unprocessedTypes, isLastRound);

    // Generate wrappers
    for (DataClass dataClass : newDataClasses) {
      try {
        wrapperGenerator.generateParcelableWrapper(dataClass).writeTo(filer);
        delegateGenerator.generatePaperParcelsDelegate(dataClass).writeTo(filer);
      } catch (IOException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "Could not write generated class " + dataClass.getClassName() + ": " + e);
      }
    }

    return true;
  }

  private void findDefaultAdapters(RoundEnvironment roundEnvironment) {
    for (Element element : roundEnvironment.getElementsAnnotatedWith(DefaultAdapter.class)) {

      // Ensure we are dealing with a TypeAdapter
      TypeMirror elementMirror = element.asType();
      TypeMirror typeAdapterMirror = typeUtil.erasure(
          elementUtils.getTypeElement(TypeAdapter.class.getCanonicalName()).asType());

      if (!(typeUtil.isAssignable(elementMirror, typeAdapterMirror))) {
        String message = "@DefaultAdapter must be applied to a TypeAdapter<T>";
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        continue;
      }

      TypeElement typeElement = (TypeElement) element;
      boolean singleton = isSingleton(typeUtil, typeElement);
      TypeName typeAdapterType =
          TypeUtils.getTypeAdapterType(typeUtil, (DeclaredType) typeElement.asType());
      defaultAdapters.put(typeAdapterType, new Adapter(singleton, ClassName.get(typeElement)));
    }
  }

  private void findPaperParcels(RoundEnvironment roundEnvironment) {
    for (Element element : roundEnvironment.getElementsAnnotatedWith(PaperParcel.class)) {
      TypeElement typeElement = (TypeElement) element;

      // Ensure the element isn't parameterized
      if (hasTypeArguments(typeElement)) {
        String message = "PaperParcel types cannot have type parameters";
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, typeElement);
        continue;
      }

      unprocessedTypes.add(typeElement);

      ClassName className = ClassName.get(typeElement);

      ClassName wrapperName =
          ClassName.get(className.packageName(), className.simpleName() + WRAPPER_SUFFIX);
      wrappers.put(className, wrapperName);

      ClassName delegateName =
          ClassName.get(className.packageName(), className.simpleName() + DELEGATE_SUFFIX);
      delegates.put(className, delegateName);
    }
  }
}
