package model.regex;

public class ZeroOrMoreRepetition extends RegEx {

    public ZeroOrMoreRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = '*';
    }

}
