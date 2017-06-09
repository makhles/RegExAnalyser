package model.automaton;

import java.util.List;

public class NDFAe extends Automaton {

    public NDFAe() {
        super();
    }

    public NDFAe(List<String> vocabulary) {
        super(vocabulary);
    }


    public Automaton toDFA() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Automaton minimize() {
        Automaton dfa = toDFA();
        return dfa;  //TODO minimize dfa
    }
}
