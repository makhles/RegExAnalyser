package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import model.automaton.Automaton;
import model.automaton.State;
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
     * Creates an automaton based on the given table and initial state. The table should be
     * formatted as such: the first row contains the vocabulary. The second and following rows have
     * a "*" in the first column if that is an accepting state, followed by the state label in the
     * second column. In each subsequent column is the state to which the first state of that row
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
                labels = new TreeSet<>();
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

    public int convertNFAtoDFA(int index) {
        Automaton nfa = automatons.get(index);
        List<String> vocabulary = new ArrayList<>(nfa.vocabulary());
        Map<State, State> closures = null;
        boolean hasEpsilon = vocabulary.remove(Automaton.EPSILON);
        Automaton dfa = new Automaton(vocabulary);

        if (hasEpsilon) {
            closures = epsilonClosuresFor(nfa);
        }
        
        Set<State> dfaStates = new LinkedHashSet<>();  // Only used to avoid creating duplicated states
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
                for (String label : currentState.id()) {
                    toState = nfa.transitionFrom(new State(label), symbol);
                    if (!toState.equals(State.ERROR_STATE)) {
                        labels.addAll(toState.id());
                    }
                }
                if (!labels.isEmpty()) {  // labels is empty if there were only error states
                    if (hasEpsilon) {
                        Set<String> updatedLabels = new TreeSet<>();
                        for (String toLabel : labels) {
                            updatedLabels.addAll(closures.get(new State(toLabel)).id());
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
                for (String dfaStateLabel : currentState.id()) {
                    if (nfaAcceptingState.id().contains(dfaStateLabel)) {
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
        return addAutomaton(dfa);
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
