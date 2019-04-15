package exception;

/**
 * Defines a basic exception that may occur during the initialization
 * of the {@link genome.assembly.GenomeRegion} object.
 * @author Sergey Hvatov
 */
public class InvalidRegionException extends Exception
{
    public InvalidRegionException(String method, String wrongField, String mes) {
        super("Error occurred in method [" + method + "]: field [" + wrongField + "] is [" + mes + "].");
    }
}
