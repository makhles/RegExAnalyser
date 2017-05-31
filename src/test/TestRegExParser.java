package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
//import static org.junit.Assert.*;

import model.regex.RegExParser;
import model.regex.RegExTree;

public class TestRegExParser {

    private RegExParser parser;

    @Before
    public void init() {
        parser = new RegExParser("a*(b?c|d)*");
    }

    @Test
    public void testSequences() throws Exception {
        assertEquals("a(*) *(.) b(?) ?(.) c(.) .(|) d(|) |(*) *(.) .", parser.parse().toString());
        parser = new RegExParser("(a|b)*ab?a+");
        assertEquals("a(|) b(|) |(*) *(.) a(.) b(?) ?(.) a(+) +(.) .(.) .(.) .", parser.parse().toString());
    }
    
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("(a|b)*ab?a+");
        RegExTree tree = parser.parse();
        System.out.println(tree);
//        printTree();
    }

//    private static void printTree() {
//        final int NUM_NODES = 15;
//        final int LINE_WIDTH = 70;
//        int[] tree = {0,1,2,3,4,5,6,7,8,9,1,2,3,4,5};
//        int[] print_pos = new int[NUM_NODES];
//        int i, j, k, pos, x = 1, level = 0;
//
//        print_pos[0] = 0;
//        for(i = 0, j = 1; i < NUM_NODES; i++, j++) {
//            int temp;
//            if (i % 2 == 0) {
//                temp = 1;
//            } else {
//                temp = -1;
//            }
//            pos = (print_pos[parent(i)] + (int) (temp * (LINE_WIDTH / (Math.pow(2, level+1)) + 1)));
//
//            for (k = 0; k < pos - x; k++) {
//                if (i == 0 || i % 2 == 1) {
//                    System.out.print(" ");
//                } else {
//                    System.out.print("-");
//                }
//            }
//            System.out.print(tree[i]);
//
//            print_pos[i] = x = pos + 1;
//            if (j == (int) Math.pow(2, level)) {
//                System.out.println();
//                level++;
//                x = 1;
//                j = 0;
//            }
//        }
//    }
//
//    private static int parent(int i) {
//        return ((i-1)/2);
//    }
}
