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
     * @param transitions
     *            - the transition table
     * @param initialState
     *            - the initial state
     * @return an index to this automaton.
     */
    public int createAutomaton(List<List<String>> transitions, String initialState) {
        List<State> toStates = null;
        Set<String> labels = null;
        State fromState = null;

        checkForConsistency(transitions, initialState); // TODO

        Automaton automaton = new Automaton(transitions.get(0));
        automaton.setInitialState(new State(initialState));

        for (int row = 1; row < transitions.size(); row++) {
            fromState = new State(transitions.get(row).get(1));
            toStates = new ArrayList<>();
            if (transitions.get(row).get(0) == "*") {
                automaton.addAcceptingState(fromState);
            }
            for (int col = 2; col < transitions.get(row).size(); col++) {
                labels = new HashSet<>();
                Collections.addAll(labels, transitions.get(row).get(col).trim().split("\\s*,\\s*"));
                if (labels.contains("-")) {
                    toStates.add(State.ERROR_STATE);
                } else {
                    toStates.add(new State(labels));
                }
            }
            automaton.addTransitions(fromState, toStates);
        }
        checkForCompleteness(automaton); // TODO
        return addAutomaton(automaton);
    }

    private void checkForConsistency(List<List<String>> transitions, String initialState) {
        // TODO Auto-generated method stub

    }

    private void checkForCompleteness(Automaton automaton) {
        // TODO Auto-generated method stub

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
            index = addAutomaton(equivalent.renameTupleStatesToSingleState());
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
            Automaton renamed = dfa.renameTupleStatesToSingleState();
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

    public int complement(int index) {
        Automaton automaton = automatons.get(index);
        Automaton complement = null;

        if (automaton.isNonDeterministic() || automaton.hasEpsilonTransitions()) {
            complement = automatons.get(convertNFAtoDFA(index));
        } else {
            complement = new Automaton(automaton);
        }

        complement.makeComplete();

        Set<State> nonAccepting = new HashSet<>();
        nonAccepting.addAll(complement.states());
        nonAccepting.removeAll(complement.acceptingStates());
        complement.acceptingStates().clear();

        for (State state : nonAccepting) {
            complement.addAcceptingState(state);
        }

        return addAutomaton(complement);
    }

    public int union(int indexA, int indexB) {
        Automaton automatonA = new Automaton(automatons.get(indexA));
        Automaton automatonB = automatons.get(indexB);

        // Renames the states of B based on states of A
        automatonB = automatonB.renameStatesBasedOn(automatonA);
        indexB = addAutomaton(automatonB);
        System.out.println("States from the second automaton were renamed. New automaton:");
        printAutomaton(indexB);

        // Vocabulary
        Set<String> vocabulary = new LinkedHashSet<>();
        vocabulary.addAll(automatonA.vocabulary());
        vocabulary.addAll(automatonB.vocabulary());
        vocabulary.add(Automaton.EPSILON);

        Automaton automaton = new Automaton(new ArrayList<>(vocabulary));

        // Transitions
        copyTransitions(automatonA, automaton);
        copyTransitions(automatonB, automaton);

        // Initial state goes to A's and B's initial states through
        // Epsilon-moves
        List<State> transitions = new ArrayList<>(automaton.vocabulary().size());
        Set<String> labels = new TreeSet<>();
        labels.addAll(automatonA.initial().labels());
        labels.addAll(automatonB.initial().labels());

        int epsilonIndex = 0, index = 0;
        for (String symbol : automaton.vocabulary()) {
            if (symbol.equals(Automaton.EPSILON)) {
                transitions.add(new State(labels));
                epsilonIndex = index;
            } else {
                transitions.add(State.ERROR_STATE);
            }
            index++;
        }

        Set<String> usedLabels = automaton.usedLabels();
        String initialLabel = automaton.nextLabel();
        while (usedLabels.contains(initialLabel)) {
            initialLabel = automaton.nextLabel();
        }
        usedLabels.add(initialLabel);

        State initial = new State(initialLabel);
        automaton.setInitialState(initial);
        automaton.addTransitions(initial, transitions);

        // Gather accepting states from A and B
        Set<State> accepting = new HashSet<>();
        for (State state : automaton.states()) {
            if (automatonA.acceptingStates().contains(state) || automatonB.acceptingStates().contains(state)) {
                accepting.add(state);
            }
        }

        // Create a new accepting state
        String acceptingLabel = automaton.nextLabel();
        while (usedLabels.contains(acceptingLabel)) {
            acceptingLabel = automaton.nextLabel();
        }
        State acceptingState = new State(acceptingLabel);
        transitions = new ArrayList<>(automaton.vocabulary().size());

        for (int i = 0; i < automaton.vocabulary().size(); i++) {
            transitions.add(State.ERROR_STATE);
        }
        automaton.addAcceptingState(acceptingState);
        automaton.addTransitions(acceptingState, transitions);

        // Make all previous accepting states go to new accepting state
        for (State state : accepting) {
            List<State> toStates = automaton.removeTransitions(state);
            toStates.remove(epsilonIndex);
            toStates.add(epsilonIndex, acceptingState);
            automaton.addTransitions(state, toStates);
        }

        return addAutomaton(automaton);
    }

    /**
     * Copies the transitions from the source automaton to the destination
     * automaton.
     * 
     * @param source
     *            - the automaton whose transitions shall be copied.
     * @param destination
     *            - the automaton which shall receive the transitions.
     */
    private void copyTransitions(Automaton source, Automaton destination) {
        for (State state : source.states()) {
            List<State> transitions = new ArrayList<>(destination.vocabulary().size());
            for (String symbol : destination.vocabulary()) {
                transitions.add(new State(source.transitionFrom(state, symbol).labels()));
            }
            destination.addTransitions(new State(state.labels()), transitions);
        }
    }

    public void printAutomaton(int index) {
        System.out.println();
        automatons.get(index).print();
        System.out.println();
    }
}
