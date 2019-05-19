package genome.compare;


import genome.compare.analyzis.GenomeRegionComparisonResult;
import genome.compare.comparator.GenomeComparator;
import genome.compare.comparator.threads.GenomeComparatorThread;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final static  String PATH_TO_SON_BAM_1 = "src/test/resources/genome/compare/testSonX.bam";

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
    private final static  String PATH_TO_DAD_BAM_1 = "src/test/resources/genome/compare/testDadX.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_3 = "src/test/resources/genome/compare/china/chinaFatherTest1000.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_3 = "src/test/resources/genome/compare/testMother4.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_SON_BAM_3 = "src/test/resources/genome/compare/china/chinaSonTest1000.bam";

    /**
     * Path to the first test BED file
     */
    private final static  String PATH_TO_BED = "src/test/resources/genome/compare/correct2.bed";

    @Ignore
    @Test
    public void GenomeComparisonOfNotParentAndChild() throws Exception {
        long startTime = System.currentTimeMillis();
        GenomeComparator comparator = new GenomeComparator(PATH_TO_DAD_BAM_3, PATH_TO_SON_BAM_3, PATH_TO_BED);
        Map<String, List<GenomeRegionComparisonResult>> result = comparator.compareGenomes();
        for (String gene: result.keySet()) {
            List<GenomeRegionComparisonResult> result1 = result.get(gene);
            int total_diff = 0;
            int total_len = 0;
            for (GenomeRegionComparisonResult res: result1) {
                System.out.println("Gene: " + gene + " Chrom: " + res.getChromName() + " - (diff; len) = (" + res.getDifference() + ", " + res.getSequenceLen() + ")");
                total_diff += res.getDifference();
                total_len += res.getSequenceLen();
            }

            System.out.println("Len: " + total_len + " Diff: " + total_diff + " %: " + (double)total_diff / total_len * 100);
        }
        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }

    @Ignore
    @Test
    public void GenomeComparisonOfNotParentAndChildThreads() throws Exception {
        long startTime = System.currentTimeMillis();
        GenomeComparatorThread comparator = new GenomeComparatorThread(PATH_TO_DAD_BAM_3, PATH_TO_SON_BAM_3, PATH_TO_BED);
        Map<String, List<GenomeRegionComparisonResult>> result = comparator.compareGenomes();
        for (String gene: result.keySet()) {
            List<GenomeRegionComparisonResult> result1 = result.get(gene);
            int total_diff = 0;
            int total_len = 0;
            for (GenomeRegionComparisonResult res: result1) {
                System.out.println("Gene: " + gene + " Chrom: " + res.getChromName() + " - (diff; len) = (" + res.getDifference() + ", " + res.getSequenceLen() + ")");
                total_diff += res.getDifference();
                total_len += res.getSequenceLen();
            }

            System.out.println("Len: " + total_len + " Diff: " + total_diff + " %: " + (double)total_diff / total_len * 100);
        }
        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }
}
