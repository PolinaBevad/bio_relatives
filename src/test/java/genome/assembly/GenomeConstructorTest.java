package genome.assembly;

import bam.BAMParser;
import bam.BEDParser;
import exception.GenomeException;
import htsjdk.samtools.SAMRecord;
import org.junit.Test;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GenomeConstructor} class.
 *
 * @author Vladislav Marchenko
 */

public class GenomeConstructorTest {
    /**
     * Empty ArrayList of SAMRecords
     */
    public static final ArrayList<SAMRecord> EMPTY_SAMRECORDS = new ArrayList<>();

    /**
     * Empty ArrayLIst of exons
     */
    public static final ArrayList<BEDParser.BEDFeature> EMPTY_EXONS = new ArrayList<>();

    /**
     * Path to correct BED file
     */
    public static final String PATH_TO_CORRECT_BED = "src/test/resources/genome/assembly/correct.bed";

    /**
     * Path to correct BAM file
     */
    public static final String PATH_TO_CORRECT_BAM = "src/test/resources/genome/assembly/correct.bam";

    /**
     * Path to incorrect BAM file
     */
    public static final String PATH_TO_INCORRECT_BAM = "src/test/resources/genome/assembly/incorrect.bam";

    /**
     * Path to incorrect BED file
     */
    public static final String PATH_TO_INCORRECT_BED = "src/test/resources/genome/assembly/incorrect1.bed";

    /**
     * Path to test BED file
     */
    public static final String PATH_TO_BED_FILE_1 = "src/test/resources/genome/assembly/file1.bed";

    /**
     * Path to test BED file
     */
    public static final String PATH_TO_BED_FILE_2 = "src/test/resources/genome/assembly/file2.bed";

    /**
     * The first checking nucleotide sequence
     */
    public static final String CHECK_SEQ_1 = "AAAAACATAAAAATAACTGGAGAGGCC";

    /**
     * The first checking array of qualities
     */
    public static final byte[] CHECK_QUALITIES_1 = {55, 59, 57, 60, 41, 44, 56, 48, 71, 62, 55, 69, 71, 44, 54, 42, 69, 47, 52, 46, 57, 53, 53, 41, 45, 71, 71};

    /**
     * The second checking nucleotide sequence
     */
    public static final String CHECK_SEQ_2 = "CACACAGCACACACACACACAACACACATGCACAC";

    /**
     * The second checking array of qualities
     */
    public static final byte[] CHECK_QUALITIES_2 = {56, 45, 46, 46, 71, 57, 52, 53, 41, 38, 40, 34, 33, 37, 37, 41, 37, 38, 50, 46, 33, 35, 41, 38, 43, 38, 44, 50, 38, 40, 53, 43, 47, 50, 61};


    @Test(expected = GenomeException.class)
    public void CreationFromEmptySAMRecords() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(EMPTY_SAMRECORDS, new BEDParser(PATH_TO_CORRECT_BED).parse());
    }

    @Test(expected = GenomeException.class)
    public void CreationFromEmptyExons() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(new BAMParser(PATH_TO_CORRECT_BAM, new BEDParser(PATH_TO_CORRECT_BED).parse()).parse(), EMPTY_EXONS);
    }

    @Test(expected = GenomeException.class)
    public void CreationFromInvalidBAMFile() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(PATH_TO_INCORRECT_BAM, PATH_TO_CORRECT_BED);
    }

    @Test(expected = GenomeException.class)
    public void CreationFromInvalidBEDFile() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(PATH_TO_CORRECT_BAM, PATH_TO_INCORRECT_BED);
    }

    @Test
    public void CreationFromValidData() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(new BAMParser(PATH_TO_CORRECT_BAM, new BEDParser(PATH_TO_CORRECT_BED).parse()).parse(), new BEDParser(PATH_TO_CORRECT_BED).parse());
    }

    @Test
    public void AssemblyFromValidFilesWithOneSAMRecord() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(PATH_TO_CORRECT_BAM, PATH_TO_BED_FILE_1);
        List<GenomeRegion> genomeRegions = genomeConstructor.assembly();
        for (int i = 0; i < 27; i++) {
            Pair<Character, Byte> nucleotide = genomeRegions.get(0).getNucleotide(i);
            assertEquals(nucleotide.getKey(), (Character) CHECK_SEQ_1.charAt(i));
            assertEquals(nucleotide.getValue(), (Byte) CHECK_QUALITIES_1[i]);
        }
    }

    @Test
    public void AssemblyFromValidFilesWithSomeSAMRecord() throws Exception {
        GenomeConstructor genomeConstructor = new GenomeConstructor(PATH_TO_CORRECT_BAM, PATH_TO_BED_FILE_2);
        List<GenomeRegion> regions = genomeConstructor.assembly();
        for (int i = 0; i < 35; i++) {
            Pair<Character, Byte> nucleotide = regions.get(0).getNucleotide(i);
            assertEquals(nucleotide.getKey(), (Character) CHECK_SEQ_2.charAt(i));
            assertEquals(nucleotide.getValue(), (Byte) CHECK_QUALITIES_2[i]);
        }

    }

}
