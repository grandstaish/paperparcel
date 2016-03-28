package nz.bradcampbell.paperparcel.java7example;

import nz.bradcampbell.paperparcel.AccessorName;
import nz.bradcampbell.paperparcel.PaperParcel;
import nz.bradcampbell.paperparcel.PaperParcelable;
import nz.bradcampbell.paperparcel.TypeAdapters;

import java.util.Date;

@PaperParcel
@TypeAdapters(DateTypeAdapter.class)
public final class State extends PaperParcelable {
  private static final PaperParcelable.Creator<State> CREATOR = new PaperParcelable.Creator<>(State.class);

  private final int count;

  // Able to use a custom getter name as per the @AccessorMethod tag. By default, if "x" is the property name,
  // PaperParcel will search for a method named "x()", "getX()", or "isX()"
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
}
