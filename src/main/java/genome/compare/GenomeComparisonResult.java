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
public class GenomeComparisonResult {
    /**
     * Distance that was used to compare
     * two nucleotide sequences.
     */
    private GenomeComparator.DistanceType distance_type_;

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
     * @param type       Name of the distance.
     * @param difference Difference value.
     * @param len        Length of the nucl. seq.
     * @throws GenomeException if input values are lesser than 0.
     */
    public GenomeComparisonResult(GenomeComparator.DistanceType type, int difference, int len) throws
        GenomeException {
        this.distance_type_ = type;

        if (difference < 0) {
            throw new GenomeException(this.getClass().getName(), "GenomeComparisonResult", "difference", "is lesser than 0");
        }
        difference_ = difference;

        if (len < 0) {
            throw new GenomeException(this.getClass().getName(), "GenomeComparisonResult", "len", "is lesser than 0");
        }
        sequence_len_ = len;
    }

    /**
     * @return The type of distance used to calculate
     * the differences between genomes.
     */
    public GenomeComparator.DistanceType getDistanceType() {
        return distance_type_;
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
}
