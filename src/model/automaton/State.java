package model.automaton;

import java.util.Set;
import java.util.TreeSet;

public class State {

    public static final State ERROR_STATE = new State("-");
    private Set<String> labels;

    public State(String label) {
        labels = new TreeSet<>();
        labels.add(label);
    }

    public State(Set<String> id) {
        this.labels = id;
    }

    public State(State state) {
        this.labels = new TreeSet<>(state.labels);
    }

    public Set<String> labels() {
        return labels;
    }

    @Override
    public String toString() {
        return labels.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((labels == null) ? 0 : labels.hashCode());
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
        if (labels == null) {
            if (other.labels != null)
                return false;
        } else if (!labels.equals(other.labels))
            return false;
        return true;
    }

    public void addLabels(Set<String> labels) {
        this.labels.addAll(labels);
    }
}
