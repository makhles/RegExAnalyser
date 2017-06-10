package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.automaton.Automaton;
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
     * Creates a new automaton.
     * @param vocabulary - the list of symbols.
     * @param transitionsTable - the table of transitions.
     * @param accepting - the list with the accepting states.
     * @param initial - the initial state.
     * @return The id of this automaton.
     */
    public int createAutomaton(List<String> vocabulary, List<List<String>> transitionsTable, List<String> accepting,
            String initial) {
        lastAutomaton = new Automaton(vocabulary);
        for (int row = 1; row < transitionsTable.size(); row++) {
            String fromState = transitionsTable.get(row).get(0);
            if (accepting.contains(fromState)) {
                lastAutomaton.addAcceptingState(fromState);
            }
            List<Set<String>> toStates = new ArrayList<>();
            for (int col = 1; col < transitionsTable.get(row).size(); col++) {
                Set<String> toStateSet = new TreeSet<>();
                String toState = transitionsTable.get(row).get(col);
                Collections.addAll(toStateSet, toState.trim().split("\\s*,\\s*"));
                toStates.add(toStateSet);
            }
            lastAutomaton.addTransitions(fromState, toStates);
        }
        lastAutomaton.setInitialState(initial);
        return addAutomaton(lastAutomaton);
    }

    /**
     * Adds the automaton to the list of automatons and return its index.
     * @param automaton - the automaton to be added.
     * @return the index of the newly added automaton in the list.
     */
    private int addAutomaton(Automaton automaton) {
        automatons.add(lastAutomaton);
        return automatons.size()-1;
    }

    public int convertNDFAtoDFA(int index) {
        return addAutomaton(automatons.get(index).toDFA());
    }

    public void printAutomaton() {
        lastAutomaton.print();
    }
}
