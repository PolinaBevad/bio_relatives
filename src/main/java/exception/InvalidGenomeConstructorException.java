package exception;

/**
 * Defines a basic exception that may occur during the initialization
 * of the {@link genome.assembly.GenomeConstructor} object.
 *
 * @author Vladislav Marchenko
 */
public class InvalidGenomeConstructorException extends Exception {
    public InvalidGenomeConstructorException(String method, String wrongField, String mes) {
        super("Error occurred in method [" + method + "]: field [" + wrongField + "] is [" + mes + "].");
    }

    public InvalidGenomeConstructorException(String message) {
        super(message);
    }
}
