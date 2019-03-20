package exception;

/**
 * Defines an exception, that may occur during initialization of the BED feature object.
 *
 * @author Sergey Hvatov
 */
public class InvalidBEDFeatureException extends Exception
{
    public InvalidBEDFeatureException(String message)
    {
        super(message);
    }
}