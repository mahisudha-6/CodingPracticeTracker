package exception;

/**
 * Exception thrown for validation failures (e.g. empty fields, incorrect formats).
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
}
