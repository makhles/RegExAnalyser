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

    private String name;
    private boolean empty;
    private boolean minimum;
    private State initialState;
    private List<Character> label;
    private List<String> vocabulary;
    private Set<State> acceptingStates;
    private Map<State, List<State>> transitions;

    public Automaton(String name) {
        this.name = name;
        init();
    }

    public Automaton(String name, List<String> vocabulary) {
        this.name = name;
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
        label = automaton.label;
    }

    private void init() {
        empty = false;
        minimum = false;
        transitions = new LinkedHashMap<>();
        acceptingStates = new HashSet<>();
    }

    public void addTransitions(State fromState, List<State> toStates) {
        transitions.put(fromState, toStates);
    }

    public List<State> removeTransitions(State fromState) {
        return transitions.remove(fromState);
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
        State state = null;
        int index = vocabulary.indexOf(symbol);
        if (index == -1) {
            state = State.ERROR_STATE;
        } else {
            state = transitions.get(fromState).get(index);
        }
        return state;
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
        if (hasEpsilonTransitions()) {
            nonDeterministic = true;
        } else {
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
        }
        return nonDeterministic;
    }

    public boolean hasEpsilonTransitions() {
        return vocabulary.contains(EPSILON);
    }

    public boolean isMinimum() {
        return minimum;
    }

    public void setMinimum(boolean minimum) {
        this.minimum = minimum;
    }

    public void removeDeadStates(Set<State> deadStates) {
        if (deadStates.contains(initialState)) {
            initialState = null;
            empty = true;
        }

        // Remove transitions from the dead states
        transitions.keySet().removeAll(deadStates);

        // Remove transitions to the dead states
        for (Map.Entry<State, List<State>> transition : transitions.entrySet()) {
            for (State state : deadStates) {
                int index = transition.getValue().indexOf(state);
                // The current state might transition to a dead state
                // through more than one symbol
                while (index != -1) {
                    transition.getValue().remove(index);
                    transition.getValue().add(index, State.ERROR_STATE);
                    index = transition.getValue().indexOf(state);
                }
            }
        }
    }

    /**
     * Returns a copy of this automaton with all states renamed to labels not in
     * use by the automaton passed as parameter.
     * 
     * @param automaton
     *            - the automaton which shall provide the labels.
     * @return the copied automaton.
     */
    public Automaton renameTupleStatesToSingleState() {
        Automaton renamed = new Automaton(name, vocabulary);
        Map<State, State> renamingMap = new LinkedHashMap<>();
        Set<String> usedLabels = usedLabels();
        String nextLabel = nextLabel();

        for (Map.Entry<State, List<State>> transition : transitions.entrySet()) {
            State renamedFromState = renamingMap.get(transition.getKey());
            if (renamedFromState == null) {
                while (usedLabels.contains(nextLabel)) {
                    nextLabel = nextLabel();
                }
                renamedFromState = new State(nextLabel);
                renamingMap.put(transition.getKey(), renamedFromState);
                usedLabels.add(nextLabel);
            }
            if (acceptingStates.contains(transition.getKey())) {
                renamed.addAcceptingState(renamedFromState);
            }
            List<State> renamedToStates = new ArrayList<>();
            for (State toState : transition.getValue()) {
                State renamedToState = renamingMap.get(toState);
                if (renamedToState == null) {
                    if (toState.equals(State.ERROR_STATE)) {
                        renamedToState = State.ERROR_STATE;
                    } else {
                        while (usedLabels.contains(nextLabel)) {
                            nextLabel = nextLabel();
                        }
                        renamedToState = new State(nextLabel);
                        renamingMap.put(toState, renamedToState);
                        usedLabels.add(nextLabel);
                    }
                }
                renamedToStates.add(renamedToState);
            }
            renamed.addTransitions(renamedFromState, renamedToStates);
        }
        renamed.setInitialState(renamingMap.get(initialState));
        renamed.setMinimum(minimum);
        return renamed;
    }

    public Automaton renameStatesBasedOn(Automaton automaton) {
        Automaton renamedAutomaton = new Automaton(name + " (renamed)", vocabulary);
        Map<String, String> labelMapping = new LinkedHashMap<>();
        Map<State, State> stateMapping = new LinkedHashMap<>();
        Set<String> usedLabels = automaton.usedLabels();
        String nextLabel = automaton.nextLabel();

        // Loop over states
        for (State state : states()) {
            if (state.labels().size() != 1) {
                System.err.println("fromState contains more than one label.");
                System.exit(1);
            }
            while (usedLabels.contains(nextLabel)) {
                nextLabel = automaton.nextLabel();
            }
            usedLabels.add(nextLabel);
            String previousLabel = null;
            for (String label : state.labels()) {
                previousLabel = label;
            }
            labelMapping.put(previousLabel, nextLabel);
            State renamedState = new State(nextLabel);
            stateMapping.put(state, renamedState);

            // Initial state
            if (state.equals(initialState)) {
                renamedAutomaton.setInitialState(renamedState);
            }

            // Accepting state
            if (acceptingStates.contains(state)) {
                renamedAutomaton.addAcceptingState(renamedState);
            }
        }

        // Loop over transitions
        for (State fromState : states()) {
            List<State> renamedToStates = new ArrayList<>();
            for (State toState : transitions.get(fromState)) {
                Set<String> renamedLabels = new TreeSet<>();
                if (toState.equals(State.ERROR_STATE)) {
                    renamedToStates.add(State.ERROR_STATE);
                } else {
                    for (String previousLabel : toState.labels()) {
                        renamedLabels.add(labelMapping.get(previousLabel));
                    }
                    renamedToStates.add(new State(renamedLabels));
                }
            }
            renamedAutomaton.addTransitions(stateMapping.get(fromState), renamedToStates);
        }
        renamedAutomaton.setMinimum(false);
        return renamedAutomaton;
    }

    /**
     * Returns all the labels used in this automaton's states.
     * 
     * @return the set of labels alphabetically ordered.
     */
    public Set<String> usedLabels() {
        Set<String> labels = new TreeSet<>();
        for (State state : states()) {
            labels.addAll(state.labels());
        }
        return labels;
    }

    public void makeComplete() {
        Map<State, List<State>> newTransitions = new HashMap<>();
        Set<String> usedLabels = usedLabels();
        String label = nextLabel();
        List<State> toStates = null;
        boolean incomplete = false;

        // Get a new avaialable label for the error state
        while (usedLabels.contains(label)) {
            label = nextLabel();
        }
        State errorState = new State(label);

        // Substitute transitions to error state
        for (Map.Entry<State, List<State>> transition : transitions.entrySet()) {
            toStates = new ArrayList<>(vocabulary.size());
            for (State state : transition.getValue()) {
                if (state.labels().contains("-")) {
                    toStates.add(errorState);
                    incomplete = true;
                } else {
                    toStates.add(state);
                }
            }
            newTransitions.put(transition.getKey(), toStates);
        }

        if (incomplete) { // TODO: turn into class field
            // Create the transition to the error state
            toStates = new ArrayList<>(vocabulary.size());
            for (int i = 0; i < vocabulary.size(); i++) {
                toStates.add(errorState);
            }
            newTransitions.put(errorState, toStates);

            // Replace current transitions
            transitions.clear();
            for (Map.Entry<State, List<State>> transition : newTransitions.entrySet()) {
                addTransitions(transition.getKey(), transition.getValue());
            }
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
