package model.regex;

import java.util.Set;

public class Union extends RegEx {

    public Union(RegEx left, RegEx right) {
        this.left = left;
        this.right = right;
        this.data = '|';
    }

    @Override
    protected Set<RegEx> moveDown() {
        Set<RegEx> leftNodes = left.moveDown();
        Set<RegEx> rightNodes = right.moveDown();
        leftNodes.addAll(rightNodes);
        return leftNodes;
    }

    @Override
    protected Set<RegEx> moveUp() {
        RegEx tempRight = right;
        while (tempRight.getRight() != null) {
            tempRight = tempRight.getRight();
        }
        return tempRight.getThread().moveUp();
    }
}
