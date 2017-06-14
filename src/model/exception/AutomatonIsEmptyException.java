package model.exception;

public class AutomatonIsEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public String message() {
        return "Automaton is empty.";
    }
}
