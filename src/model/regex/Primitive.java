package model.regex;

import java.util.HashSet;
import java.util.Set;

public class Primitive extends RegEx {

    public Primitive(String data) {
        left = null;
        right = null;
        this.data = data;
    }

    @Override
    protected Set<RegEx> moveDown() {
        Set<RegEx> set = new HashSet<>();
        set.add(this);
        return set;
    }

    @Override
    protected Set<RegEx> moveUp() {
        return thread.moveUp();
    }

    @Override
    public void fillVocabulary(Set<String> vocabulary) {
        vocabulary.add(data);
    }
}
