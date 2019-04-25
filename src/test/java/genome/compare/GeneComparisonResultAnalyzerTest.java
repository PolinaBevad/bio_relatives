package genome.compare;

import exception.GenomeException;

import org.junit.Before;
import org.junit.Test;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link GeneComparisonResultAnalyzer} class.
 *
 * @author Vladislav Marchenko
 */
public class GeneComparisonResultAnalyzerTest {
    /**
     * Name of X chromosome
     */
    private final static String X_CHR_NAME ="chrX";

    /**
     * Name of mitochondrial chromosome
     */
    private final static String MT_CHR_NAME ="chrM";

    /**
     * Name of the first autosomal chromosome
     */
    private final static String AUTOSOMAL_CHR_NAME_1 ="chr1";

    /**
     * Name of the  second autosomal chromosome
     */
    private final static String AUTOSOMAL_CHR_NAME_2 ="chr2";

    /**
     * Name of the third autosomal chromosome
     */
    private final static String AUTOSOMAL_CHR_NAME_3 ="chr3";

    /**
     * Length of sequences
     */
    private final static int SEQUENCE_LENGTH = 100;

    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_2 = 2;

    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_10 = 10;

    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_80 = 80;
    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_55 = 55;
    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_99 = 99;

    /**
     * Position in the chromosome
     */
    private final static int POS = 138000;

    /**
     * Test List of results
     */
    private List<GeneComparisonResult> RESULTS_1 = new ArrayList<>();

    /**
     * Test List of results
     */
    private List<GeneComparisonResult> RESULTS_2 = new ArrayList<>();
    /**
     * Test empty input data
     */
    private List<GeneComparisonResult> EMPTY_DATA = new ArrayList<>();

    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_1 = "Similarity percentage for each chromosome:\n"
            +"Name of chromosome: chr3. Similarity percentage: "
            + 98d + "\n"
            +"Name of chromosome: chr1. Similarity percentage: "
            + 20d + "\n"
            +"Name of chromosome: chrM. Similarity percentage: "
            + 1d + "\n"
            +"Count of chromosomes with 98% similarity: 1\n"
            +"Count of chromosomes with 45% similarity: 0\n"
            +"Count of dissimilar chromosomes: 2\n"
            +"These persons are not child and parent.";


    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_2 = "Similarity percentage for each chromosome:\n"
            +"Name of chromosome: chrX. Similarity percentage: "
            + 98d + "\n"
            +"Name of chromosome: chr3. Similarity percentage: "
            + 90d + "\n"
            +"Name of chromosome: chr2. Similarity percentage: "
            + 44.99999999999999d + "\n"
            +"Count of chromosomes with 98% similarity: 1\n"
            +"Count of chromosomes with 45% similarity: 2\n"
            +"Count of dissimilar chromosomes: 0\n"
            +"These persons are child and parent.";

    @Before
    public void setUp() {
        try {
            GeneComparisonResult geneComparisonResult1 = new GeneComparisonResult(MT_CHR_NAME, POS, DIFF_99, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult2 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_1, POS, DIFF_80, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult3 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_3, POS, DIFF_2, SEQUENCE_LENGTH);
            RESULTS_1.add(geneComparisonResult1);
            RESULTS_1.add(geneComparisonResult2);
            RESULTS_1.add(geneComparisonResult3);

            GeneComparisonResult geneComparisonResult4 = new GeneComparisonResult(X_CHR_NAME, POS, DIFF_2, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult5 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_3, POS, DIFF_10, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult6 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_2, POS, DIFF_55, SEQUENCE_LENGTH);
            RESULTS_2.add(geneComparisonResult4);
            RESULTS_2.add(geneComparisonResult5);
            RESULTS_2.add(geneComparisonResult6);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test(expected = GenomeException.class)
    public void CreationFromEmptyData() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(EMPTY_DATA);
    }

    @Test
    public void AnalyzeOfNotParentAndChild() throws Exception {
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_1);
        assertEquals(geneComparisonResultAnalyzer.toString(), CHECK_STRING_1);
        assertEquals(geneComparisonResultAnalyzer.areParentAndChild(), false);
    }

    @Test
    public void AnalyzeOfParentAndChild() throws Exception {
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_2);
        assertEquals(geneComparisonResultAnalyzer.toString(), CHECK_STRING_2);
        assertEquals(geneComparisonResultAnalyzer.areParentAndChild(), true);
    }

}
