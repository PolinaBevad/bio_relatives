package genome.assembly;

import com.sun.javaws.exceptions.InvalidArgumentException;
import exception.InvalidRegionException;
import javafx.util.Pair;

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
    private String chrom_ = "";

    /**
     * Nucleotide sequence in this chromosome.
     */
    private String nucleotide_seq_ = "";

    /**
     * Start position of the nucleotide sequence.
     */
    private int start_pos_ = 0;

    /**
     * Array of qualities for each nucleotide in the sequence.
     */
    private byte nucleotide_quality_[] = null;

    /**
     * Default class constructor from the base information about each region in the bam file.
     *
     * @param chrom   Name of the chromosome.
     * @param pos     Starting position.
     * @param seq     Nucleotide sequence.
     * @param quality Arrays of qualities for each nucleotide in the sequence.
     * @throws InvalidRegionException if string parameters are null or if starting position
     *                                of the nucleotide sequence is < 0.
     */
    public GenomeRegion(String chrom, int pos, String seq, byte[] quality) throws InvalidRegionException {
        if (chrom == null) {
            throw new InvalidRegionException("GenomeRegion", "chrom", "null");
        }
        if (chrom.replaceAll("[^0-9]", "").isEmpty()) {
            throw new InvalidRegionException("GenomeRegion", "chrom", "wrong format of the chromosome's name");
        }
        this.chrom_ = chrom;

        if (pos < 0) {
            throw new InvalidRegionException("GenomeRegion", "pos", " < 0");
        }
        this.start_pos_ = pos;

        if (seq == null) {
            throw new InvalidRegionException("GenomeRegion", "seq", "null");
        }
        this.nucleotide_seq_ = seq;

        if (quality.length != seq.length()) {
            throw new InvalidRegionException("GenomeRegion", "seq", "not equals to the len of nucleotide sequence");
        }
        this.nucleotide_quality_ = quality;
    }

    /**
     * Returns information about nucleotide on the position pos, where pos is in range [0, len).
     *
     * @param pos Position of the nucleotide in the sequence.
     * @return Pair of nucleotide and it's quality.
     * @throws InvalidRegionException if pos is < 0 or len < pos.
     */
    public Pair<Character, Byte> getNucleotide(int pos) throws InvalidRegionException {
        if (pos < 0 || pos >= nucleotide_seq_.length()) {
            throw new InvalidRegionException("getNucleotide", "pos", "is not in range(0, len)");
        }
        return new Pair<>(nucleotide_seq_.charAt(pos), nucleotide_quality_[pos]);
    }

    /**
     * Returns normalized position of the nucleotide in this nucleotide sequence.
     *
     * @param position Position of the nucleotide in the whole genome.
     * @return Transformed position into position in the range [0, len).
     * @throws InvalidRegionException if nucleotide is not in the range of this nucleotide sequence.
     */
    public int normalize(int position) throws InvalidRegionException {
        if (position < start_pos_ || position > start_pos_ + nucleotide_seq_.length()) {
            throw new InvalidRegionException("getNucleotide", "pos", "is not in range(start_pos, start_pos + len)");
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
     * Get the start position of the chromosome.
     *
     * @return Start position of the chromosome.
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
     * Return nucleotide sequence from this region.
     *
     * @return
     */
    public String getNucleotideSequence() {
        return nucleotide_seq_;
    }
}
