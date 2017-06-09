package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import controller.AutomatonBuilder;
import controller.Controller;
import model.automaton.Automaton;
import model.regex.RegExParser;
import model.regex.RegExTree;

public class TestAutomaton {

    public static void main(String[] args) {
//        printAutomaton();
        createAutomaton();
    }

    private static void createAutomaton() {
        List<String> vocabulary = new ArrayList<>();
        vocabulary.add("");
        vocabulary.add("a");
        vocabulary.add("b");
        vocabulary.add("c");
        List<String> fromA = new ArrayList<>();
        fromA.add("A");
        fromA.add("A");
        fromA.add("B");
        fromA.add("C");
        List<String> fromB = new ArrayList<>();
        fromB.add("B");
        fromB.add("B");
        fromB.add("-");
        fromB.add("C");
        List<String> fromC = new ArrayList<>();
        fromC.add("C");
        fromC.add("B");
        fromC.add("A");
        fromC.add("C");
        List<List<String>> table = new ArrayList<>();
        table.add(vocabulary);
        table.add(fromA);
        table.add(fromB);
        table.add(fromC);
        List<Boolean> initial = new ArrayList<>();
        initial.add(true);
        initial.add(false);
        initial.add(false);
        List<Boolean> accepting = new ArrayList<>();
        accepting.add(false);
        accepting.add(true);
        accepting.add(true);
        Controller control = Controller.instance();
        control.createAutomaton(initial, accepting, table);
        control.printAutomaton();
    }

    private static void printAutomaton() {
        String input = "(ab|ac)*a?|(ba?c)*";
//        String input = "(a|b)*");
//        String input = "a*(b?c|d)*");
//        String input = "(b*ab*ab*ab*)*ab*a?b*");
//        String input = "(a|b)*c");
//        String input = "a?(ba)*b?");
//        String input = "(ab|b(ab)*b)*(ba)*");
//        String input = "b?(ab?ab?)*ab?");
//        String input = "l(u?d|u?l)*");
//        String input = "(0|1(01*0)*1)+");
//        String input = "1?1?(0??011?)*0?0?");
        RegExTree tree = new RegExParser(input).parse();  //OK
        System.out.println("Generated tree: " + tree);
        System.out.println();

        Automaton dfa = tree.convertToDFA();
        System.out.println("DFA: ");
        dfa.print();
    }

}
