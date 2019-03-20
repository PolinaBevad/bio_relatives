package bam;

import exception.InvalidBEDFileException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Tests the {@link BEDParser} class methods.
 *
 * @author Sergey Hvatov
 */
public class BEDParserTest
{
    /**
     * Path to non existent file.
     */
    private static final String pathToNonExistentFile = "/wrong/path/file.bed";

    /**
     * Path to file with wrong extension.
     */
    private static final String pathToFileWithWrongExt = "/home/ishvatov/Desktop/bio_relatives/src/test/resources/bam/BEDParser/file.txt";

    /**
     * Path to incorrect bed file .
     */
    private static final String pathToIncorrectFile = "/home/ishvatov/Desktop/bio_relatives/src/test/resources/bam/BEDParser/incorrect.bed";

    /**
     * Path to correct bed file.
     */
    private static final String pathToCorrectFile = "/home/ishvatov/Desktop/bio_relatives/src/test/resources/bam/BEDParser/correct.bed";

    /**
     * Path to directory.
     */
    private static final String pathToNotAFile = "/home/ishvatov/Desktop/bio_relatives/src/test/resources/bam/BEDParser/";

    /**
     * Correct start position values from the file.
     */
    private static final int[] correctStart = {127471196, 127472363, 127473530, 127474697, 127475864, 127477031};

    /**
     * Correct en position values from the file.
     */
    private static final int[] correctEnd = {127472363, 127473530, 127474697, 127475864, 127477031, 127478198};

    /**
     * Correct name of the chromosome from the file.
     */
    private static final String correctName = "chr7";

    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToNonExistentFile() throws Exception
    {
        BEDParser parser = new BEDParser(pathToNonExistentFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToFileWithWrongExtension() throws Exception
    {
        BEDParser parser = new BEDParser(pathToFileWithWrongExt);
    }

    @Test
    public void CreationFromPathToCorrectFile() throws Exception
    {
        BEDParser parser = new BEDParser(pathToCorrectFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToNotAFile() throws Exception
    {
        BEDParser parser = new BEDParser(pathToNotAFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void ParsingIncorrectFile() throws Exception
    {
        BEDParser parser = new BEDParser(pathToIncorrectFile);
        ArrayList<BEDParser.BEDFeature> result = parser.parse();
    }

    @Test
    public void ParsingCorrectFile() throws Exception
    {
        BEDParser parser = new BEDParser(pathToCorrectFile);
        ArrayList<BEDParser.BEDFeature> result = parser.parse();
        for (int i = 0; i < result.size(); i++)
        {
            assertEquals(result.get(i).getStartPos(), correctStart[i]);
            assertEquals(result.get(i).getEndPos(), correctEnd[i]);
            assertEquals(result.get(i).getChromosomeName(), correctName);
        }
    }
}
