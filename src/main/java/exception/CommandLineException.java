package exception;

import java.io.IOException;

/**
 * Represents basic exception, that may occur while working with the user's input.
 *
 * @author Sergey Khvatov
 */

public class CommandLineException extends IOException {

    public CommandLineException(String message) {
        super("Error occurred in class [CommandLineParser] in method [parse]:" + message);
    }
}
