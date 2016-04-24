package nz.bradcampbell.paperparcel.model.properties;

import com.squareup.javapoet.TypeName;
import java.util.LinkedList;
import java.util.Queue;
import nz.bradcampbell.paperparcel.model.Property;
import nz.bradcampbell.paperparcel.model.properties.base.CollectionProperty;

public class QueueProperty extends CollectionProperty<Queue> {
  private final boolean isInterface;

  public QueueProperty(Property typeArgument, boolean isInterface, boolean isNullable,
      TypeName typeName, String name) {
    super(typeArgument, isNullable, typeName, name);
    this.isInterface = isInterface;
  }

  @Override public Class<? extends Queue> getDefaultType() {
    return LinkedList.class;
  }

  @Override public boolean isDefaultType() {
    return isInterface;
  }

  @Override public boolean defaultTypeHasDefaultCapacityConstructor() {
    return false;
  }
}
