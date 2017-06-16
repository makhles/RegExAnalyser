package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.Controller;
import model.automaton.Automaton;
import model.exception.AutomatonAlreadyMinimumException;
import model.regex.RegExParser;
import model.regex.RegExTree;

public class TestAutomaton {

    public static void main(String[] args) {
        // printAutomaton();
        // createAutomaton();
        // testClosure();
//        testMinimization();
        testUnion();
    }

    private static void testUnion() {
        int index, indexA, indexB;
        indexA = createAutomaton(0);
        indexB = createAutomaton(1);
        index = Controller.instance().union(indexA, indexB);
        System.out.println("Resulting automaton:");
        printAutomaton(index);
    }

    private static void testMinimization() {
        int index = createAutomaton(8);
        try {
            index = Controller.instance().minimize(index);
        } catch (AutomatonAlreadyMinimumException e) {
            System.out.println(e.message());
        }
    }

    private static void testClosure() {
        int index = createAutomaton(3);
        index = Controller.instance().convertNFAtoDFA(index);
        System.out.println("Resulting automaton:");
        printAutomaton(index);
    }

    private static int createAutomaton(int model) {
        List<List<String>> table = table(model);
        int index = Controller.instance().createAutomaton(table, table.get(1).get(1));
        System.out.println("Automaton created:");
        printAutomaton(index);
        return index;
    }

    private static List<List<String>> table(int model) {
        List<List<String>> table = new ArrayList<>();

        switch (model) {
        // NFA without epsilon transitions

        case 0:
            table.add(Arrays.asList("a", "b", "c"));
            table.add(Arrays.asList("*", "S", "A,C", "A,D", "B,C"));
            table.add(Arrays.asList("*", "A", "A", "A", "B"));
            table.add(Arrays.asList("*", "B", "A", "A", "-"));
            table.add(Arrays.asList("*", "C", "C", "D", "C"));
            table.add(Arrays.asList("*", "D", "C", "-", "C"));
        break;
        case 1:
             table.add(Arrays.asList("a", "b", "c"));
             table.add(Arrays.asList(" ", "A", "B", "C", "D"));
             table.add(Arrays.asList("*", "B", " C,D", "-", "D"));
             table.add(Arrays.asList("*", "C", "A", "A", "-"));
             table.add(Arrays.asList(" ", "D", "-", "A, B, C", "C"));
         break;
        case 2:
             table.add(Arrays.asList("a", "b"));
             table.add(Arrays.asList("*", "0", "1,2", "-"));
             table.add(Arrays.asList("*", "1", "1,2", "-"));
             table.add(Arrays.asList(" ", "2", "-", "1,3"));
             table.add(Arrays.asList(" ", "3", "1,2", "-"));
         break;

        // NFA with epsilon transitions
        case 3:
             table.add(Arrays.asList("a", "b", "c", "&"));
             table.add(Arrays.asList(" ", "A", "A", "B", "-", "B,C"));
             table.add(Arrays.asList(" ", "B", "C", "-", "A,B", "-"));
             table.add(Arrays.asList("*", "C", "A", "C", "C", "B"));
         break;

        case 4:
             table.add(Arrays.asList("a", "b", "c", "&"));
             table.add(Arrays.asList(" ", "1", "2", "-", "4", "-"));
             table.add(Arrays.asList(" ", "2", "-", "3", "-", "1"));
             table.add(Arrays.asList("*", "3", "2", "-", "-", "-"));
             table.add(Arrays.asList("*", "4", "-", "-", "3", "3"));
         break;

        // DFAs
        case 5:
            table.add(Arrays.asList("a", "b"));
            table.add(Arrays.asList(" ", "Q0", "Q1", "Q2"));
            table.add(Arrays.asList(" ", "Q1", "Q3", "-"));
            table.add(Arrays.asList(" ", "Q2", "-", "Q4"));
            table.add(Arrays.asList("*", "Q3", "Q3", "Q3"));
            table.add(Arrays.asList("*", "Q4", "Q4", "Q4"));
        break;

         // DFA with unreachable states {C, D} and dead states {C, F}
        case 6:
             table.add(Arrays.asList("a", "b"));
             table.add(Arrays.asList(" ", "A", "E", "B"));
             table.add(Arrays.asList(" ", "B", "E", "F"));
             table.add(Arrays.asList(" ", "C", "-", "F"));
             table.add(Arrays.asList(" ", "D", "D", "A"));
             table.add(Arrays.asList("*", "E", "E", "E"));
             table.add(Arrays.asList(" ", "F", "-", "-"));
         break;

        // DFA with unreachable states {B, C, E, F} and dead states {A, C, D, F}
        case 7:
             table.add(Arrays.asList("a", "b"));
             table.add(Arrays.asList(" ", "A", "D", "D"));
             table.add(Arrays.asList(" ", "B", "E", "F"));
             table.add(Arrays.asList(" ", "C", "-", "F"));
             table.add(Arrays.asList(" ", "D", "D", "A"));
             table.add(Arrays.asList("*", "E", "E", "E"));
             table.add(Arrays.asList(" ", "F", "-", "-"));
         break;

        // DFA with unreachable states {D, H} and 3 equivalent classes
        case 8:
             table.add(Arrays.asList("a", "b"));
             table.add(Arrays.asList("*", "A", "G", "B"));
             table.add(Arrays.asList(" ", "B", "F", "E"));
             table.add(Arrays.asList(" ", "C", "C", "G"));
             table.add(Arrays.asList("*", "D", "A", "H"));
             table.add(Arrays.asList(" ", "E", "E", "A"));
             table.add(Arrays.asList(" ", "F", "B", "C"));
             table.add(Arrays.asList("*", "G", "G", "F"));
             table.add(Arrays.asList(" ", "H", "H", "D"));
         break;
         
        default:
            break;
        }
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

    private static void printAutomaton(int index) {
        Controller.instance().printAutomaton(index);
    }
}
