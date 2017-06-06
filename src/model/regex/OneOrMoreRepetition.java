package model.regex;

import java.util.Set;

public class OneOrMoreRepetition extends RegEx {

    public OneOrMoreRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = '+';
    }

    @Override
    protected Set<RegEx> moveDown() {
        return left.moveDown();
    }

    @Override
    protected Set<RegEx> moveUp() {
        Set<RegEx> leftNodes = left.moveDown();
        Set<RegEx> threadNodes = thread.moveUp();
        leftNodes.addAll(threadNodes);
        return leftNodes;
    }

}
