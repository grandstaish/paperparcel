package nz.bradcampbell.paperparcel.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

/**
 * A model object that holds information needed to build a Parcelable data class wrapper
 */
public class DataClass {
  private final String classPackage;
  private final List<Property> properties;
  private final TypeName className;
  private final ClassName wrapperClassName;
  private final boolean requiresClassLoader;
  private final Set<ClassName> requiredTypeAdapters;
  private final boolean singleton;

  /**
   * Constructor.
   *
   * @param properties All properties in the data class
   * @param classPackage The package of the data class
   * @param wrapperTypeName The simple name of the wrapper class
   * @param className The data class type name
   * @param requiresClassLoader True if a ClassLoader field is required, false otherwise
   * @param requiredTypeAdapters All of the TypeAdapter types required for this class
   * @param singleton True if the class is a singleton object
   */
  public DataClass(List<Property> properties, String classPackage, String wrapperTypeName, TypeName className,
                   boolean requiresClassLoader, Set<ClassName> requiredTypeAdapters, boolean singleton) {
    this.properties = properties;
    this.classPackage = classPackage;
    this.requiresClassLoader = requiresClassLoader;
    this.wrapperClassName = ClassName.get(classPackage, wrapperTypeName);
    this.className = className;
    this.requiredTypeAdapters = requiredTypeAdapters;
    this.singleton = singleton;
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

  public boolean getRequiresClassLoader() {
    return requiresClassLoader;
  }

  public Set<ClassName> getRequiredTypeAdapters() {
    return requiredTypeAdapters;
  }

  public boolean isSingleton() {
    return singleton;
  }
}
