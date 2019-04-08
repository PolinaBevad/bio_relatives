package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link GenomeComparator} class.
 *
 * @author Sergey Khvatov
 */
public class GenomeComparatorTest {
    /**
     * Test object of GenomeComparator class.
     */
    private GenomeComparator testObj;

    /**
     * Test sequence of nucleotides with
     * len = 75.
     */
    private static final String fseq = "ATGACTATTCCACAACGTTAATACTCCGCGCTCTTGTGATCCAGGGCAGTTCGCAACTTAGAGGTTTCTTTATAG";

    /**
     * Test sequence of nucleotides with
     * len = 75.
     */
    private static final String sseq = "CTTGTAAGTATGCAGGGTCACGCGGGCAGATCGGGAGACATTAGATTGGACAAGCTTTAAACCGACGCGCACCCG";

    /**
     * Test array of genome regions.
     */
    private static GenomeRegion[] regions = new GenomeRegion[4];

    @Before
    public void setUp() {
        try {
            byte fqual[] = new byte[fseq.length()];
            for (int i = 0; i < fqual.length; i++) {
                fqual[i] = 100;
            }
            byte squal[] = new byte[sseq.length()];
            for (int i = 0; i < squal.length; i++) {
                squal[i] = 100;
            }

            regions[0] = new GenomeRegion("chr1", 0, fseq, fqual);
            regions[1] = new GenomeRegion("chr2", 0, sseq, squal);
            regions[2] = new GenomeRegion("chr2", 0, sseq, squal);
            regions[3] = new GenomeRegion("chr1", 0, sseq, squal);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test(expected = GenomeException.class)
    public void GenomeComparatorConstructorTestWithDiffSizeArrays() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(regions[0]);
        fl.add(regions[1]);
        fl.add(regions[2]);
        sl.add(regions[1]);
        sl.add(regions[2]);
        testObj = new GenomeComparator(fl, sl);
    }

    @Test
    public void GenomeComparatorConstructorTestWithSameSizeArrays() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(regions[0]);
        fl.add(regions[1]);
        sl.add(regions[1]);
        sl.add(regions[2]);
        testObj = new GenomeComparator(fl, sl);
    }

    @Test
    public void HemmingDistTest() {
        try {
            ArrayList<GenomeRegion> fl = new ArrayList<>();
            ArrayList<GenomeRegion> sl = new ArrayList<>();
            fl.add(regions[0]);
            fl.add(regions[1]);
            sl.add(regions[2]);
            sl.add(regions[3]);
            testObj = new GenomeComparator(fl, sl);
            assertEquals(testObj.HemmingDistance().get(0), new Pair<>(58, 75));
            assertEquals(testObj.HemmingDistance().get(1), new Pair<>(0, 75));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void LevenshteinDistTest() {
        try {
            ArrayList<GenomeRegion> fl = new ArrayList<>();
            ArrayList<GenomeRegion> sl = new ArrayList<>();
            fl.add(regions[0]);
            fl.add(regions[1]);
            sl.add(regions[2]);
            sl.add(regions[3]);
            testObj = new GenomeComparator(fl, sl);
            assertEquals(testObj.LevenshteinDistance().get(0), new Pair<>(46, 75));
            assertEquals(testObj.LevenshteinDistance().get(1), new Pair<>(0, 75));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
