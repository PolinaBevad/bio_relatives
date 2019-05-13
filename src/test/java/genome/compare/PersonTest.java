package genome.compare;


import bam.BAMParser;
import bam.BEDFeature;
import bam.BEDParser;
import genome.assembly.GenomeConstructor;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.comparator.GenomeComparator;
import htsjdk.samtools.SAMRecord;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tests for the {@link GenomeComparator} class.
 *
 * @author Vladislav Marchenko
 */
public class PersonTest {
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
    private final static  String PATH_TO_DAD_BAM_3 = "src/test/resources/genome/compare/testDad4.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_3 = "src/test/resources/genome/compare/testMother4.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_SON_BAM_3 = "src/test/resources/genome/compare/testSon4.bam";

    /**
     * Path to the first test BED file
     */
    private final static  String PATH_TO_BED = "src/test/resources/genome/compare/correct3.bed";

    @Ignore
    @Test
    public void GenomeComparisonOfNotParentAndChild() throws Exception {
        GenomeComparator comparator = new GenomeComparator("D:\\BIO_DATA\\mother3M.bam","D:\\BIO_DATA\\father3M.bam", PATH_TO_BED);
        HashMap<String, ArrayList<GeneComparisonResult>> result = comparator.compareGenomes();
        for (String gene: result.keySet()) {
            ArrayList<GeneComparisonResult> result1 = result.get(gene);
            int total_diff = 0;
            int total_len = 0;
            for (GeneComparisonResult res: result1) {
                System.out.println("Gene: " + gene + " Chrom: " + res.getChromName() + " - (diff; len) = (" + res.getDifference() + ", " + res.getSequenceLen() + ")");
                total_diff += res.getDifference();
                total_len += res.getSequenceLen();
            }

            System.out.println("Len: " + total_len + " Diff: " + total_diff + " %: " + (double)total_diff / total_len * 100);
        }
    }
}
