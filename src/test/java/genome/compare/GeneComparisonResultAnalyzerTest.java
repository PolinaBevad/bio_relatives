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
    private final static int DIFF_1 = 2;

    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_2 = 10;

    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_3 = 20;
    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_4 = 65;
    /**
     * Count of differences between two sequences
     */
    private final static int DIFF_5 = 99;

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
     * Test List of results
     */
    private List<GeneComparisonResult> RESULTS_3 = new ArrayList<>();

    /**
     * Test List of results
     */
    private List<GeneComparisonResult> RESULTS_4 = new ArrayList<>();

    /**
     * Test empty input data
     */
    private List<GeneComparisonResult> EMPTY_DATA = new ArrayList<>();

    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_1 = "Criterion of comparison of mitochondrial chromosomes.\n"
            +"Persons under study are parent and child.\n"
            +"The average percentage of similarity - "
            + 98d;
    /**
     * Check object with the best value of similarity
     */
    private final static Pair<String, Double> CHECK_OBJ_1 = new Pair<>("MT_CHROMOSOME", 98d);

    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_2 = "Criterion of comparison of X chromosomes.\n"
            +"Persons under study are parent and child.\n"
            +"The average percentage of similarity - "
            + 80d;
    /**
     * Check object with the best value of similarity
     */
    private final static Pair<String, Double> CHECK_OBJ_2 = new Pair<>("X_CHROMOSOME", 80d);

    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_3 = "Criterion of comparison of autosomal chromosomes.\n"
            +"Persons under study are parent and child.\n"
            +"The average percentage of similarity - "
            + 90d;
    /**
     * Check object with the best value of similarity
     */
    private final static Pair<String, Double> CHECK_OBJ_3 = new Pair<>("AUTOSOMAL_CHROMOSOME", 90d);

    /**
     * Check String with results of analysis
     */
    private final static String CHECK_STRING_4 = "Persons under study are not parent and child.\n"
            + "The average percentage of similarity of mitochondrial chromosomes - "
            + 1d + "\n"
            + "The average percentage of similarity of X chromosomes - "
            + 1d +"\n"
            + "The average percentage of similarity of autosomal chromosomes - "
            + 1d +"\n";
    /**
     * Check object with the best value of similarity
     */
    private final static Pair<String, Double> CHECK_OBJ_4 = new Pair<>("MT_CHROMOSOME", 1d);

    @Before
    public void setUp() {
        try {
            GeneComparisonResult geneComparisonResult1 = new GeneComparisonResult(MT_CHR_NAME, POS, DIFF_1, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult2 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_1, POS, DIFF_3, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult3 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_3, POS, DIFF_4, SEQUENCE_LENGTH);
            RESULTS_1.add(geneComparisonResult1);
            RESULTS_1.add(geneComparisonResult2);
            RESULTS_1.add(geneComparisonResult3);

            GeneComparisonResult geneComparisonResult4 = new GeneComparisonResult(X_CHR_NAME, POS, DIFF_3, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult5 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_3, POS, DIFF_5, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult6 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_2, POS, DIFF_3, SEQUENCE_LENGTH);
            RESULTS_2.add(geneComparisonResult4);
            RESULTS_2.add(geneComparisonResult5);
            RESULTS_2.add(geneComparisonResult6);

            GeneComparisonResult geneComparisonResult7 = new GeneComparisonResult(MT_CHR_NAME, POS, DIFF_3, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult8 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_1, POS, DIFF_2, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult9 = new GeneComparisonResult(X_CHR_NAME, POS, DIFF_5, SEQUENCE_LENGTH);
            RESULTS_3.add(geneComparisonResult7);
            RESULTS_3.add(geneComparisonResult8);
            RESULTS_3.add(geneComparisonResult9);

            GeneComparisonResult geneComparisonResult10 = new GeneComparisonResult(MT_CHR_NAME, POS, DIFF_5, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult11 = new GeneComparisonResult(AUTOSOMAL_CHR_NAME_1, POS, DIFF_5, SEQUENCE_LENGTH);
            GeneComparisonResult geneComparisonResult12 = new GeneComparisonResult(X_CHR_NAME, POS, DIFF_5, SEQUENCE_LENGTH);
            RESULTS_4.add(geneComparisonResult10);
            RESULTS_4.add(geneComparisonResult11);
            RESULTS_4.add(geneComparisonResult12);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test(expected = GenomeException.class)
    public void CreationFromEmptyData() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(EMPTY_DATA);
    }

    @Test
    public void GeneComparisonResultAnalyzerTestOfResultsWithTheBestMTChromosome() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_1);
        assertEquals(CHECK_STRING_1, geneComparisonResultAnalyzer.toString());
        assertEquals(CHECK_OBJ_1.getKey(), geneComparisonResultAnalyzer.getAverageSimilarity().getKey());
        assertEquals(CHECK_OBJ_1.getValue(), geneComparisonResultAnalyzer.getAverageSimilarity().getValue());
    }

    @Test
    public void GeneComparisonResultAnalyzerTestOfResultsWithTheBestXChromosome() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_2);
        assertEquals(CHECK_STRING_2, geneComparisonResultAnalyzer.toString());
        assertEquals(CHECK_OBJ_2.getKey(), geneComparisonResultAnalyzer.getAverageSimilarity().getKey());
        assertEquals(CHECK_OBJ_2.getValue(), geneComparisonResultAnalyzer.getAverageSimilarity().getValue());
    }

    @Test
    public void GeneComparisonResultAnalyzerTestOfResultsWithTheBestAutosomalChromosome() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_3);
        assertEquals(CHECK_STRING_3, geneComparisonResultAnalyzer.toString());
        assertEquals(CHECK_OBJ_3.getKey(), geneComparisonResultAnalyzer.getAverageSimilarity().getKey());
        assertEquals(CHECK_OBJ_3.getValue(), geneComparisonResultAnalyzer.getAverageSimilarity().getValue());
    }

    @Test
    public void GeneComparisonResultAnalyzerTestOfResultsWithoutTheBestChromosome() throws Exception{
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = new GeneComparisonResultAnalyzer(RESULTS_4);
        assertEquals(CHECK_STRING_4, geneComparisonResultAnalyzer.toString());
        assertEquals(CHECK_OBJ_4.getKey(), geneComparisonResultAnalyzer.getAverageSimilarity().getKey());
        assertEquals(CHECK_OBJ_4.getValue(), geneComparisonResultAnalyzer.getAverageSimilarity().getValue());
    }



}
