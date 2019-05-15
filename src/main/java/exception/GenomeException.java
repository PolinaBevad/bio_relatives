package exception;

/**
 * Represents basic exception, that may occur while working with genome methods.
 *
 * @author Sergey Khvatov
 */
public class GenomeException extends Exception {

    public GenomeException(String message) {
        super(message);
    }

    public GenomeException(String cls, String method, String wrongField, String message) {
        super("Error occurred in class [" + cls + "] " + "in method [" + method + "]: field [" + wrongField + "] is [" + message + "].");
    }

    public GenomeException(String cls, String method, String message) {
        super("Error occurred in class [" + cls + "] " + "in method [" + method + "]: message is [" + message + "].");
    }
}
