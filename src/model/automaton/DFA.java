package model.automaton;

import java.util.List;

public class DFA extends Automaton {

    public DFA() {
        super();
    }

    public DFA(List<String> vocabulary) {
        super(vocabulary);
    }

    @Override
    public Automaton minimize() {
        return this;  //TODO minimize
    }
}
