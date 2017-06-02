package model.regex;

public class RegExTree {

    private RegEx root;

    public RegExTree(RegEx root) {
        this.root = root;
        linkNodes();
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private void linkNodes() {
    	root.knit();
    }
}
