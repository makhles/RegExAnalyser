package controller;

import model.automaton.Automaton;
import model.regex.RegExParser;

public class Controller {

    private RegExParser parser;
    private Automaton automaton;

    public Controller() {
        parser = RegExParser.instance();
    }
    
    public void convertRegExToAutomaton(String input) {
        automaton = parser.parse(input).convertToDFA();
        automaton.print();
    }
}
