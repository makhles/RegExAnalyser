package model.regex;

public class RegExParser {

    private static RegExParser INSTANCE = new RegExParser();
    private String input;

    private RegExParser() {
    }

    public static RegExParser instance() {
        return INSTANCE;
    }

    public RegExTree parse(String input) {
        this.input = input;
        return new RegExTree(regex());
    }

    /* ------ Utility methods -------- */

    private char peek() {
        return input.charAt(0);
    }

    private void eat(char c) {
        if (peek() == c) {
            this.input = this.input.substring(1);
        } else {
            throw new RuntimeException("Expected: " + c + "; got: " + peek());
        }
    }

    private char next() {
        char c = peek();
        eat(c);
        return c;
    }

    private boolean more() {
        return input.length() > 0;
    }

    /* ------ Parsing methods -------- */

    private RegEx regex() {
        RegEx term = term();

        if (more() && peek() == '|') {
            eat('|');
            RegEx regex = regex();
            return new Union(term, regex);
        } else {
            return term;
        }
    }

    private RegEx term() {
        RegEx factor = factor();

        while (more() && peek() != ')' && peek() != '|') {
            RegEx nextFactor = factor();
            factor = new Concatenation(factor, nextFactor);
        }

        return factor;
    }

    private RegEx factor() {
        RegEx base = base();

        while (more() && (peek() == '*' || peek() == '+' || peek() == '?')) {
            switch (peek()) {
            case '*':
                eat('*');
                base = new ZeroOrMoreRepetition(base);
                break;

            case '+':
                eat('+');
                base = new OneOrMoreRepetition(base);
                break;

            case '?':
                eat('?');
                base = new OneOrNoneRepetition(base);
                break;
            }
        }

        return base;
    }

    private RegEx base() {
        RegEx base;

        switch (peek()) {
        case '(':
            eat('(');
            RegEx r = regex();
            eat(')');
            base = r;
            break;

        default:
            base = new Primitive(next());
            break;
        }

        return base;
    }
}
