package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            assertEquals(testObj.HemmingDistance().get(0).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 58, 75).getDifference());
            assertEquals(testObj.HemmingDistance().get(1).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 0, 75).getDifference());
        } catch (Exception ex) {
            ex.printStackTrace();
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
            assertEquals(testObj.LevenshteinDistance().get(0).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 46, 75).getDifference());
            assertEquals(testObj.LevenshteinDistance().get(1).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 0, 75).getDifference());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void AutoDistanceTest() {
        try {
            ArrayList<GenomeRegion> fl = new ArrayList<>();
            ArrayList<GenomeRegion> sl = new ArrayList<>();
            fl.add(regions[0]);
            fl.add(regions[1]);
            sl.add(regions[2]);
            sl.add(regions[3]);
            testObj = new GenomeComparator(fl, sl);
            List<GenomeComparisonResult> res = testObj.calculateDistance();
            assertEquals(res.get(0).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 58, 75).getDifference());
            assertEquals(res.get(1).getDifference(), new GenomeComparisonResult(GenomeComparator.DistanceType.HEMMING_DISTANCE, 0, 75).getDifference());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void NeedlemanWunschAlgorithmTest() {
        try {
            ArrayList<GenomeRegion> fl = new ArrayList<>();
            ArrayList<GenomeRegion> sl = new ArrayList<>();
            fl.add(regions[0]);
            fl.add(regions[1]);
            sl.add(regions[2]);
            sl.add(regions[3]);

            testObj = new GenomeComparator(fl, sl);
            List<Pair<GenomeComparisonResult, Integer[][]>> res = testObj.LevenshteinDistanceCanonical();
            Integer table[][] = res.get(0).getValue();

            StringBuilder alignmentA = new StringBuilder();
            StringBuilder alignmentB = new StringBuilder();

            int i = fseq.length();
            int j = sseq.length();

            while (i > 0 && j > 0) {
                int score = table[i][j];
                int diag = table[i - 1][j - 1];
                int up = table[i][j - 1];
                int left = table[i - 1][j];

                if (score == diag + (fseq.charAt(i - 1) == sseq.charAt(j - 1) ? 0 : 1)) {
                    alignmentA.append(fseq.charAt(i - 1));
                    alignmentB.append(sseq.charAt(j - 1));
                    i -= 1;
                    j -= 1;
                } else if (score == left + 1) {
                    alignmentA.append(fseq.charAt(i - 1));
                    alignmentB.append('-');
                    i -= 1;
                } else if (score == up + 1) {
                    alignmentB.append(sseq.charAt(j - 1));
                    alignmentA.append('-');
                    j -= 1;
                }
            }

            while (i > 0) {
                alignmentA.append(fseq.charAt(i - 1));
                alignmentB.append('-');
                i -= 1;
            }

            while (j > 0) {
                alignmentB.append(sseq.charAt(j - 1));
                alignmentA.append('-');
                j -= 1;
            }

            System.out.println(alignmentA);
            System.out.println(alignmentB);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
