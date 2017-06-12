package model.exception;

public class AutomatonAlreadyMinimumException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public String message() {
        return "This automaton is already minimum.";
    }

}
