package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                labels = new TreeSet<>();
                Collections.addAll(labels, table.get(row).get(col).trim().split("\\s*,\\s*"));
                toStates.add(new State(labels));
            }
            lastAutomaton.addTransitions(fromState, toStates);
        }
        return addAutomaton(lastAutomaton);
    }

    // /**
    // * Creates a new automaton.
    // * @param vocabulary - the list of symbols.
    // * @param transitionsTable - the table of transitions.
    // * @param accepting - the list with the accepting states.
    // * @param initial - the initial state.
    // * @return The id of this automaton.
    // */
    // public int createAutomaton(List<String> vocabulary, List<List<String>>
    // transitionsTable, List<String> accepting,
    // String initial) {
    // lastAutomaton = new Automaton(vocabulary);
    // for (int row = 1; row < transitionsTable.size(); row++) {
    // Set<String> fromState = new TreeSet<>();
    // fromState.add(transitionsTable.get(row).get(0));
    // if (accepting.contains(fromState)) {
    // lastAutomaton.addAcceptingState(fromState);
    // }
    // List<Set<String>> toStates = new ArrayList<>();
    // for (int col = 1; col < transitionsTable.get(row).size(); col++) {
    // Set<String> toStateSet = new TreeSet<>();
    // String toState = transitionsTable.get(row).get(col);
    // Collections.addAll(toStateSet, toState.trim().split("\\s*,\\s*"));
    // toStates.add(toStateSet);
    // }
    // lastAutomaton.addTransitions(fromState, toStates);
    // }
    // Set<String> initialState = new TreeSet<>();
    // initialState.add(initial);
    // lastAutomaton.setInitialState(initialState);
    // return addAutomaton(lastAutomaton);
    // }

    /**
     * Adds the automaton to the list of automatons and return its index.
     * 
     * @param automaton
     *            - the automaton to be added.
     * @return the index of the newly added automaton in the list.
     */
    private int addAutomaton(Automaton automaton) {
        automatons.add(lastAutomaton);
        return automatons.size() - 1;
    }

    public int convertNDFAtoDFA(int index) {
        Automaton ndfa = automatons.get(index);
        Automaton dfa = null;
        boolean epsilonFree = true;
        List<String> vocabulary = ndfa.vocabulary();
        int epsilon = vocabulary.indexOf(Automaton.EPSILON);

        if (epsilon != -1) {
            epsilonFree = false;
            // Finds the Epsilon-closure
            Map<State,State> closures = new HashMap<>();
            for (State state : ndfa.states()) {
                State closure = ndfa.epsilonClosure(state);
                closures.put(state, closure);
                System.out.println(closure);
            }
        }
        
        
        return 0;
    }

    public void printAutomaton(int index) {
        automatons.get(index).print();
        System.out.println();
    }
}
