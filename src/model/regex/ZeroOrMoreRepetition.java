package model.regex;

import java.util.HashSet;
import java.util.Set;

public class ZeroOrMoreRepetition extends RegEx {

    private boolean locked;

    public ZeroOrMoreRepetition(RegEx left) {
        this.left = left;
        this.right = null;
        this.data = "*";
        this.locked = false;
    }

    @Override
    protected Set<RegEx> moveDown() {
        Set<RegEx> leftNodes = new HashSet<>();
        if (!locked) {
            locked = true;
            leftNodes = left.moveDown();
            Set<RegEx> threadNodes = thread.moveUp();
            leftNodes.addAll(threadNodes);
            locked = false;
        }
        return leftNodes;
    }

    @Override
    protected Set<RegEx> moveUp() {
        return moveDown();
    }

}
