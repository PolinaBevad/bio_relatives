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
public class BEDParserTest {
    /**
     * Path to non existent file.
     */
    private final String pathToNonExistentFile = "/wrong/path/file.bed";

    /**
     * Path to file with wrong extension.
     */
    private static final String pathToFileWithWrongExt = "src/test/resources/bam/BEDParser/file.txt";

    /**
     * Path to incorrect bed file with wrong start and end position.
     */
    private static final String pathToIncorrectFile1 = "src/test/resources/bam/BEDParser/incorrect1.bed";

    /**
     * Path to incorrect bed file with wrong genome name.
     */
    private static final String pathToIncorrectFile2 = "src/test/resources/bam/BEDParser/incorrect2.bed";

    /**
     * Path to correct bed file.
     */
    private final String pathToCorrectFile = "src/test/resources/bam/BEDParser/correct.bed";

    /**
     * Path to directory.
     */
    private static final String pathToNotAFile = "src/test/resources/bam/BEDParser/";

    /**
     * Correct start position values from the file.
     */
    private static final int[] correctStart = {169853073, 169795048, 27612063, 196651877, 24357004, 24415793, 33013043, 23019447, 54784550};

    /**
     * Correct en position values from the file.
     */
    private static final int[] correctEnd = {169893959, 169854080, 27635277, 196747504, 24413725, 24469307, 33036992, 23083689, 54801198};

    /**
     * Correct name of the chromosome from the file.
     */
    private static final String correctName = "chr1";

    /**
     * Correct names of the genomes from the file.
     */
    private static final String[] correctGenome = {"SCYL3", "C1orf112", "FGR", "CFH", "STPG1", "NIPAL3", "AK2", "KDM1A", "TTC22"};


    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToNonExistentFile() throws Exception {
        BEDParser parser = new BEDParser(pathToNonExistentFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToFileWithWrongExtension() throws Exception {
        BEDParser parser = new BEDParser(pathToFileWithWrongExt);
    }

    @Test
    public void CreationFromPathToCorrectFile() throws Exception {
        BEDParser parser = new BEDParser(pathToCorrectFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void CreationFromPathToNotAFile() throws Exception {
        BEDParser parser = new BEDParser(pathToNotAFile);
    }

    @Test(expected = InvalidBEDFileException.class)
    public void ParsingIncorrectFileWithWrongStartEnd() throws Exception {
        BEDParser parser = new BEDParser(pathToIncorrectFile1);
        ArrayList<BEDParser.BEDFeature> result = parser.parse();
    }

    @Test(expected = InvalidBEDFileException.class)
    public void ParsingIncorrectFileWithWrongGenome() throws Exception {
        BEDParser parser = new BEDParser(pathToIncorrectFile2);
        ArrayList<BEDParser.BEDFeature> result = parser.parse();
    }

    @Test
    public void ParsingCorrectFile() throws Exception {
        BEDParser parser = new BEDParser(pathToCorrectFile);
        ArrayList<BEDParser.BEDFeature> result = parser.parse();
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getStartPos(), correctStart[i]);
            assertEquals(result.get(i).getEndPos(), correctEnd[i]);
            assertEquals(result.get(i).getChromosomeName(), correctName);
            assertEquals(result.get(i).getGenomeSymbol(), correctGenome[i]);
        }
    }
}
