/*
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
            byte [] fqual = new byte[FSEQ.length()];
            for (int i = 0; i < fqual.length; i++) {
                fqual[i] = 100;
            }
            byte [] squal = new byte[SSEQ.length()];
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
    public void GenomeComparatorConstructorTest() {
        testObj = new LevenshteinComparator(REGIONS[0], REGIONS[1]);
    }

    @Test
    public void LevenshteinDistTest() {
        testObj = new LevenshteinComparator(REGIONS[0], REGIONS[1]);
        assertEquals(testObj.compare().getDifference(), new LevenshteinComparisonResult("chr1", "00", 46, 75).getDifference());
    }
}
