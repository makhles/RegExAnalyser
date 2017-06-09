package model.regex;

import java.util.HashSet;
import java.util.Set;

public class Lambda extends RegEx {

    public Lambda() {
        left = null;
        right = null;
        this.data = "lambda";
    }

    @Override
    protected Set<RegEx> moveDown() {
        System.out.println("Lambda node should not be invoked with moveDown()");
        return null;
    }

    @Override
    protected Set<RegEx> moveUp() {
        Set<RegEx> set = new HashSet<>();
        set.add(this);
        return set;
    }

}
