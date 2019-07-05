package genome.assembly;

import bam.regular.BAMParser;
import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import htsjdk.samtools.SAMRecord;
import org.junit.Before;
import org.junit.Test;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GenomeConstructor} class.
 *
 * @author Vladislav Marchenko
 */

public class GenomeConstructorTest {
    /**
     * name of gene from bed file
     */
    private static final String geneName1 = "SCYL3";

    /**
     * name of gene from bed file
     */
    private static final String geneName2 = "C1orf112";
    /**
     * Empty ArrayList of SAMRecords
     */
    private static final ArrayList<SAMRecord> EMPTY_SAMRECORDS = new ArrayList<>();

    /**
     * Empty ArrayLIst of exons
     */
    private static final ArrayList<BEDFeature> EMPTY_EXONS = new ArrayList<>();

    /**
     * Path to correct BED file
     */
    private static final String PATH_TO_CORRECT_BED = "src/test/resources/genome/assembly/correct.bed";

    /**
     * Path to correct BAM file
     */
    private static final String PATH_TO_CORRECT_BAM = "src/test/resources/genome/assembly/correct.bam";

    /**
     * Path to incorrect BAM file
     */
    private static final String PATH_TO_INCORRECT_BAM = "src/test/resources/genome/assembly/incorrect.bam";

    /**
     * Path to incorrect BED file
     */
    private static final String PATH_TO_INCORRECT_BED = "src/test/resources/genome/assembly/incorrect1.bed";

    /**
     * Path to test BED file
     */
    private static final String PATH_TO_BED_FILE_1 = "src/test/resources/genome/assembly/file1.bed";

    /**
     * Path to test BED file
     */
    private static final String PATH_TO_BED_FILE_2 = "src/test/resources/genome/assembly/file2.bed";

    /**
     * The first checking nucleotide sequence
     */
    private static final String CHECK_SEQ_1 = "AAAAACATAAAAATAACTGGAGAGGCC";

    /**
     * The first checking array of qualities
     */
    private static final byte[] CHECK_QUALITIES_1 = {55, 59, 57, 60, 41, 44, 56, 48, 71, 62, 55, 69, 71, 44, 54, 42, 69, 47, 52, 46, 57, 53, 53, 41, 45, 71, 71};

    /**
     * The second checking nucleotide sequence
     */
    private static final String CHECK_SEQ_2 = "CACACAGCACACACACACACAACACACATGCACAC";

    /**
     * The second checking array of qualities
     */
    private static final byte[] CHECK_QUALITIES_2 = {46, 45, 46, 46, 71, 57, 52, 53, 41, 38, 40, 34, 33, 37, 37, 41, 37, 38, 50, 46, 33, 35, 41, 38, 43, 38, 44, 50, 38, 40, 53, 43, 47, 50, 61};

    /**
     * exons from bed file
     */
    private static Map<String, List<BEDFeature>> exons1;

    /**
     * exons from bed file
     */
    private static Map<String, List<BEDFeature>> exons2;

    /**
     * exons from bed file
     */
    private static Map<String, List<BEDFeature>> exons3;

    /**
     * exons from bed file
     */
    private static Map<String, List<BEDFeature>> exons4;

    @Before
    public void setUp() throws Exception {
        exons1 = new BEDParser(PATH_TO_BED_FILE_1).parse();
        exons2 = new BEDParser(PATH_TO_BED_FILE_2).parse();
        exons3 = new BEDParser(PATH_TO_CORRECT_BED).parse();
    }


    @Test
    public void AssemblyFromValidFilesWithOneSAMRecord() throws Exception {
        List<GenomeRegion> genomeRegions = GenomeConstructor.assembly(
            new BAMParser(PATH_TO_CORRECT_BAM).parse(new BEDParser(PATH_TO_BED_FILE_1).parse().get(geneName1)),
                new BEDParser(PATH_TO_BED_FILE_1).parse().get(geneName1)
        );

        for (int i = 0; i < 27; i++) {
            Pair<Character, Byte> nucleotide = genomeRegions.get(0).getNucleotide(i);
            assertEquals(nucleotide.getKey(), (Character) CHECK_SEQ_1.charAt(i));
            assertEquals(nucleotide.getValue(), (Byte) CHECK_QUALITIES_1[i]);
        }
    }

    @Test
    public void AssemblyFromValidFilesWithSomeSAMRecord() throws Exception {
        List<GenomeRegion> genomeRegions = GenomeConstructor.assembly(new BAMParser(PATH_TO_CORRECT_BAM).parse(new BEDParser(PATH_TO_BED_FILE_2).parse().get(geneName1)),
                new BEDParser(PATH_TO_BED_FILE_2).parse().get(geneName1)
        );
    }
}
