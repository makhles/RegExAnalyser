package model.regex;

public class Union extends RegEx {

    public Union(RegEx left, RegEx right) {
        this.left = left;
        this.right = right;
        this.data = '|';
    }
}
