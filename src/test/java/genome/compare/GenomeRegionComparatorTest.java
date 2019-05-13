package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.comparator.GenomeRegionComparator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link GenomeRegionComparator} class.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegionComparatorTest {
    /**
     * Test object of GenomeRegionComparator class.
     */
    private GenomeRegionComparator testObj;

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
        testObj = new GenomeRegionComparator(REGIONS[0], REGIONS[1]);
    }

    @Test
    public void HemmingDistTest() throws Exception {
        testObj = new GenomeRegionComparator(REGIONS[0], REGIONS[1]);
        assertEquals(testObj.HemmingDistance().getDifference(), new GeneComparisonResult("chr1", "00", 0, 58, 75).getDifference());
    }

    @Test
    public void LevenshteinDistTest() throws Exception {
        testObj = new GenomeRegionComparator(REGIONS[0], REGIONS[1]);
        assertEquals(testObj.LevenshteinDistance().getDifference(), new GeneComparisonResult("chr1", "00", 0, 46, 75).getDifference());
    }
}
