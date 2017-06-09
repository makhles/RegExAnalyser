package model.automaton;

public class State {

    public static final State ERROR_STATE = new State("-");
    private String label;

    public State(String id) {
        this.label = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
