package genome.compare;

import genome.assembly.GenomeRegion;
import genome.compare.levenshtein.LevenshteinComparisonResult;
import genome.compare.levenshtein.LevenshteinComparator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Sergey Khvatov
 */
public class LevenshteinComparatorTest {
    /**
     * Test object of GenomeRegionComparator class.
     */
    private LevenshteinComparator testObj;

    /**
     * Test sequence of nucleotides with
     * len = 75.
     */
    private static final String FSEQ = "ATGACTATTCCACAACGTTAATACTCCGCGCTCTTGTGATCCAGGGCAGTTCGCAACTTAGAGGTTTCTTTATAG";

    /**
     * Test sequence of nucleotides with
     * len = 75.
     */
    private static final String SSEQ = "CTTGTAAGTATGCAGGGTCACGCGGGCAGATCGGGAGACATTAGATTGGACAAGCTTTAAACCGACGCGCACCCG";


    /**
     * Test array of genome REGIONS.
     */
    private static final GenomeRegion[] REGIONS = new GenomeRegion[2];

    @Before
    public void setUp() {
        try {
            byte fqual[] = new byte[FSEQ.length()];
            for (int i = 0; i < fqual.length; i++) {
                fqual[i] = 100;
            }
            byte squal[] = new byte[SSEQ.length()];
            for (int i = 0; i < squal.length; i++) {
                squal[i] = 100;
            }

            REGIONS[0] = new GenomeRegion("chr1", 0, FSEQ, fqual, "00");
            REGIONS[1] = new GenomeRegion("chr1", 0, SSEQ, squal, "00");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void GenomeComparatorConstructorTest() throws Exception {
        testObj = new LevenshteinComparator(REGIONS[0], REGIONS[1]);
    }

    @Test
    public void LevenshteinDistTest() throws Exception {
        testObj = new LevenshteinComparator(REGIONS[0], REGIONS[1]);
        assertEquals(testObj.compare().getDifference(), new LevenshteinComparisonResult("chr1", "00", 46, 75).getDifference());
    }
}
