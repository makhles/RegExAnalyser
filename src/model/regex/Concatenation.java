package model.regex;

public class Concatenation extends RegEx {

    public Concatenation(RegEx left, RegEx right) {
        this.left = left;
        this.right = right;
        this.data = '.';
    }
}
