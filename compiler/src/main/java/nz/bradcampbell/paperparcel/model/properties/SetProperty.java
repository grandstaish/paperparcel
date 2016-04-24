package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.TypeName;
import java.util.LinkedHashSet;
import java.util.Set;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.model.properties.base.CollectionProperty;

public class SetProperty extends CollectionProperty<Set> {
  private final boolean isInterface;

  public SetProperty(Property typeArgument, boolean isInterface, boolean isNullable,
      TypeName typeName, String name) {
    super(typeArgument, isNullable, typeName, name);
    this.isInterface = isInterface;
  }

  @Override public Class<? extends Set> getDefaultType() {
    return LinkedHashSet.class;
  }

  @Override public boolean isDefaultType() {
    return isInterface;
  }

  @Override public boolean defaultTypeHasDefaultCapacityConstructor() {
    return true;
  }
}
