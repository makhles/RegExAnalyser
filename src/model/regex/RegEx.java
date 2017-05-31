package model.regex;

public abstract class RegEx {

    protected static RegEx blank = null;
    protected Character data;
    protected RegEx left;
    protected RegEx right;

    @Override
    public String toString() {
        String print = "";
        if (left != null) {
            print = left.toString();
            print += "(" + data + ") ";
//            System.out.print("(" + data + ")");
        }
        if (right != null) {
            print += right.toString();
            print += "(" + data + ") ";
//            System.out.print(" (" + data + ")");
        }
        print += data.toString();
        return print;
    }

    public Character getData() {
        return data;
    }

    public RegEx getLeft() {
        return left;
    }

    public RegEx getRight() {
        return right;
    }
}
