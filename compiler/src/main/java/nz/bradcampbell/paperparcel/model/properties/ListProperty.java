package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.model.properties.base.CollectionProperty;

public class ListProperty extends CollectionProperty<List> {
  private final boolean isInterface;

  public ListProperty(Property typeArgument, boolean isInterface, boolean isNullable,
      TypeName typeName, String name) {
    super(typeArgument, isNullable, typeName, name);
    this.isInterface = isInterface;
  }

  @Override public Class<? extends List> getDefaultType() {
    return ArrayList.class;
  }

  @Override public boolean isDefaultType() {
    return isInterface;
  }

  @Override public boolean defaultTypeHasDefaultCapacityConstructor() {
    return true;
  }
}
