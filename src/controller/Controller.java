package controller;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.automaton.Automaton;
import model.automaton.NDFA;
import model.automaton.NDFAe;
import model.automaton.State;
import model.regex.RegExParser;

public class Controller {

    private static Controller instance = new Controller();
    private Automaton lastAutomaton;

    private List<Automaton> automatons;

    private Controller() {
    }

    public static Controller instance() {
        return instance;
    }

    public void convertRegExToAutomaton(String input) {
        Automaton automaton = new RegExParser(input).parse().convertToDFA();
        automaton.print();
    }

    public void createAutomaton(List<Boolean> initial, List<Boolean> accepting, List<List<String>> inputTable) {
        AutomatonBuilder builder = new AutomatonBuilder();
        builder.addVocabulary(inputTable.get(0).subList(1, inputTable.get(0).size()));

        for (int row = 1; row < inputTable.size(); row++) {
            List<String> rowSymbols = inputTable.get(row);
            List<String> toLabels = rowSymbols.subList(1, rowSymbols.size());
            builder.addTransitions(rowSymbols.get(0), toLabels, initial.get(row - 1), accepting.get(row - 1));
        }
        lastAutomaton = builder.buildAutomaton();
        automatons.add(lastAutomaton);
    }

    public void convertNDFAtoDFA(int index) {
        Automaton automaton = automatons.get(index);
        if (automaton instanceof NDFA) {
            NDFA ndfa = (NDFA) automaton;
            automatons.add(ndfa.toDFA());
        } else if (automaton instanceof NDFAe) {
            NDFAe ndfae = (NDFAe) automaton;
            automatons.add(ndfae.toDFA());
        } else {
            System.out.println("Automaton is already a DFA.");
        }
    }

    public void printAutomaton() {
        lastAutomaton.print();
    }
}
