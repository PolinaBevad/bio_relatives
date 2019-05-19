package exception;

/**
 * Represents a basic runtime exception that may occur during the work
 * of one the threads.
 *
 * @author Sergey Khvatov
 */
public class GenomeThreadException extends RuntimeException {
    public GenomeThreadException(String message) {
        super(message);
    }

    public GenomeThreadException(String cls, String method, String wrongField, String message) {
        super("Error occurred in class [" + cls + "] " + "in method [" + method + "]: field [" + wrongField + "] is [" + message + "].");
    }

    public GenomeThreadException(String cls, String method, String message) {
        super("Error occurred in class [" + cls + "] " + "in method [" + method + "]: message is [" + message + "].");
    }
}
