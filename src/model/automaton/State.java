package model.automaton;

import java.util.Set;
import java.util.TreeSet;

public class State {

    public static final State ERROR_STATE = new State("-");
    private Set<String> id;

    public State(String label) {
        id = new TreeSet<>();
        id.add(label);
    }

    public State(Set<String> id) {
        this.id = id;
    }

    public State(State state) {
        this.id = new TreeSet<>(state.id);
    }

    public Set<String> id() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof State))
            return false;
        State other = (State) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void addLabels(Set<String> labels) {
        id.addAll(labels);
    }
}
