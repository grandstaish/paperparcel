package nz.bradcampbell.paperparcel.java7example;

import java.util.Date;
import nz.bradcampbell.paperparcel.PaperParcel;
import nz.bradcampbell.paperparcel.PaperParcelable;
import nz.bradcampbell.paperparcel.TypeAdapters;

@PaperParcel
@TypeAdapters(DateTypeAdapter.class)
public final class State extends PaperParcelable {
  public static final PaperParcelable.Creator<State> CREATOR =
      new PaperParcelable.Creator<>(State.class);

  private final int count;

  public Date modificationDate;

  public State(int count) {
    this.count = count;
  }

  public int getCount() {
    return count;
  }
}
