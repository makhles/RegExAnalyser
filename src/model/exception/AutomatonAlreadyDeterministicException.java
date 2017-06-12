package model.exception;

public class AutomatonAlreadyDeterministicException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public String message() {
        return "Invalid operation: the automaton is already deterministic.";
    }
}
