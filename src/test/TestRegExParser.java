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
    }
}
