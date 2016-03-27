package nz.bradcampbell.paperparcel.javaexample;

import nz.bradcampbell.paperparcel.AccessorName;
import nz.bradcampbell.paperparcel.PaperParcel;
import nz.bradcampbell.paperparcel.TypeAdapters;

import java.util.Date;

@PaperParcel
@TypeAdapters(DateTypeAdapter.class)
public final class State {
  private final int count;

  /**
   * Able to use a custom getter name as per the @AccessorMethod tag. By default, if "x" is the property name,
   * PaperParcel will search for a method named "x()", "getX()", or "isX()"
   */
  @AccessorName("customGetterMethodName")
  private final Date modificationDate;

  public State(int count, Date modificationDate) {
    this.count = count;
    this.modificationDate = modificationDate;
  }

  public int getCount() {
    return count;
  }

  public Date customGetterMethodName() {
    return modificationDate;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    State state = (State) o;

    if (count != state.count) {
      return false;
    }
    return modificationDate != null ? modificationDate.equals(state.modificationDate) : state.modificationDate == null;

  }

  @Override public int hashCode() {
    int result = count;
    result = 31 * result + (modificationDate != null ? modificationDate.hashCode() : 0);
    return result;
  }
}
