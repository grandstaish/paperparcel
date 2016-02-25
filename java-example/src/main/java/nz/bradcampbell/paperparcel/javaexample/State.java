package nz.bradcampbell.paperparcel.javaexample;

import android.support.annotation.Nullable;
import nz.bradcampbell.paperparcel.GetterMethodName;
import nz.bradcampbell.paperparcel.PaperParcel;

import java.util.Date;

@PaperParcel(typeAdapters = DateTypeAdapter.class)
public final class State {
    private final int count;

    /**
     * Able to use a custom getter name as per the @GetterMethodName tag. By default, if "x" is the property name,
     * PaperParcel will search for a method named "x()", "getX()", or "isX()"
     */
    @GetterMethodName("customGetterMethodName")
    private final Date modificationDate;

    /**
     * PaperParcel will do a null-check for this variable. This can be any Nullable annotation, it does not
     * need to be the one from the Android support library
     */
    @Nullable
    private final String nullableString;

    public State(int count, Date modificationDate, @Nullable String nullableString) {
        this.count = count;
        this.modificationDate = modificationDate;
        this.nullableString = nullableString;
    }

    public int getCount() {
        return count;
    }

    public Date customGetterMethodName() {
        return modificationDate;
    }

    @Nullable
    public String getNullableString() {
        return nullableString;
    }

    @Override public int hashCode() {
        int result = count;
        result = 31 * result + modificationDate.hashCode();
        result = 31 * result + (nullableString != null ? nullableString.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (count != state.count) return false;
        if (!modificationDate.equals(state.modificationDate)) return false;
        return nullableString != null ? nullableString.equals(state.nullableString)
            : state.nullableString == null;
    }
}
