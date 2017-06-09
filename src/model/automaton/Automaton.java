package model.automaton;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Automaton {

    protected State initialState;
    protected List<Character> label;
    protected List<String> vocabulary;
    protected Set<State> acceptingStates;
    protected Map<State, List<State>> transitions;

    public Automaton() {
        init();
    }

    public Automaton(List<String> vocabulary) {
        this.vocabulary = vocabulary;
        init();
        label = new LinkedList<>();
        label.add(0, '@');
    }

    private void init() {
        transitions = new LinkedHashMap<>();
        acceptingStates = new HashSet<>();
    }
    
    public void addTransitions(State fromState, List<State> toStates) {
        transitions.put(fromState, toStates);
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

    public void setVocabulary(List<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setInitialState(State state) {
        initialState = state;
    }

    public void addAcceptingState(State state) {
        acceptingStates.add(state);
    }

    public void print() {
        StringBuilder sbSymbols = new StringBuilder();
        StringBuilder sbLines = new StringBuilder();

        sbSymbols.append("        |");
        sbLines.append("--------+");

        for (String symbol : vocabulary) {
            sbSymbols.append("  ");
            sbSymbols.append(symbol);
            sbLines.append("---");
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
            System.out.print(" " + state.label() + " |");

            for (State toState : transitions.get(state)) {
                System.out.print("  " + toState.label());
            }
            System.out.println();
        }
        System.out.println(sbLines);
    }

    public abstract Automaton minimize();

}
