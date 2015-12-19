package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import java.util.List;

public class Parcel {
  private final List<Property> properties;
  private final String name;
  private final String classPackage;
  private final TypeName data;
  private final TypeElement element;

  public Parcel(List<Property> properties, String classPackage, String name, TypeElement element) {
    this.properties = properties;
    this.classPackage = classPackage;
    this.name = name;
    this.element = element;
    this.data = ClassName.get(element);
  }

  public List<Property> getProperties() {
    return properties;
  }

  public String getClassPackage() {
    return classPackage;
  }

  public String getName() {
    return name;
  }

  public TypeElement getElement() {
    return element;
  }

  public TypeName getTypeName() {
    return data;
  }
}
