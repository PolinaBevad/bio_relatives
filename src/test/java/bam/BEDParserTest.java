package bam;

import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import exception.GenomeFileException;
import org.junit.Test;

import java.util.*;

/**
 * Tests the {@link BEDParser} class methods.
 *
 * @author Sergey Hvatov
 */
public class BEDParserTest {
    /**
     * Path to non existent file.
     */
    private static final String pathToNonExistentFile = "/wrong/path/file.bed";

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
     * Path to incorrect bed file with gene which contains in two chromosomes
     */
    private static final String pathToIncorrectFile3 ="src/test/resources/bam/BEDParser/incorrect3.bed";

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
    private static final int[] correctStart = {27612063, 24357004, 169853073, 169795048, 196651877, 23019447, 54784550, 24415793, 33013043};

    /**
     * Correct en position values from the file.
     */
    private static final int[] correctEnd = {27635277, 24413725, 169893959, 169854080,  196747504, 23083689,  54801198,  24469307, 33036992};

    /**
     * Correct name of the chromosome from the file.
     */
    private static final String correctName = "chr1";

    /**
     * Correct names of the genomes from the file.
     */
    private static final String[] correctGenome = {"FGR", "STPG1", "SCYL3", "C1orf112",  "CFH",  "KDM1A",  "TTC22", "NIPAL3", "AK2"};


    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNonExistentFile() throws Exception {
        BEDParser parser = new BEDParser(pathToNonExistentFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToFileWithWrongExtension() throws Exception {
        BEDParser parser = new BEDParser(pathToFileWithWrongExt);
    }

    @Test
    public void CreationFromPathToCorrectFile() throws Exception {
        BEDParser parser = new BEDParser(pathToCorrectFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNotAFile() throws Exception {
        BEDParser parser = new BEDParser(pathToNotAFile);
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithWrongStartEnd() throws Exception {
        BEDParser parser = new BEDParser(pathToIncorrectFile1);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithWrongGenome() throws Exception {
        BEDParser parser = new BEDParser(pathToIncorrectFile2);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithGeneContainsInTwoChromosomes() throws Exception {
        BEDParser parser = new BEDParser(pathToIncorrectFile3);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test
    public void ParsingCorrectFile() throws Exception {
        BEDParser parser = new BEDParser(pathToCorrectFile);
        Map<String, List<BEDFeature>> result = parser.parse();
    }
}
