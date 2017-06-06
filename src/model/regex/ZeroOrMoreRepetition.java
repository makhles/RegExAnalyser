package model.regex;

import java.util.Set;

public class ZeroOrMoreRepetition extends RegEx {

    public ZeroOrMoreRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = '*';
    }

    @Override
    protected Set<RegEx> moveDown() {
        Set<RegEx> leftNodes = left.moveDown();
        Set<RegEx> threadNodes = thread.moveUp();
        leftNodes.addAll(threadNodes);
        return leftNodes;
    }

    @Override
    protected Set<RegEx> moveUp() {
        return moveDown();
    }

}
