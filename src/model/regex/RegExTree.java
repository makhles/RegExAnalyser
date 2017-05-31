package model.regex;

public class RegExTree {

    private RegEx root;

    public RegExTree(RegEx root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return root.toString();
    }
//    @Override
//    public String toString() {
//        visit(root);
//        return "";
//    }

//    private void visit(RegEx node) {
//        if (node.getLeft() != null) {
//            visit(node.getLeft());
//            System.out.print("(" + node.getData() + ")");
//        }
//        System.out.print(" ");
//        if (node.getRight() != null) {
//            visit(node.getRight());
//            System.out.print("(" + node.getData() + ")");
//        }
//        System.out.print(" " + node.getData());
//    }
}
