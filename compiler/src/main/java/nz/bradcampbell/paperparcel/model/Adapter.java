package nz.bradcampbell.paperparcel.model;

import com.google.common.base.Objects;
import com.squareup.javapoet.ClassName;

public class Adapter {
  private final ClassName className;
  private final boolean singleton;

  public Adapter(boolean singleton, ClassName className) {
    this.singleton = singleton;
    this.className = className;
  }

  public ClassName getClassName() {
    return className;
  }

  public boolean isSingleton() {
    return singleton;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Adapter adapter = (Adapter) o;
    return singleton == adapter.singleton &&
        Objects.equal(className, adapter.className);
  }

  @Override public int hashCode() {
    return Objects.hashCode(className, singleton);
  }
}
