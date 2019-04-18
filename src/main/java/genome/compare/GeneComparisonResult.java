package genome.compare;

import exception.GenomeException;

/**
 * This class contains the results of the comparison of
 * two nucleotide sequences. Object of this class is a result
 * of comparison of two {@link genome.assembly.GenomeRegion}
 * made by {@link GenomeComparator}.
 *
 * @author Sergey Khvatov
 */
public class GeneComparisonResult {

    /**
     * Name of the chromosome
     * which contains this nucleotide sequence.
     */
    private String chrom_;

    /**
     * Start position of the nucleotide sequence.
     */
    private int start_pos_;

    /**
     * The distance value that was calculated
     * by the {@link GenomeComparator} methods.
     */
    private int difference_;

    /**
     * Contains the length of the
     * longest nucleotide sequence
     * among the compared.
     */
    private int sequence_len_;

    /**
     * Default class constructor from name of the distance,
     * that was used to calculate the distance between two genomes,
     * the distance value and the length of the longest nucleotide sequence
     * between compared ones.
     *
     * @param chrom      Name of the chromosome.
     * @param pos        It's start position in the chrom.
     * @param difference Difference value.
     * @param len        Length of the nucl. seq.
     * @throws GenomeException if input values are lesser than 0 or the chromosome name is invalid.
     */
    public GeneComparisonResult(String chrom, int pos, int difference, int len) throws GenomeException {
        this.chrom_ = chrom;

        // check the position
        if (pos < 0) {
            throw new GenomeException("GenomeRegion", "pos", " < 0");
        }
        this.start_pos_ = pos;

        // check the diff value
        if (difference < 0) {
            throw new GenomeException(this.getClass().getName(), "GeneComparisonResult", "difference", "is lesser than 0");
        }
        difference_ = difference;

        // check the len value
        if (len < 0) {
            throw new GenomeException(this.getClass().getName(), "GeneComparisonResult", "len", "is lesser than 0");
        }
        sequence_len_ = len;
    }

    /**
     * @return The difference distance between two genomes.
     */
    public int getDifference() {
        return difference_;
    }

    /**
     * @return The length of the compared sequences.
     */
    public int getSequenceLen() {
        return sequence_len_;
    }

    /**
     * Get the name of the chromosome.
     *
     * @return Name of the chromosome.
     */
    public String getChromName() {
        return chrom_;
    }

    /**
     * Get the start position of the gene.
     *
     * @return Start position of the gene.
     */
    public int getStart() {
        return start_pos_;
    }
}
