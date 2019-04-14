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

    public GenomeException(String method, String wrongField, String mes) {
        super("Error occurred in method [" + method + "]: field [" + wrongField + "] is [" + mes + "].");
    }

    public GenomeException(String cls, String method, String wrongField, String mes) {
        super("Error occurred in class [" + cls + "] " +
            "in method [" + method + "]: field [" + wrongField + "] is [" + mes + "].");
    }
}
