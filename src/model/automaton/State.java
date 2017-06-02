package model.automaton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State {

    private Character id;
    private boolean accepting;
    private Map<Character, Set<State>> transitions;

    public State(Character id) {
        this.id = id;
        this.accepting = false;
        transitions = new HashMap<>();
    }

    public void addTransition(Character c, Set<State> states) {
        transitions.put(c, states);
    }

    public Set<State> getTransitionWith(Character c) {
        return transitions.get(c);
    }
    public Character getId() {
        return id;
    }

    public void setId(Character id) {
        this.id = id;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }
}
