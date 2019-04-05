package exception;

/**
 * Defines a basic exception that may occur during the assembly of genome.
 * @author Vladislav Marchenko
 */
public class InvalidGenomeAssemblyException extends  Exception{
    public InvalidGenomeAssemblyException(String message) {
        super(message);
    }
}
