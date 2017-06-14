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
import model.exception.AutomatonIsEmptyException;
import model.regex.RegExParser;

public class Controller {

    private static final String NON_DETERMINISTIC = "Automaton is non-deterministic and shall be converted to a DFA:";
    private static Controller instance = new Controller();
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

        Automaton automaton = new Automaton(table.get(0));
        automaton.setInitialState(new State(initialState));

        for (int row = 1; row < table.size(); row++) {
            fromState = new State(table.get(row).get(1));
            toStates = new ArrayList<>();
            if (table.get(row).get(0) == "*") {
                automaton.addAcceptingState(fromState);
            }
            for (int col = 2; col < table.get(row).size(); col++) {
                labels = new HashSet<>();
                Collections.addAll(labels, table.get(row).get(col).trim().split("\\s*,\\s*"));
                if (labels.contains("-")) {
                    toStates.add(State.ERROR_STATE);
                } else {
                    toStates.add(new State(labels));
                }
            }
            automaton.addTransitions(fromState, toStates);
        }
        return addAutomaton(automaton);
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

    /**
     * Minimizes the given automaton. If the automaton is already minimum, a
     * {@link AutomatonAlreadyMinimumException} shall be thrown.
     * 
     * @param index
     *            - the index to the automaton in the list of automatons.
     * @return - the index to the minimized automaton.
     * @throws AutomatonAlreadyMinimumException
     */
    public int minimize(int index) throws AutomatonAlreadyMinimumException, AutomatonIsEmptyException {
        Automaton automaton = automatons.get(index);

        if (automaton.isNonDeterministic()) {
            System.out.println(NON_DETERMINISTIC);
            index = convertNFAtoDFA(index);
            automaton = automatons.get(index);
        } else if (automaton.isMinimum()) {
            throw new AutomatonAlreadyMinimumException();
        }

        // Creates a copy automaton so that it's safe to
        // remove unreachable and dead states
        index = addAutomaton(new Automaton(automaton));

        try {
            removeUnreachableStates(index);
            removeDeadStates(index);
            index = mergeEquivalentStates(index);
        } catch (Exception e) {
            throw e;
        }

        return index;
    }

    /**
     * Remove all unreachable states from the automaton.
     * 
     * @param automaton
     *            - the automaton from which any unreachable stat shall be
     *            removed.
     */
    private void removeUnreachableStates(int index) {
        System.out.print("Removing unreachable states... ");

        Automaton automaton = automatons.get(index);
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
        if (unreachable.isEmpty()) {
            System.out.println("nothing to be done.");
        } else {
            automaton.acceptingStates().removeAll(unreachable);
            automaton.states().removeAll(unreachable);
            System.out.println("done.");
            System.out.println("Unreachable states removed: " + unreachable);
            System.out.println("Resulting automaton:");
            printAutomaton(index);
        }
    }

    /**
     * Removes dead states from the automaton.
     * 
     * @param automaton
     *            - the automaton from which any dead states shall be removed.
     */
    private void removeDeadStates(int index) throws AutomatonIsEmptyException {
        System.out.print("Removing dead states... ");

        Automaton automaton = automatons.get(index);
        if (automaton.states().isEmpty()) {
            throw new AutomatonIsEmptyException();
        }

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
        if (dead.isEmpty()) {
            System.out.println("nothing to be done.");
        } else {
            automaton.removeDeadStates(dead);
            System.out.println("done.");
            System.out.println("Dead states removed: " + dead);
            System.out.println("Resulting automaton:");
            printAutomaton(index);
        }
    }

    private int mergeEquivalentStates(int index) throws AutomatonIsEmptyException, AutomatonAlreadyMinimumException {
        System.out.print("Merging equivalent states... ");

        Automaton automaton = automatons.get(index);
        if (automaton.states().isEmpty()) {
            throw new AutomatonIsEmptyException();
        }

        Set<List<State>> classes = new HashSet<>();
        List<State> nonAccepting = new ArrayList<>(automaton.states());

        nonAccepting.removeAll(automaton.acceptingStates());
        classes.add(new ArrayList<>(automaton.acceptingStates()));
        if (!nonAccepting.isEmpty()) {
            classes.add(new ArrayList<>(nonAccepting));
        }

        boolean needsAnotherPass = true;
        while (classes.size() != automaton.states().size() && needsAnotherPass) {
            needsAnotherPass = false;
            Map<List<State>, List<State>> castOutMapping = new HashMap<>();
            List<State> castOut = null;
            for (List<State> equivalentClass : classes) {
                boolean classChanged = false;
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
                                classChanged = true;
                                break;
                            }
                        }
                    }
                }
                if (classChanged) {
                    castOutMapping.put(equivalentClass, castOut);
                    needsAnotherPass = true;
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
        // {C} becomes I
        // {D, E} becomes J
        //
        // statesMapping maps each previous state to the new state (class):
        // A --> H; B --> H; C --> I
        // D --> J; E --> J
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
                if (toState.equals(State.ERROR_STATE)) {
                    transitions.add(State.ERROR_STATE);
                } else {
                    transitions.add(statesMapping.get(toState));
                }
            }
            equivalent.addTransitions(statesMapping.get(representative), transitions);
        }
        for (State state : automaton.acceptingStates()) {
            equivalent.addAcceptingState(statesMapping.get(state));
        }
        equivalent.setInitialState(statesMapping.get(automaton.initial()));
        equivalent.setMinimum(true);

        // User might have given the minimum automaton
        if (automaton.states().size() == equivalent.states().size()) {
            System.out.println("nothing to be done.");
            throw new AutomatonAlreadyMinimumException();
        } else {
            System.out.println("done.");
            System.out.println("Equivalent classes: " + classes);
            System.out.println("Resulting automaton:");
            printAutomaton(addAutomaton(equivalent));
            System.out.println("Renamed automaton:");
            index = addAutomaton(equivalent.renameStates());
            printAutomaton(index);
        }
        return index;
    }

    /**
     * Converts a NFA to a DFA through the De Simone tree method.
     * 
     * @param index
     *            - the index of the NFA.
     * @return the index of the DFA.
     * @throws AutomatonAlreadyDeterministicException
     */
    public int convertNFAtoDFA(int index) throws AutomatonAlreadyDeterministicException {
        Automaton nfa = automatons.get(index);
        boolean hasEpsilon = nfa.hasEpsilonTransitions();
        index = -1;

        if (nfa.isNonDeterministic() || hasEpsilon) {
            List<String> vocabulary = new ArrayList<>(nfa.vocabulary());
            Map<State, State> closures = null;

            if (hasEpsilon) {
                vocabulary.remove(Automaton.EPSILON);
                closures = epsilonClosuresFor(nfa);
            }
            Automaton dfa = new Automaton(vocabulary);

            // Only used to avoid creating duplicate states
            Set<State> dfaStates = new LinkedHashSet<>();

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
            }
            index = addAutomaton(dfa);
            printAutomaton(index);
            System.out.println("Renamed automaton:");
            Automaton renamed = dfa.renameStates();
            index = addAutomaton(renamed);
            printAutomaton(index);
        } else {
            throw new AutomatonAlreadyDeterministicException();
        }
        return index;
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

    public int union(int indexA, int indexB) {
        Automaton automatonA = automatons.get(indexA);
        Automaton automatonB = automatons.get(indexB);
        int index = unionIntersectionDifference(indexA, indexB);

        return index;
    }

    private int unionIntersectionDifference(int indexA, int indexB) {
        int index = -1;
        Automaton automatonA = automatons.get(indexA);
        Automaton automatonB = automatons.get(indexB);

        System.out.println("Analysing automaton A... ");
        if (automatonA.isNonDeterministic()) {
            System.out.println(NON_DETERMINISTIC);
            indexA = convertNFAtoDFA(indexA);
            automatonA = automatons.get(indexA);
        } else {
            System.out.println(" done.");
        }

        System.out.println("Analysing automaton B... ");
        if (automatonB.isNonDeterministic()) {
            System.out.println(NON_DETERMINISTIC);
            indexB = convertNFAtoDFA(indexB);
            automatonB = automatons.get(indexB);
        } else {
            System.out.println(" done.");
        }

        // Rename states in both automatons simultaneously
        // A '1' will be added to states in automatonA and
        // a '2' will be added to states in automatonB
        // TODO: continue...

        // Vocabulary
        Set<String> vocabulary = new TreeSet<>();
        vocabulary.addAll(automatonA.vocabulary());
        vocabulary.addAll(automatonB.vocabulary());

        Automaton automaton = new Automaton(new ArrayList<>(vocabulary));

        // Initial state
        Set<String> labels = new LinkedHashSet<>();
        labels.addAll(automatonA.initial().labels());
        labels.addAll(automatonB.initial().labels());
        State initial = new State(labels);

        // Transitions
        List<State> transitions = new ArrayList<>();
        Set<State> visited = new HashSet<>();
        Queue<State> pending = new LinkedList<>();
        pending.add(initial);
        
        while (!pending.isEmpty()) {

            State current = pending.poll();
            visited.add(current);
            
            for (String symbol : vocabulary) {
                
            }
        }
        
        // TODO: continue...

        return index;
    }

    public void printAutomaton(int index) {
        System.out.println();
        automatons.get(index).print();
        System.out.println();
    }
}
