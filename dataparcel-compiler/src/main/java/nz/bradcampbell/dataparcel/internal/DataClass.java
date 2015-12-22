package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import java.util.List;

public class DataClass {

  // Shared package. The data class wrapper is generated in the same package as the data class
  private final String classPackage;

  // Data class info
  private final List<Property> dataClassProperties;
  private final TypeName dataClassTypeName;
  private final TypeElement dataClassElement;

  // Wrapper class info
  private final ClassName wrapperClassName;

  public DataClass(List<Property> dataClassProperties, String classPackage, String wrapperClassName, TypeElement dataClassElement) {
    this.dataClassProperties = dataClassProperties;
    this.classPackage = classPackage;
    this.dataClassElement = dataClassElement;
    this.wrapperClassName = ClassName.get(classPackage, wrapperClassName);
    this.dataClassTypeName = ClassName.get(dataClassElement);
  }

  public List<Property> getDataClassProperties() {
    return dataClassProperties;
  }

  public String getClassPackage() {
    return classPackage;
  }

  public TypeElement getDataClassElement() {
    return dataClassElement;
  }

  public ClassName getWrapperClassName() {
    return wrapperClassName;
  }

  public TypeName getDataClassTypeName() {
    return dataClassTypeName;
  }
}
