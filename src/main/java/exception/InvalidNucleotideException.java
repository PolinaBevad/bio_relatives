package exception;

import genome.assembly.Nucleotide;

/**
 * Defines a basic exception that may occur during the initialization
 * of the {@link Nucleotide} object.
 * @author Sergey Hvatov
 */
public class InvalidNucleotideException extends Exception
{
    public InvalidNucleotideException(String message)
    {
        super(message);
    }
}
