package genome.compare;

import genome.compare.analyzis.ComparisonResultAnalyzer;
import executors.GenomeComparatorExecutor;
import org.junit.Test;

/**
 * Tests for the {@link } class.
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

    @Test
    public void GenomeComparisonOfNotParentAndChild() throws Exception {
        long startTime = System.currentTimeMillis();
        GenomeComparatorExecutor comparator = new GenomeComparatorExecutor(PATH_TO_SON_BAM_1, PATH_TO_DAD_BAM_1, PATH_TO_BED, ComparatorType.LEVENSHTEIN);
        ComparisonResultAnalyzer result = comparator.compareGenomes(1, false);
        System.out.println(result);
        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }

}
