package model.regex;

public class OneOrMoreRepetition extends RegEx {

    public OneOrMoreRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = '+';
    }
}
