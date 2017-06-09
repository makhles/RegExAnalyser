package test;

import model.automaton.Automaton;
import model.regex.RegExParser;
import model.regex.RegExTree;

public class TestAutomaton {

    public static void main(String[] args) {
        printAutomaton();
    }

    private static void printAutomaton() {
//        RegExTree tree = RegExParser.instance().parse("(a|b)*");  //OK
//        RegExTree tree = RegExParser.instance().parse("a*(b?c|d)*");  //OK
//        RegExTree tree = RegExParser.instance().parse("(b*ab*ab*ab*)*ab*a?b*");  //OK
//        RegExTree tree = RegExParser.instance().parse("(a|b)*c");  //
//        RegExTree tree = RegExParser.instance().parse("a?(ba)*b?");  // OK
//        RegExTree tree = RegExParser.instance().parse("(ab|b(ab)*b)*(ba)*");  //OK
//        RegExTree tree = RegExParser.instance().parse("b?(ab?ab?)*ab?");  //OK
//        RegExTree tree = RegExParser.instance().parse("l(u?d|u?l)*");  //OK
        RegExTree tree = RegExParser.instance().parse("(ab|ac)*a?|(ba?c)*");  //OK
//        RegExTree tree = RegExParser.instance().parse("(0|1(01*0)*1)+");  //OK
//        RegExTree tree = RegExParser.instance().parse("1?1?(0??011?)*0?0?");  //OK
        System.out.println("Generated tree: " + tree);
        System.out.println();

        Automaton dfa = tree.convertToDFA();
        System.out.println("DFA: ");
        dfa.print();
    }

}
