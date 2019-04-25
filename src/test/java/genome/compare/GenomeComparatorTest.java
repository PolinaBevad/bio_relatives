package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
    private static final String FSEQ = "ATGACTATTCCACAACGTTAATACTCCGCGCTCTTGTGATCCAGGGCAGTTCGCAACTTAGAGGTTTCTTTATAG";

    /**
     * Test sequence of nucleotides with
     * len = 75.
     */
    private static final String SSEQ = "CTTGTAAGTATGCAGGGTCACGCGGGCAGATCGGGAGACATTAGATTGGACAAGCTTTAAACCGACGCGCACCCG";

    /**
     * Test sequence of nucleotides with
     * len = 9.
     */
    private static final String SSEQ_DISTURBED = "A*GTCA**G";

    /**
     * Test sequence of nucleotides with
     * len = 9.
     */
    private static final String FSEQ_DISTURBED = "AGGT*AAAG";

    /**
     * First sequence alignment.
     */
    private static final String FIRST_SEQ = "GATATTTCTTTGG-AGATT-C-AAC-GCTTGACGGGACCTAGTGTTCT-CGCGCCTCATAATTGCAACACCTTATCAGTA";

    /**
     * First sequence alignment.
     */
    private static final String SECOND_SEQ = "GCCCACGCGCAGCCAAATTTCGAACAGGTT-AGATTACAGAGGGCTAGACGGGCG-CAC--TGGGA-CGTATGAATGTTC";



    /**
     * Test array of genome REGIONS.
     */
    private static final GenomeRegion[] REGIONS = new GenomeRegion[6];

    @Before
    public void setUp() throws Exception {
        byte fqual[] = new byte[FSEQ.length()];
        for (int i = 0; i < fqual.length; i++) {
            fqual[i] = 100;
        }
        byte squal[] = new byte[SSEQ.length()];
        for (int i = 0; i < squal.length; i++) {
            squal[i] = 100;
        }

        byte[] qual = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        REGIONS[0] = new GenomeRegion("chr1", 0, FSEQ, fqual);
        REGIONS[1] = new GenomeRegion("chr2", 0, SSEQ, squal);
        REGIONS[2] = new GenomeRegion("chr2", 0, SSEQ, squal);
        REGIONS[3] = new GenomeRegion("chr1", 0, SSEQ, squal);
        REGIONS[4] = new GenomeRegion("chr3", 0, SSEQ_DISTURBED, qual);
        REGIONS[5] = new GenomeRegion("chr3", 0, FSEQ_DISTURBED, qual);
    }

    @Test(expected = GenomeException.class)
    public void GenomeComparatorConstructorTestWithDiffSizeArrays() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[0]);
        fl.add(REGIONS[1]);
        fl.add(REGIONS[2]);
        sl.add(REGIONS[1]);
        sl.add(REGIONS[2]);
        testObj = new GenomeComparator(fl, sl);
    }

    @Test
    public void GenomeComparatorConstructorTestWithSameSizeArrays() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[0]);
        fl.add(REGIONS[1]);
        sl.add(REGIONS[2]);
        sl.add(REGIONS[3]);
        testObj = new GenomeComparator(fl, sl);
    }

    @Test
    public void HemmingDistTest() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[0]);
        fl.add(REGIONS[1]);
        sl.add(REGIONS[2]);
        sl.add(REGIONS[3]);
        testObj = new GenomeComparator(fl, sl);
        assertEquals(testObj.HemmingDistance().get(0).getDifference(), new GeneComparisonResult("chr1", 0, 58, 75).getDifference());
        assertEquals(testObj.HemmingDistance().get(1).getDifference(), new GeneComparisonResult("chr2", 0, 0, 75).getDifference());
    }

    @Test
    public void LevenshteinDistTest() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[0]);
        fl.add(REGIONS[1]);
        sl.add(REGIONS[2]);
        sl.add(REGIONS[3]);
        testObj = new GenomeComparator(fl, sl);
        assertEquals(testObj.LevenshteinDistance().get(0).getDifference(), new GeneComparisonResult("chr1", 0, 46, 75).getDifference());
        assertEquals(testObj.LevenshteinDistance().get(1).getDifference(), new GeneComparisonResult("chr2", 0, 0, 75).getDifference());
    }

    @Test
    public void LevenshteinDisturbedTest() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[4]);
        sl.add(REGIONS[5]);
        testObj = new GenomeComparator(fl, sl);
        assertEquals(testObj.LevenshteinDistance().get(0).getDifference(), new GeneComparisonResult("chr2", 0, 0, 9).getDifference());
    }

    @Test
    public void NeedlemanWunschAlgorithmTest() throws Exception {
        ArrayList<GenomeRegion> fl = new ArrayList<>();
        ArrayList<GenomeRegion> sl = new ArrayList<>();
        fl.add(REGIONS[0]);
        fl.add(REGIONS[1]);
        sl.add(REGIONS[2]);
        sl.add(REGIONS[3]);

        testObj = new GenomeComparator(fl, sl);
        List<Pair<GeneComparisonResult, Integer[][]>> res = testObj.LevenshteinDistanceCanonical();
        Integer table[][] = res.get(0).getValue();

        StringBuilder alignmentA = new StringBuilder();
        StringBuilder alignmentB = new StringBuilder();

        int i = FSEQ.length();
        int j = SSEQ.length();

        while (i > 0 && j > 0) {
            int score = table[i][j];
            int diag = table[i - 1][j - 1];
            int up = table[i][j - 1];
            int left = table[i - 1][j];

            if (score == diag + (FSEQ.charAt(i - 1) == SSEQ.charAt(j - 1) ? 0 : 1)) {
                alignmentA.append(FSEQ.charAt(i - 1));
                alignmentB.append(SSEQ.charAt(j - 1));
                i -= 1;
                j -= 1;
            } else if (score == left + 1) {
                alignmentA.append(FSEQ.charAt(i - 1));
                alignmentB.append('-');
                i -= 1;
            } else if (score == up + 1) {
                alignmentB.append(SSEQ.charAt(j - 1));
                alignmentA.append('-');
                j -= 1;
            }
        }

        while (i > 0) {
            alignmentA.append(FSEQ.charAt(i - 1));
            alignmentB.append('-');
            i -= 1;
        }

        while (j > 0) {
            alignmentB.append(SSEQ.charAt(j - 1));
            alignmentA.append('-');
            j -= 1;
        }

        assertEquals(FIRST_SEQ, alignmentA.toString());
        assertEquals(SECOND_SEQ, alignmentB.toString());
    }
}
