package model.automaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Automaton {

    public final static String EPSILON = "&";

    private boolean minimum;
    private State initialState;
    private List<Character> label;
    private List<String> vocabulary;
    private Set<State> acceptingStates;
    private Map<State, List<State>> transitions;

    public Automaton() {
        init();
    }

    public Automaton(List<String> vocabulary) {
        this.vocabulary = vocabulary;
        init();
        label = new LinkedList<>();
        label.add(0, '@');
    }

    public Automaton(Automaton automaton) {
        minimum = automaton.isMinimum();
        vocabulary = new ArrayList<>(automaton.vocabulary());
        initialState = new State(automaton.initialState);
        acceptingStates = new HashSet<>();
        for (State state : automaton.acceptingStates) {
            Set<String> labels = new TreeSet<>(state.labels());
            acceptingStates.add(new State(labels));
        }
        transitions = new HashMap<>();
        for (Map.Entry<State, List<State>> transition : automaton.transitions.entrySet()) {
            List<State> states = new ArrayList<>();
            for (State state : transition.getValue()) {
                Set<String> labels = new TreeSet<>(state.labels());
                states.add(new State(labels));
            }
            transitions.put(new State(transition.getKey().labels()), states);
        }
    }

    private void init() {
        minimum = false;
        transitions = new LinkedHashMap<>();
        acceptingStates = new HashSet<>();
    }

    public void addTransitions(State fromState, List<State> toStates) {
        transitions.put(fromState, toStates);
    }

    public void setVocabulary(List<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setInitialState(State state) {
        initialState = state;
    }

    public void addAcceptingState(State state) {
        acceptingStates.add(state);
    }

    public Set<State> acceptingStates() {
        return acceptingStates;
    }

    public void setAcceptingStates(Set<State> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }

    public List<String> vocabulary() {
        return vocabulary;
    }

    public State initial() {
        return initialState;
    }

    public Set<State> states() {
        return transitions.keySet();
    }

    public State transitionFrom(State fromState, String symbol) {
        int index = vocabulary.indexOf(symbol);
        return transitions.get(fromState).get(index);
    }

    public State transitionFrom(State state, int index) {
        return transitions.get(state).get(index);
    }

    public List<State> transitionsFrom(State state) {
        return transitions.get(state);
    }

    public Set<State> transitionsTo(State state) {
        Set<State> states = new HashSet<>();
        for (Map.Entry<State, List<State>> transition : transitions.entrySet()) {
            if (transition.getValue().contains(state)) {
                states.add(transition.getKey());
            }
        }
        return states;
    }

    public State epsilonClosure(State fromState) {
        Queue<String> pendingLabels = new LinkedList<>();
        Set<String> closure = new TreeSet<>();
        State stateFound = null;
        int index = vocabulary.indexOf(EPSILON);

        closure.addAll(fromState.labels());
        pendingLabels.addAll(fromState.labels());

        while (!pendingLabels.isEmpty()) {
            String label = pendingLabels.poll();
            stateFound = transitionFrom(new State(label), index);
            if (!stateFound.equals(State.ERROR_STATE)) {
                closure.addAll(stateFound.labels());
                pendingLabels.addAll(stateFound.labels());
            }
        }
        return new State(closure);
    }

    public void print() {
        StringBuilder sbSymbols = new StringBuilder();
        StringBuilder sbLines = new StringBuilder();

        sbSymbols.append("  S F     |");
        sbLines.append("----------+");

        for (String symbol : vocabulary) {
            sbSymbols.append("  ");
            sbSymbols.append(symbol);
            sbLines.append("----");
        }

        sbLines.append("-");
        System.out.println(sbSymbols);
        System.out.println(sbLines);

        for (State state : transitions.keySet()) {
            if (state.equals(initialState)) {
                System.out.print(" ->");
            } else {
                System.out.print("   ");
            }
            if (acceptingStates.contains(state)) {
                System.out.print(" *");
            } else {
                System.out.print("  ");
            }
            System.out.print(" " + state + " |");

            for (State toState : transitions.get(state)) {
                System.out.print(" " + toState);
            }
            System.out.println();
        }
        System.out.println(sbLines);
    }

    public String nextLabel() {
        int lastPosition = label.size() - 1;
        char symbol;

        for (int i = lastPosition; i >= 0; i--) {
            symbol = (char) (1 + label.get(i));
            if (symbol > 'Z') {
                label.set(i, 'A');
                if (i == 0) {
                    label.add(0, 'A');
                }
            } else {
                label.set(i, symbol);
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Character c : label) {
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean isNonDeterministic() {
        boolean nonDeterministic = false;
        for (List<State> states : transitions.values()) {
            for (State state : states) {
                if (state.labels().size() > 1) {
                    nonDeterministic = true;
                    break;
                }
            }
            if (nonDeterministic) {
                break;
            }
        }
        return nonDeterministic;
    }

    public boolean isMinimum() {
        return minimum;
    }

    public void setMinimum(boolean minimum) {
        this.minimum = minimum;
    }

    public void removeDeadStates(Set<State> states) {
        if (states.contains(initialState)) {
            initialState = null;
        }
        for (State state : states) {
            transitions.keySet().removeAll(transitionsTo(state));
        }
        transitions.keySet().removeAll(states);
        System.out.println("Dead states removed: " + states);
    }

}
