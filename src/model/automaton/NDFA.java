package model.automaton;

import java.util.List;

public class NDFA extends Automaton {

    public NDFA() {
        super();
    }

    public NDFA(List<String> vocabulary) {
        super(vocabulary);
    }


    public Automaton toDFA() {
        // TODO Auto-generated method stub
        Automaton dfa = new DFA();
        dfa.setVocabulary(vocabulary);
        dfa.setInitialState(initialState);
        
        return null;
    }

    @Override
    public Automaton minimize() {
        Automaton dfa = toDFA();
        return dfa;  //TODO minimize dfa
    }
}
