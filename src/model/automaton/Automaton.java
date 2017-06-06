package model.automaton;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Automaton {

    private State initialState;
    private final State errorState;
    private List<Character> label;
    private Set<State> states;
    private Set<Character> vocabulary;
    private Set<Transition> transitions;
    private Set<State> acceptingStates;

    public Automaton(Set<Character> vocabulary) {
        this.vocabulary = vocabulary;
        errorState = new State("-");
        states = new HashSet<>();
        transitions = new HashSet<>();
        acceptingStates = new HashSet<>();
        label = new LinkedList<>();
        label.add(0, '@');
    }

    public void addTransition(State from, State to, Character c) {
        if (to == null) {
            transitions.add(new Transition(from, errorState, c));
        } else {
            transitions.add(new Transition(from, to, c));
            if (!to.equals(from)) {
                states.add(to);
            }
        }
    }

    public State getTransitionFrom(State fromState, Character symbol) {
        State state = null;
        for (Transition t : transitions) {
            if (t.getFrom().equals(fromState) && t.getSymbol().equals(symbol)) {
                state = t.getTo();
            }
        }
        return state;
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

    public void setInitialState(State state) {
        initialState = state;
    }

    public void addAcceptingState(State state) {
        acceptingStates.add(state);
        addState(state);
    }

    public void addState(State state) {
        states.add(state);
    }

    public void print() {
        Set<Character> sortedVocabulary = new LinkedHashSet<>(vocabulary);
        Set<State> sortedStates = new LinkedHashSet<>(states);

        StringBuilder sbSymbols = new StringBuilder();
        StringBuilder sbLines = new StringBuilder();

        sbSymbols.append("        |");
        sbLines.append("--------+");

        for (Character symbol : sortedVocabulary) {
            sbSymbols.append("  ");
            sbSymbols.append(symbol);
            sbLines.append("---");
        }
        sbLines.append("-");
        System.out.println(sbSymbols);
        System.out.println(sbLines);

        for (State state : sortedStates) {
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
            for (Character symbol : sortedVocabulary) {
                System.out.print("  " + getTransitionFrom(state, symbol).label());
            }
            System.out.println();
        }

        System.out.println(sbLines);
    }

}
