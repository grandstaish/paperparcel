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

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.UnknownTypeException;
import javax.tools.Diagnostic;

import static com.google.auto.common.MoreElements.asType;
import static javax.lang.model.element.Modifier.PRIVATE;

/**
 * A {@link BasicAnnotationProcessor.ProcessingStep} that is responsible for dealing with all
 * {@link PaperParcel} annotated objects as part of the {@link PaperParcelProcessor}.
 */
final class PaperParcelProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

  private static final String LOMBOK_GETTER_ANNOTATION = "@lombok.Getter";
  private static final String LOMBOK_ACCESS_LEVEL_NONE = "AccessLevel.NONE";
  private static final String LOMBOK_DATA_ANNOTATION = "@lombok.Data";

  private final Messager messager;
  private final OptionsHolder optionsHolder;
  private final PaperParcelValidator paperParcelValidator;
  private final PaperParcelDescriptor.Factory paperParcelDescriptorFactory;
  private final PaperParcelGenerator paperParcelGenerator;

  private boolean isLombokEnabled = false;
  private boolean waitForLombok = false;

  PaperParcelProcessingStep(
      Messager messager,
      OptionsHolder optionsHolder,
      PaperParcelValidator paperParcelValidator,
      PaperParcelDescriptor.Factory paperParcelDescriptorFactory,
      PaperParcelGenerator paperParcelGenerator) {
    this.messager = messager;
    this.optionsHolder = optionsHolder;
    this.paperParcelValidator = paperParcelValidator;
    this.paperParcelDescriptorFactory = paperParcelDescriptorFactory;
    this.paperParcelGenerator = paperParcelGenerator;

  }

  @Override public Set<? extends Class<? extends Annotation>> annotations() {
    return ImmutableSet.of(PaperParcel.class);
  }

  @Override
  public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

    Set<Element> defferedElements = new HashSet<>();

    for (Element element : elementsByAnnotation.get(PaperParcel.class)) {

      TypeElement paperParcelElement = asType(element);
      OptionsDescriptor options = Utils.getOptions(paperParcelElement).or(optionsHolder.getOptions());
      ValidationReport<TypeElement> validationReport = paperParcelValidator.validate(paperParcelElement, options);
      validationReport.printMessagesTo(messager);

      isLombokEnabled = options.isLombokEnabled();
      waitForLombok = isLombokEnabled && isLombokDataAnnotationPresent(element);

      if (validationReport.isClean()) {
        try {
          generatePaperParcel(paperParcelDescriptorFactory.create(paperParcelElement, options));
        } catch (PaperParcelDescriptor.NonWritableFieldsException e) {
          if (waitForLombok) {
            defferedElements.add(element);
          } else {
            printMessages(e);
          }
        } catch (PaperParcelDescriptor.NonReadableFieldsException e) {
          if (waitForLombok && !hasFieldsWithLombockGetterAccessLevelNone(e)) {
            defferedElements.add(element);
          } else {
            printMessages(e);
          }
        } catch (UnknownTypeException e) {
          messager.printMessage(Diagnostic.Kind.ERROR, String.format(ErrorMessages.FIELD_MISSING_TYPE_ADAPTER,
                                                                     e.getUnknownType().toString()), (Element) e.getArgument());
        }
      }
    }
    return defferedElements;
  }

  private boolean isLombokDataAnnotationPresent(Element element) {
    if (element.getKind().equals(ElementKind.CLASS)) {
      for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
        if (mirror.toString().equals(LOMBOK_DATA_ANNOTATION)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasFieldsWithLombockGetterAccessLevelNone(PaperParcelDescriptor.NonReadableFieldsException e) {
    for (VariableElement nonReadableField : e.nonReadableFields()) {
      for (AnnotationMirror mirror : nonReadableField.getAnnotationMirrors())
        if (mirror.toString().contains(LOMBOK_GETTER_ANNOTATION) &&
            mirror.toString().contains(LOMBOK_ACCESS_LEVEL_NONE)) {
        return true;
      }
    }
    return false;
  }

  private void generatePaperParcel(PaperParcelDescriptor descriptor) {
    try {
      paperParcelGenerator.generate(descriptor);
    } catch (SourceFileGenerationException e) {
      e.printMessageTo(messager);
    }
  }

  private void printMessages(PaperParcelDescriptor.NonWritableFieldsException e) {
    ImmutableSet<ExecutableElement> validConstructors = e.allNonWritableFieldsMap().keySet();
    ImmutableSet<ExecutableElement> invalidConstructors =
        e.unassignableConstructorParameterMap().keySet();

    if (validConstructors.size() > 0) {
      // Log errors for each non-writable field in each valid constructor
      for (ExecutableElement validConstructor : validConstructors) {
        ImmutableList<VariableElement> nonWritableFields =
            e.allNonWritableFieldsMap().get(validConstructor);
        for (VariableElement nonWritableField : nonWritableFields) {
          String fieldName = nonWritableField.getSimpleName().toString();
          String modifier = nonWritableField.getModifiers().contains(PRIVATE) ? "private" : "final";
          messager.printMessage(Diagnostic.Kind.ERROR,
              String.format(ErrorMessages.FIELD_NOT_WRITABLE,
                  asType(nonWritableField.getEnclosingElement()).getQualifiedName(),
                  fieldName,
                  modifier,
                  validConstructor.toString(),
                  buildExcludeRulesChecklist()),
                  nonWritableField);
        }
      }

    } else {
      // Log errors for unassignable parameters in each invalid constructor
      for (ExecutableElement invalidConstructor : invalidConstructors) {
        ImmutableList<VariableElement> unassignableFields =
            e.unassignableConstructorParameterMap().get(invalidConstructor);
        for (VariableElement unassignableField : unassignableFields) {
          String fieldName = unassignableField.getSimpleName().toString();
          messager.printMessage(Diagnostic.Kind.ERROR,
              String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
                  fieldName,
                  asType(invalidConstructor.getEnclosingElement()).getQualifiedName()),
              invalidConstructor);
        }
      }
    }
  }

  private void printMessages(PaperParcelDescriptor.NonReadableFieldsException e) {
    for (VariableElement nonReadableField : e.nonReadableFields()) {
      String fieldName = nonReadableField.getSimpleName().toString();
      String errorMessage = ErrorMessages.FIELD_NOT_READABLE;
		if (waitForLombok){
        for (AnnotationMirror mirror : nonReadableField.getAnnotationMirrors())
          if (mirror.toString().contains(LOMBOK_GETTER_ANNOTATION) &&
              mirror.toString().contains(LOMBOK_ACCESS_LEVEL_NONE)) {
            errorMessage = ErrorMessages.FIELD_NOT_READABLE_LOMBOK_GETTER_ACCESS_LEVEL_NONE;
            break;
          }
      }

      messager.printMessage(Diagnostic.Kind.ERROR,
          String.format(errorMessage,
              asType(nonReadableField.getEnclosingElement()).getQualifiedName(),
              fieldName,
              buildExcludeRulesChecklist()),
          nonReadableField);
    }
  }

  private String buildExcludeRulesChecklist() {
    StringBuilder sb = new StringBuilder();
    OptionsDescriptor options = optionsHolder.getOptions();

    for (Set<Modifier> modifiers : options.excludeModifiers()) {
      sb.append("- Adding the ");
      for (Modifier modifier : modifiers) {
        sb.append(modifier.toString());
        sb.append(" ");
      }
      sb.append(modifiers.size() == 1 ? "modifier\n" : "modifiers\n");
    }

    ImmutableList<String> excludeAnnotations = options.excludeAnnotationNames();
    for (String excludeAnnotation : excludeAnnotations) {
      sb.append("- Adding @");
      sb.append(excludeAnnotation);
      sb.append('\n');
    }

    if (options.excludeNonExposedFields()) {
      ImmutableList<String> exposeAnnotations = options.exposeAnnotationNames();
      for (String exposeAnnotation : exposeAnnotations) {
        sb.append("- Removing @");
        sb.append(exposeAnnotation);
        sb.append('\n');
      }
    }

    return sb.toString();
  }
}
