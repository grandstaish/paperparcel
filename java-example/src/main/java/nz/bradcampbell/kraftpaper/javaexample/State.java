package nz.bradcampbell.kraftpaper.javaexample;

import nz.bradcampbell.kraftpaper.KraftPaper;

import java.util.Date;

@KraftPaper(typeAdapters = DateTypeAdapter.class)
public final class State {
    private final int count;
    private final Date modificationDate;

    public State(int count, Date modificationDate) {
        this.count = count;
        this.modificationDate = modificationDate;
    }

    public int getCount() {
        return count;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    // NOTE: component1() and component2() are requirements of KraftPaper, but will not be in a future release

    public int component1() {
        return count;
    }

    public Date component2() {
        return modificationDate;
    }

    // NOTE: equals and hashcode automatically created by Intellij by pressing Ctrl + Enter and selecting
    // equals() and hashcode().

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
