package controller;

import java.util.ArrayList;
import java.util.List;

import model.automaton.Automaton;
import model.automaton.State;

public class AutomatonBuilder {
    private Automaton automaton;

    public AutomatonBuilder() {
        automaton = new Automaton();
    }

    public void addVocabulary(List<String> vocabulary) {
        automaton.setVocabulary(vocabulary);
    }

    public void addTransitions(String fromLabel, List<String> toLabels, boolean initial, boolean accepting) {
        List<State> toStates = new ArrayList<>();
        for (String label : toLabels) {
            if (label != null) {
                toStates.add(new State(label));
            } else {
                toStates.add(State.ERROR_STATE);
            }
        }
        State fromState = new State(fromLabel);
        automaton.addTransitions(fromState, toStates);
        if (initial) {
            automaton.setInitialState(fromState);
        }
        if (accepting) {
            automaton.addAcceptingState(fromState);
        }
    }

    public Automaton buildAutomaton() {
        return automaton;
    }
}
