package genome.assembly;

import exception.GenomeException;
import util.Pair;

/**
 * This class stores genome information and data for each region in BED file.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegion {
    /**
     * Name of the chromosome
     * which contains this nucleotide sequence.
     */
    private String chrom_;

    /**
     * Nucleotide sequence in this chromosome.
     */
    private String nucleotide_seq_;

    /**
     * Start position of the nucleotide sequence.
     */
    private int start_pos_;

    /**
     * Array of qualities for each nucleotide in the sequence.
     */
    private byte[] nucleotide_quality_;

    /**
     * Default class constructor from the base information about each region in the bam file.
     *
     * @param chrom   Name of the chromosome.
     * @param pos     Starting position.
     * @param seq     Nucleotide sequence.
     * @param quality Arrays of qualities for each nucleotide in the sequence.
     * @throws GenomeException if starting position
     *                                of the nucleotide sequence is < 0.
     */
    public GenomeRegion(String chrom, int pos, String seq, byte[] quality) throws GenomeException {
        // set the name of the chromosome
        this.chrom_ = chrom;

        // check the start position
        if (pos < 0) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "pos", " < 0");
        }
        this.start_pos_ = pos;
        this.nucleotide_seq_ = seq;

        // check the quality array
        if (quality.length != seq.length()) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "seq", "not equals to the len of nucleotide sequence");
        }
        this.nucleotide_quality_ = quality;
    }

    /**
     * Returns information about nucleotide on the position pos, where pos is in range [0, len).
     *
     * @param pos Position of the nucleotide in the sequence.
     * @return Pair of nucleotide and it's quality.
     * @throws GenomeException if pos is < 0 or len < pos.
     */
    public Pair<Character, Byte> getNucleotide(int pos) throws GenomeException {
        if (pos < 0 || pos >= nucleotide_seq_.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(0, len)");
        }
        return new Pair<>(nucleotide_seq_.charAt(pos), nucleotide_quality_[pos]);
    }

    /**
     * Returns normalized position of the nucleotide in this nucleotide sequence.
     *
     * @param position Position of the nucleotide in the whole genome.
     * @return Transformed position into position in the range [0, len).
     * @throws GenomeException if nucleotide is not in the range of this nucleotide sequence.
     */
    public int normalize(int position) throws GenomeException {
        if (position < start_pos_ || position > start_pos_ + nucleotide_seq_.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(start_pos, start_pos + len)");
        }
        return position - start_pos_;
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

    /**
     * Get the length of the nucleotide sequence.
     *
     * @return The length of the nucleotide sequence.
     */
    public int getNucleotideLength() {
        return nucleotide_seq_.length();
    }

    /**
     * @return nucleotide sequence from this region.
     */
    public String getNucleotideSequence() {
        return nucleotide_seq_;
    }

    /**
     * Basic implementation for comparing two genome regions.
     *
     * @param other Other {@link GenomeRegion} object.
     * @return True, if both genome regions are from the same chromosome
     * and start from the same position, false otherwise. If other is not an instance
     * of {@link GenomeRegion}, than return false.
     */
    public boolean equals(Object other) {
        if (other instanceof GenomeRegion)
            return this.chrom_.equals(((GenomeRegion) other).chrom_)
                && this.start_pos_ == ((GenomeRegion) other).start_pos_;
        return false;
    }
}
