package model.regex;

public class OneOrNoneRepetition extends RegEx {

    public OneOrNoneRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = '?';
    }
}
