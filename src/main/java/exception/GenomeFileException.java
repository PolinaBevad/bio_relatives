package exception;

import java.io.IOException;

/**
 * Represents basic exception, that may occur while working with files methods.
 *
 * @author Sergey Khvatov
 */
public class GenomeFileException extends IOException {

    public GenomeFileException(String message) {
        super(message);
    }

    public GenomeFileException(String cls, String method, String filename, String message) {
        super("Error occurred in class [" + cls + "] " +
            "in method [" + method + "]: file [" + filename + "] is [" + message + "].");
    }
}
