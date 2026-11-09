package exception;

public class SecondeInvalideException extends MatchException {

    public SecondeInvalideException(String message) {
        super(message);
    }

    public SecondeInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}