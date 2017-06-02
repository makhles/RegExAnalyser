package model.regex;

public abstract class RegEx {

    protected static RegEx blank = null;
    protected Character data;
    protected RegEx left;
    protected RegEx right;
    protected RegEx thread;

    /**
     * Knits the binary tree, turning it into a threaded binary tree.
     * @return The node which will hold the thread.
     */
    public RegEx knit() {
    	if (left != null) {
    		RegEx predecessor = left.knit();
    		predecessor.setThread(this);
    	}
    	if (right != null) {
    		return right.knit();
    	}
    	return this;
    }

    @Override
    public String toString() {
        String print = "";
        if (left != null) {
            print = left.toString();
            print += "(" + data + ") ";
        }
        if (right != null) {
            print += right.toString();
            print += "(" + data + ") ";
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

	public RegEx getThread() {
		return thread;
	}

	public void setThread(RegEx thread) {
		this.thread = thread;
	}
}
