package model.regex;

import java.util.Set;

public class Concatenation extends RegEx {

    public Concatenation(RegEx left, RegEx right) {
        this.left = left;
        this.right = right;
        this.data = ".";
    }

    @Override
    protected Set<RegEx> moveDown() {
        return left.moveDown();
    }

    @Override
    protected Set<RegEx> moveUp() {
        return right.moveDown();
    }
}
