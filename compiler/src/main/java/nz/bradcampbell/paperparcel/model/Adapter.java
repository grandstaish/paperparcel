package nz.bradcampbell.paperparcel.model;

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
}
