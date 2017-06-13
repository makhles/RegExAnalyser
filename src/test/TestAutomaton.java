package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.Controller;
import model.automaton.Automaton;
import model.regex.RegExParser;
import model.regex.RegExTree;

public class TestAutomaton {

    public static void main(String[] args) {
        // printAutomaton();
        // createAutomaton();
        // testClosure();
        testMinimization();
    }

    private static void testMinimization() {
        Controller control = Controller.instance();
        List<List<String>> table = table();
        int index = control.createAutomaton(table, table.get(1).get(1));
        control.printAutomaton(index);
        index = control.minimize(index);
        control.printAutomaton(index);
    }

    private static void testClosure() {
        Controller control = Controller.instance();
        List<List<String>> table = table();
        int index = control.createAutomaton(table, table.get(1).get(1));
        control.printAutomaton(index);
        index = control.convertNFAtoDFA(index);
        control.printAutomaton(index);
    }

    private static void createAutomaton() {
        Controller control = Controller.instance();
        List<List<String>> table = table();
        int index = control.createAutomaton(table, table.get(1).get(1));
        control.printAutomaton(index);
    }

    private static List<List<String>> table() {
        List<List<String>> table = new ArrayList<>();

        // table.add(Arrays.asList("a", "b", "c"));
        // table.add(Arrays.asList(" ", "A", "B", "C", "D"));
        // table.add(Arrays.asList("*", "B", " C,D", "-", "D"));
        // table.add(Arrays.asList("*", "C", "A", "A", "-"));
        // table.add(Arrays.asList(" ", "D", "-", "A, B, C", "C"));

        // NFA With epsilon transitions
        // table.add(Arrays.asList("a", "b", "c", "&"));
        // table.add(Arrays.asList(" ", "A", "A", "B", "-", "B,C"));
        // table.add(Arrays.asList(" ", "B", "C", "-", "A,B", "-"));
        // table.add(Arrays.asList("*", "C", "A", "C", "C", "B"));

        // table.add(Arrays.asList("a", "b", "c", "&"));
        // table.add(Arrays.asList(" ", "1", "2", "-", "4", "-"));
        // table.add(Arrays.asList(" ", "2", "-", "3", "-", "1"));
        // table.add(Arrays.asList("*", "3", "2", "-", "-", "-"));
        // table.add(Arrays.asList("*", "4", "-", "-", "3", "3"));

        // NFA without epsilon transitions
        // table.add(Arrays.asList("a", "b"));
        // table.add(Arrays.asList("*", "0", "1,2", "-"));
        // table.add(Arrays.asList("*", "1", "1,2", "-"));
        // table.add(Arrays.asList(" ", "2", "-", "1,3"));
        // table.add(Arrays.asList(" ", "3", "1,2", "-"));

        // DFA with unreachable states {C, D} and dead states {C, F}
        // table.add(Arrays.asList("a", "b"));
        // table.add(Arrays.asList(" ", "A", "E", "B"));
        // table.add(Arrays.asList(" ", "B", "E", "F"));
        // table.add(Arrays.asList(" ", "C", "-", "F"));
        // table.add(Arrays.asList(" ", "D", "D", "A"));
        // table.add(Arrays.asList("*", "E", "E", "E"));
        // table.add(Arrays.asList(" ", "F", "-", "-"));

        // DFA with unreachable states {B, C, E, F} and dead states {A, C, D, F}
//        table.add(Arrays.asList("a", "b"));
//        table.add(Arrays.asList(" ", "A", "D", "D"));
//        table.add(Arrays.asList(" ", "B", "E", "F"));
//        table.add(Arrays.asList(" ", "C", "-", "F"));
//        table.add(Arrays.asList(" ", "D", "D", "A"));
//        table.add(Arrays.asList("*", "E", "E", "E"));
//        table.add(Arrays.asList(" ", "F", "-", "-"));
        
      // DFA with unreachable states {D, H} and 3 equivalent classes
      table.add(Arrays.asList("a", "b"));
      table.add(Arrays.asList("*", "A", "G", "B"));
      table.add(Arrays.asList(" ", "B", "F", "E"));
      table.add(Arrays.asList(" ", "C", "C", "G"));
      table.add(Arrays.asList("*", "D", "A", "H"));
      table.add(Arrays.asList(" ", "E", "E", "A"));
      table.add(Arrays.asList(" ", "F", "B", "C"));
      table.add(Arrays.asList("*", "G", "G", "F"));
      table.add(Arrays.asList(" ", "H", "H", "D"));
        return table;
    }

    private static void printAutomaton() {
        String input = "(ab|ac)*a?|(ba?c)*";
        // String input = "(a|b)*");
        // String input = "a*(b?c|d)*");
        // String input = "(b*ab*ab*ab*)*ab*a?b*");
        // String input = "(a|b)*c");
        // String input = "a?(ba)*b?");
        // String input = "(ab|b(ab)*b)*(ba)*");
        // String input = "b?(ab?ab?)*ab?");
        // String input = "l(u?d|u?l)*");
        // String input = "(0|1(01*0)*1)+");
        // String input = "1?1?(0??011?)*0?0?");
        RegExTree tree = new RegExParser(input).parse(); // OK
        System.out.println("Generated tree: " + tree);
        System.out.println();

        Automaton dfa = tree.convertToDFA();
        System.out.println("DFA: ");
        dfa.print();
    }
}
