package nz.bradcampbell.paperparcel.model;

import com.google.common.base.Objects;
import com.squareup.javapoet.ClassName;
import java.util.List;
import java.util.Set;
import nz.bradcampbell.paperparcel.PaperParcels.Delegate;

/**
 * A model object that holds information needed to build a Parcelable data class wrapper
 */
public class DataClass {
  private final String classPackage;
  private final List<Property> properties;
  private final ClassName className;
  private final ClassName wrapperClassName;
  private final ClassName delegateClassName;
  private final boolean requiresClassLoader;
  private final Set<Adapter> requiredTypeAdapters;
  private final boolean singleton;

  /**
   * Constructor.
   *
   * @param properties All properties in the data class
   * @param classPackage The package of the data class
   * @param wrapperTypeName The class name of the wrapper class
   * @param className The data class type name
   * @param delegateClassName The class name of the {@link Delegate} for this data class
   * @param requiresClassLoader True if a ClassLoader field is required, false otherwise
   * @param requiredTypeAdapters All of the TypeAdapter types required for this class
   * @param singleton True if the class is a singleton object
   */
  public DataClass(List<Property> properties, String classPackage, ClassName wrapperTypeName,
      ClassName className, ClassName delegateClassName, boolean requiresClassLoader,
      Set<Adapter> requiredTypeAdapters, boolean singleton) {
    this.properties = properties;
    this.classPackage = classPackage;
    this.delegateClassName = delegateClassName;
    this.requiresClassLoader = requiresClassLoader;
    this.wrapperClassName = wrapperTypeName;
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

  public ClassName getClassName() {
    return className;
  }

  public ClassName getDelegateClassName() {
    return delegateClassName;
  }

  public boolean getRequiresClassLoader() {
    return requiresClassLoader;
  }

  public Set<Adapter> getRequiredTypeAdapters() {
    return requiredTypeAdapters;
  }

  public boolean isSingleton() {
    return singleton;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataClass dataClass = (DataClass) o;
    return requiresClassLoader == dataClass.requiresClassLoader &&
        singleton == dataClass.singleton &&
        Objects.equal(classPackage, dataClass.classPackage) &&
        Objects.equal(properties, dataClass.properties) &&
        Objects.equal(className, dataClass.className) &&
        Objects.equal(wrapperClassName, dataClass.wrapperClassName) &&
        Objects.equal(delegateClassName, dataClass.delegateClassName) &&
        Objects.equal(requiredTypeAdapters, dataClass.requiredTypeAdapters);
  }

  @Override public int hashCode() {
    return Objects.hashCode(classPackage, properties, className, wrapperClassName,
        delegateClassName, requiresClassLoader, requiredTypeAdapters, singleton);
  }
}
