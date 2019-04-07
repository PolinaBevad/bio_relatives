package genome.assembly;

import exception.InvalidRegionException;
import javafx.util.Pair;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the {@link GenomeRegion} class.
 *
 * @author Vladislav Marchenko
 */

public class GenomeRegionTest {
    /**
     * Null input chromosome name
     */
    private static final String NULL_CHROM_NAME = null;

    /**
     * Invalid starting position of region
     */
    private static final int INVALID_START_POSITION = -1;

    /**
     * Null input nucleotide sequence
     */
    private static final String NULL_SEQ = null;

    /**
     * Array of qualities with invalid length
     */
    private static final byte [] INVALID_QUALITIES = {10, 34, 35};

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
    private static final byte [] VALID_QUALITIES = {25, 70, 50, 60, 90, 20, 30, 55, 51, 57, 54, 58};

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

    @Test(expected = InvalidRegionException.class)
    public void CreationFromNullChrom() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(NULL_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
    }

    @Test(expected = InvalidRegionException.class)
    public void CreationFromInvalidPosition() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, INVALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
    }

    @Test(expected = InvalidRegionException.class)
    public void CreationFromNullSequence() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, NULL_SEQ, VALID_QUALITIES);
    }

    @Test(expected = InvalidRegionException.class)
    public void CreationFromInvalidQualities() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, INVALID_QUALITIES);
    }

    @Test
    public void CreationFromValidData() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
    }

    @Test(expected = InvalidRegionException.class)
    public void GettingNucleotideFromInvalidPosition() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
        genomeRegion.getNucleotide(INVALID_POSITION);
    }

    @Test(expected = InvalidRegionException.class)
    public void NormalizationOfInvalidPosition() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
        genomeRegion.normalize(INVALID_POSITION);
    }

    @Test
    public void GettingNucleotideFromValidePosition() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
        Pair<Character, Byte> nucleotide = genomeRegion.getNucleotide(VALID_POSITION);
        assertEquals(nucleotide.getKey(), (Character) VALID_SEQ.charAt(VALID_POSITION));
    }

    @Test
    public void NormalizationOfValidPosition() throws Exception {
        GenomeRegion genomeRegion = new GenomeRegion(VALID_CHROM_NAME, VALID_START_POSITION, VALID_SEQ, VALID_QUALITIES);
        int position = genomeRegion.normalize(VALID_POSITION_IN_GENOME);
        assertEquals(position, VALID_POSITION);
    }
}
