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

package genome.assembly;

import exception.GenomeException;
import org.junit.Test;
import util.Pair;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GenomeRegion} class.
 *
 * @author Vladislav Marchenko
 */

public class GenomeRegionTest {

    /**
     * Invalid starting position of region
     */
    private static final int INVALID_START_POSITION = -1;

    /**
     * Array of qualities with invalid length
     */
    private static final byte[] INVALID_QUALITIES = {10, 34, 35};

    /**
     * Valid chromosome name
     */
    private static final String VALID_CHROM_NAME = "chr1";

    /**
     * Valid starting position of region
     */
    private static final int VALID_START_POSITION = 168900;

    /**
     * Valid nucleotide sequence
     */
    private static final String VALID_SEQ = "AGCTAGCTAGCT";

    /**
     * Valid array of qualities
     */
    private static final byte[] VALID_QUALITIES = {25, 70, 50, 60, 90, 20, 30, 55, 51, 57, 54, 58};

    /**
     * Invalid position of nucleotide in the region
     */
    private static final int INVALID_POSITION = 10000000;

    /**
     * Valid position of nucleotide in the region
     */
    private static final int VALID_POSITION = 3;

    /**
     * Valid position in genome
     */
    private static final int VALID_POSITION_IN_GENOME = 168903;

    private final static String VALID_GENE_NAME = "klkjlk1";

    private final static String INVALID_GENE_NAME = "@---";

    @Test(expected = GenomeException.class)
    public void CreationFromInvalidPosition() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, INVALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, VALID_GENE_NAME);
    }

    @Test(expected = GenomeException.class)
    public void CreationFromInvalidGene() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, INVALID_GENE_NAME);
    }

    @Test(expected = GenomeException.class)
    public void CreationFromInvalidQualities() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, INVALID_QUALITIES, VALID_GENE_NAME);
    }

    @Test
    public void CreationFromValidData() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, VALID_GENE_NAME);
    }

    @Test(expected = GenomeException.class)
    public void GettingNucleotideFromInvalidPosition() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES,VALID_GENE_NAME);
        genomeRegion.getNucleotide(INVALID_POSITION);
    }

    @Test(expected = GenomeException.class)
    public void NormalizationOfInvalidPosition() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, VALID_GENE_NAME);
        genomeRegion.normalize(INVALID_POSITION);
    }

    @Test
    public void GettingNucleotideFromValidePosition() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, VALID_GENE_NAME);
        Pair<Character, Byte> nucleotide = genomeRegion.getNucleotide(VALID_POSITION);
        assertEquals(nucleotide.getKey(), (Character) VALID_SEQ.charAt(VALID_POSITION));
    }

    @Test
    public void NormalizationOfValidPosition() {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES, VALID_GENE_NAME);
        int position = genomeRegion.normalize(VALID_POSITION_IN_GENOME);
        assertEquals(position, VALID_POSITION);
    }
}
