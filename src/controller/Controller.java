package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import model.automaton.Automaton;
import model.automaton.State;
import model.exception.AutomatonAlreadyDeterministicException;
import model.exception.AutomatonAlreadyMinimumException;
import model.regex.RegExParser;

public class Controller {

    private static Controller instance = new Controller();
    private Automaton lastAutomaton;

    private List<Automaton> automatons;

    private Controller() {
        automatons = new ArrayList<>();
    }

    public static Controller instance() {
        return instance;
    }

    public void convertRegExToAutomaton(String input) {
        Automaton automaton = new RegExParser(input).parse().convertToDFA();
        automaton.print();
    }

    /**
     * Creates an automaton based on the given table and initial state. The
     * table should be formatted as such: the first row contains the vocabulary.
     * The second and following rows have a "*" in the first column if that is
     * an accepting state, followed by the state label in the second column. In
     * each subsequent column is the state to which the first state of that row
     * goes to through the symbol in the respective column of the first row.
     * 
     * @param table
     *            - the transition table
     * @param initialState
     *            - the initial state
     * @return an index to this automaton.
     */
    public int createAutomaton(List<List<String>> table, String initialState) {
        List<State> toStates = null;
        Set<String> labels = null;
        State fromState = null;

        lastAutomaton = new Automaton(table.get(0));
        lastAutomaton.setInitialState(new State(initialState));

        for (int row = 1; row < table.size(); row++) {
            fromState = new State(table.get(row).get(1));
            toStates = new ArrayList<>();
            if (table.get(row).get(0) == "*") {
                lastAutomaton.addAcceptingState(fromState);
            }
            for (int col = 2; col < table.get(row).size(); col++) {
                labels = new HashSet<>();
                Collections.addAll(labels, table.get(row).get(col).trim().split("\\s*,\\s*"));
                toStates.add(new State(labels));
            }
            lastAutomaton.addTransitions(fromState, toStates);
        }
        return addAutomaton(lastAutomaton);
    }

    /**
     * Adds the automaton to the list of automatons and return its index.
     * 
     * @param automaton
     *            - the automaton to be added.
     * @return the index of the newly added automaton in the list.
     */
    private int addAutomaton(Automaton automaton) {
        automatons.add(automaton);
        return automatons.size() - 1;
    }

    public int minimize(int index) throws AutomatonAlreadyMinimumException {
        Automaton automaton = automatons.get(index);
        if (automaton.isNonDeterministic()) {
            index = convertNFAtoDFA(index);
            automaton = automatons.get(index);
        } else if (automaton.isMinimum()) {
            throw new AutomatonAlreadyMinimumException();
        }

        Automaton minimum = new Automaton(automaton);
        int statesBefore = minimum.states().size();
        removeUnreachableStates(minimum);
        removeDeadStates(minimum);
        minimum = mergeEquivalentStates(minimum);

        // User might have given the minimum automaton
        if (minimum.states().size() == statesBefore) {
            throw new AutomatonAlreadyMinimumException();
        }

        return addAutomaton(minimum);
    }

    private void removeUnreachableStates(Automaton automaton) {
        Set<State> unreachable = new HashSet<>(automaton.states());
        Queue<State> toBeVisited = new LinkedList<>();

        toBeVisited.add(automaton.initial());
        unreachable.remove(automaton.initial());

        while (!toBeVisited.isEmpty()) {
            State currentState = toBeVisited.poll();
            for (State state : automaton.transitionsFrom(currentState)) {
                if (unreachable.contains(state)) {
                    unreachable.remove(state);
                    toBeVisited.add(state);
                }
            }
        }
        System.out.println("Unreachable states removed: " + unreachable);
        automaton.acceptingStates().removeAll(unreachable);
        automaton.states().removeAll(unreachable);
    }

    private void removeDeadStates(Automaton automaton) {
        Set<State> dead = new HashSet<>(automaton.states());
        Queue<State> toBeVisited = new LinkedList<>();

        toBeVisited.addAll(automaton.acceptingStates());
        dead.removeAll(automaton.acceptingStates());

        while (!toBeVisited.isEmpty()) {
            State currentState = toBeVisited.poll();
            for (State state : automaton.transitionsTo(currentState)) {
                if (dead.contains(state)) {
                    dead.remove(state);
                    toBeVisited.add(state);
                }
            }
        }
        automaton.removeDeadStates(dead);
    }

    private Automaton mergeEquivalentStates(Automaton automaton) {
        Set<List<State>> classes = new HashSet<>();
        List<State> nonAccepting = new ArrayList<>(automaton.states());

        nonAccepting.removeAll(automaton.acceptingStates());
        classes.add(new ArrayList<>(automaton.acceptingStates()));
        classes.add(new ArrayList<>(nonAccepting));
        
        // TODO: error state

        boolean setsChanged = true;
        while (classes.size() != automaton.states().size() && setsChanged) {
            setsChanged = false;
            Map<List<State>, List<State>> castOutMapping = new HashMap<>();
            List<State> castOut = null;
            for (List<State> equivalentClass : classes) {
                if (equivalentClass.size() > 1) {
                    State current = equivalentClass.get(0);
                    castOut = null;
                    for (int i = 0; i < equivalentClass.size() - 1; i++) {
                        State next = equivalentClass.get(i + 1);
                        for (String symbol : automaton.vocabulary()) {
                            State fromCurrent = automaton.transitionFrom(current, symbol);
                            State fromNext = automaton.transitionFrom(next, symbol);
                            boolean notTheSameSet = true;
                            for (List<State> set : classes) {
                                if (set.contains(fromCurrent) && set.contains(fromNext)) {
                                    notTheSameSet = false;
                                    break;
                                }
                            }
                            if (notTheSameSet) {
                                if (castOut == null) {
                                    castOut = new ArrayList<>();
                                }
                                castOut.add(next);
                                setsChanged = true;
                                break;
                            }
                        }
                    }
                }
                if (setsChanged) {
                    castOutMapping.put(equivalentClass, castOut);
                }
            }
            // Remove states that do not belong to each equivalence class
            for (Map.Entry<List<State>, List<State>> entry : castOutMapping.entrySet()) {
                entry.getKey().removeAll(entry.getValue());
                classes.add(entry.getValue());
            }
        }

        // Each equivalent class is a state. Ex.:
        // {A, B} becomes H
        // {C}    becomes I
        // {D, E} becomes J
        //
        // statesMapping maps each previous state to the new state (class):
        // A --> H;   B --> H;   C --> I
        // D --> J;   E --> J
        Map<State, State> statesMapping = new HashMap<>();
        for (List<State> toClass : classes) {
            Set<String> labels = new TreeSet<>();
            for (State state : toClass) {
                labels.addAll(state.labels());
            }
            for (State state : toClass) {
                statesMapping.put(state, new State(labels));
            }
        }

        Automaton equivalent = new Automaton(automaton.vocabulary());
        List<State> transitions = null;

        for (List<State> equivalentClass : classes) {
            transitions = new ArrayList<>();
            State representative = equivalentClass.get(0);
            for (String symbol : automaton.vocabulary()) {
                State toState = automaton.transitionFrom(representative, symbol);
                transitions.add(statesMapping.get(toState));
            }
            equivalent.addTransitions(statesMapping.get(representative), transitions);
        }
        for (State state : automaton.acceptingStates()) {
            equivalent.addAcceptingState(statesMapping.get(state));
        }
        equivalent.setInitialState(statesMapping.get(automaton.initial()));
        equivalent.setMinimum(true);
        System.out.println("Number of equivalent classes: " + equivalent.states().size());
        return equivalent;
    }

    public int convertNFAtoDFA(int index) throws AutomatonAlreadyDeterministicException {
        Automaton nfa = automatons.get(index);
        int indexToReturn = -1;

        if (nfa.isNonDeterministic()) {
            List<String> vocabulary = new ArrayList<>(nfa.vocabulary());
            Map<State, State> closures = null;
            boolean hasEpsilon = vocabulary.remove(Automaton.EPSILON);
            Automaton dfa = new Automaton(vocabulary);

            if (hasEpsilon) {
                closures = epsilonClosuresFor(nfa);
            }

            Set<State> dfaStates = new LinkedHashSet<>(); // Only used to avoid
                                                          // creating duplicated
                                                          // states
            Queue<State> pendingStates = new LinkedList<>();
            State initialState = new State(nfa.initial());
            pendingStates.add(initialState);
            dfaStates.add(initialState);
            dfa.setInitialState(initialState);

            while (!pendingStates.isEmpty()) {
                State currentState = pendingStates.poll();
                List<State> transitions = new ArrayList<>();
                for (String symbol : vocabulary) {
                    Set<String> labels = new TreeSet<>();
                    State toState = null;
                    for (String label : currentState.labels()) {
                        toState = nfa.transitionFrom(new State(label), symbol);
                        if (!toState.equals(State.ERROR_STATE)) {
                            labels.addAll(toState.labels());
                        }
                    }
                    if (!labels.isEmpty()) { // labels is empty if there were
                                             // only error states
                        if (hasEpsilon) {
                            Set<String> updatedLabels = new TreeSet<>();
                            for (String toLabel : labels) {
                                updatedLabels.addAll(closures.get(new State(toLabel)).labels());
                            }
                            toState = new State(updatedLabels);
                        } else {
                            toState = new State(labels);
                        }
                    }
                    if (!toState.equals(State.ERROR_STATE) && dfaStates.add(toState)) {
                        pendingStates.add(toState);
                    }
                    transitions.add(toState);
                }
                dfa.addTransitions(currentState, transitions);
                for (State nfaAcceptingState : nfa.acceptingStates()) {
                    boolean acceptingState = false;
                    for (String dfaStateLabel : currentState.labels()) {
                        if (nfaAcceptingState.labels().contains(dfaStateLabel)) {
                            dfa.addAcceptingState(currentState);
                            acceptingState = true;
                            break;
                        }
                    }
                    if (acceptingState) {
                        break;
                    }
                }
                indexToReturn = addAutomaton(dfa);
            }
        } else {
            throw new AutomatonAlreadyDeterministicException();
        }
        return indexToReturn;
    }

    private Map<State, State> epsilonClosuresFor(Automaton nfa) {
        Map<State, State> closures = new HashMap<>();
        System.out.println("NDFAe closure:");
        for (State state : nfa.states()) {
            State closure = nfa.epsilonClosure(state);
            closures.put(state, closure);
            System.out.println(closure);
        }
        System.out.println();
        return closures;
    }

    public void printAutomaton(int index) {
        System.out.println("Automaton:");
        automatons.get(index).print();
        System.out.println();
    }
}
