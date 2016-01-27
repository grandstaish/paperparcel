package nz.bradcampbell.paperparcel.javaexample;

import nz.bradcampbell.paperparcel.GetterMethodName;
import nz.bradcampbell.paperparcel.PaperParcel;

import java.util.Date;

@PaperParcel(typeAdapters = DateTypeAdapter.class)
public final class State {
    private final int count;

    @GetterMethodName("customGetterMethodName")
    private final Date modificationDate;

    public State(int count, Date modificationDate) {
        this.count = count;
        this.modificationDate = modificationDate;
    }

    public int getCount() {
        return count;
    }

    // NOTE: able to use a custom getter name as per the @GetterMethodName tag. By default, if "x" is the property name,
    // PaperParcel will search for a method named "x()", "getX()", or "isX()"

    public Date customGetterMethodName() {
        return modificationDate;
    }

    // NOTE: equals and hashcode automatically created by Intellij by pressing Ctrl + Enter anywhere in the class and
    // selecting the "equals() and hashcode()" option. These methods are not required by PaperParcel

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (count != state.count) return false;
        return modificationDate != null ? modificationDate.equals(state.modificationDate) : state.modificationDate == null;
    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + (modificationDate != null ? modificationDate.hashCode() : 0);
        return result;
    }
}
