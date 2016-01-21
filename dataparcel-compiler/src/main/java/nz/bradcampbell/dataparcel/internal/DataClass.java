package nz.bradcampbell.dataparcel.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * A model object that holds information needed to build a Parcelable data class wrapper
 */
public class DataClass {
  private final String classPackage;
  private final List<Property> properties;
  private final TypeName className;
  private final ClassName wrapperClassName;
  private final boolean requiresClassLoader;

  /**
   * Constructor.
   *  @param properties All properties in the data class
   * @param classPackage The package of the data class
   * @param wrapperTypeName The simple name of the wrapper class
   * @param className The data class type name
   * @param requiresClassLoader True if a ClassLoader field is required, false otherwise
   */
  public DataClass(List<Property> properties, String classPackage, String wrapperTypeName, TypeName className,
                   boolean requiresClassLoader) {
    this.properties = properties;
    this.classPackage = classPackage;
    this.requiresClassLoader = requiresClassLoader;
    this.wrapperClassName = ClassName.get(classPackage, wrapperTypeName);
    this.className = className;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public String getClassPackage() {
    return classPackage;
  }

  public ClassName getWrapperClassName() {
    return wrapperClassName;
  }

  public TypeName getClassName() {
    return className;
  }

  public boolean requiresClassLoader() {
    return requiresClassLoader;
  }
}
