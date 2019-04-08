package exception;

/**
 * Defines a basic exception that may occur during the reading from BAM file.
 *
 * @author Vladislav Marchenko
 */
public class InvalidBAMFileException extends Exception {
    public InvalidBAMFileException(String message) {
        super(message);
    }
}
