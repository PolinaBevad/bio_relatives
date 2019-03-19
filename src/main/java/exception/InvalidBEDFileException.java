package exception;

/**
 * Defines a basic exception that may occur during the reading from BED file.
 * @author Sergey Hvatov
 */
public class InvalidBEDFileException extends Exception
{
    public InvalidBEDFileException(String message)
    {
        super(message);
    }
}
