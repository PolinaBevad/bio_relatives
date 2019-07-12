package genome.compare;

import executors.GenomeComparatorExecutor;
import genome.compare.common.ComparatorType;
import genome.compare.common.ComparisonResultAnalyzer;
import genome.compare.common.GenomeComparator;
import org.junit.Test;

/**
 * Tests for the {@link GenomeComparator} class.
 *
 * @author Vladislav Marchenko
 */
public class GenomeComparatorTest {
    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_1 = "src/test/resources/genome/compare/testMotherX.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_SON_BAM_1 = "src/test/resources/genome/compare/testSon4.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_2 = "src/test/resources/genome/compare/testMotherMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_SON_BAM_2 = "src/test/resources/genome/compare/testSonMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_2 = "src/test/resources/genome/compare/testDadMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_1 = "src/test/resources/genome/compare/testDad4.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_3 = "H:\\china\\chinaFatherTest1000.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_3 = "src/test/resources/genome/compare/mother.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_SON_BAM_3 = "H:\\china\\chinaSonTest1000.bam";

    /**
     * Path to the first test BED file
     */
    private final static  String PATH_TO_BED = "src/test/resources/genome/compare/correct.bed";

    /**
     * Path to the bed file with marker regions
     */
    private final static  String PATH_TO_MARKER_BED = "src/test/resources/genome/compare/Ymarker.bed";

    /**
     * Path to marker dad bam
     */
    private final static String PATH_TO_DAD_Y ="src/test/resources/genome/compare/tdadY.bam";

    /**
     * Path to marker son bam
     */
    private final static String PATH_TO_SON_Y="src/test/resources/genome/compare/tsonY.bam";

    @Test
    public void GenomeComparisonOfNotParentAndChild() {
        long startTime = System.currentTimeMillis();
        GenomeComparatorExecutor comparator = new GenomeComparatorExecutor(PATH_TO_SON_BAM_1, PATH_TO_DAD_BAM_1, PATH_TO_BED, ComparatorType.LEVENSHTEIN);
        ComparisonResultAnalyzer result = comparator.compareGenomes(3, false);
        System.out.println(result.getResultString());
        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void YMarkerComparisonOfDadAndSon() {
        GenomeComparatorExecutor comparator = new GenomeComparatorExecutor(PATH_TO_SON_Y, PATH_TO_DAD_Y, PATH_TO_MARKER_BED, ComparatorType.Y_STR);
        ComparisonResultAnalyzer res = comparator.compareGenomes(4, false);
        res.analyze();
        System.out.println(res.getResultString());
    }



}
