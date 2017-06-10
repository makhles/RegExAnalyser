package model.automaton;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Automaton {

    public final static String ERROR_STATE = "-";
    public final static String EPSILON = "&";

    protected String initialState;
    protected List<Character> label;
    protected List<String> vocabulary;
    protected Set<String> acceptingStates;
    protected Map<String, List<Set<String>>> transitions;

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

    public void addTransitions(String fromString, List<Set<String>> toStates) {
        transitions.put(fromString, toStates);
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

    public void setInitialState(String state) {
        initialState = state;
    }

    public void addAcceptingState(String state) {
        acceptingStates.add(state);
    }

    public void setTransitions(Map<String, List<Set<String>>> transitions) {
        this.transitions = transitions;
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

        for (String state : transitions.keySet()) {
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

            for (Set<String> toStates : transitions.get(state)) {
                System.out.print("  " + toStates);
            }
            System.out.println();
        }
        System.out.println(sbLines);
    }

    public Automaton minimize() {
        // TODO Auto-generated method stub
        return null;
    }

    public Automaton toDFA() {
        // TODO Auto-generated method stub
        return null;
    }

}
