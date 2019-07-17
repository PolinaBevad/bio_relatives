/*
 * MIT License
 *
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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
import util.Pair;

/**
 * This class stores genome information and data for each region in BED file.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegion {

    /**
     * Regular expression that contains all
     * the allowed symbols for the gene name.
     */
    private static final String ALLOWED_SYMBOLS_REGEXP = "[a-zA-Z0-9.\\-_+]*";


    /**
     * Name of the chromosome
     * which contains this nucleotide sequence.
     */
    private String chrom;

    /**
     * Nucleotide sequence in this chromosome.
     */
    private String nucleotideSeq;

    /**
     * Start position of the nucleotide sequence.
     */
    private int startPos;

    /**
     * Array of qualities for each nucleotide in the sequence.
     */
    private byte[] nucleotideQuality;

    /**
     * Name of the gene that is located in this genome region.
     */
    private String gene;

    /**
     * Default class constructor from the base information about each region in the bam file.
     *
     * @param chrom   Name of the chromosome.
     * @param pos     Starting position.
     * @param seq     Nucleotide sequence.
     * @param quality Arrays of qualities for each nucleotide in the sequence.
     * @param gene    Name of the gene.
     * @throws GenomeException if starting position of the nucleotide sequence is < 0.
     */
    public GenomeRegion(String chrom, int pos, String seq, byte[] quality, String gene) {
        // set the name of the chromosome
        this.chrom = chrom;

        // check the start position
        if (pos < 0) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "pos", " < 0");
        }
        this.startPos = pos;
        this.nucleotideSeq = seq;
        this.gene = gene;

        if (!gene.matches(ALLOWED_SYMBOLS_REGEXP)) {
            throw new GenomeException("Error occurred during initialization of BEDFeature object: " + "Incorrect parameters were passed: [" + chrom + ", " + startPos + ", " + gene);
        }

        // check the quality array
        if (quality.length != seq.length()) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "seq", "not equals to the len of nucleotide sequence");
        }
        this.nucleotideQuality = quality;
    }

    /**
     * Returns information about nucleotide on the position pos, where pos is in range [0, len).
     *
     * @param pos Position of the nucleotide in the sequence.
     * @return Pair of nucleotide and it's quality.
     * @throws GenomeException if pos is < 0 or len < pos.
     */
    public Pair<Character, Byte> getNucleotide(int pos) {
        if (pos < 0 || pos >= nucleotideSeq.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(0, len)");
        }
        return new Pair<>(nucleotideSeq.charAt(pos), nucleotideQuality[pos]);
    }

    /**
     * Returns normalized position of the nucleotide in this nucleotide sequence.
     *
     * @param position Position of the nucleotide in the whole genome.
     * @return Transformed position into position in the range [0, len).
     * @throws GenomeException if nucleotide is not in the range of this nucleotide sequence.
     */
    public int normalize(int position) {
        if (position < startPos || position > startPos + nucleotideSeq.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(startPos, startPos + len)");
        }
        return position - startPos;
    }

    /**
     * Get the name of the chromosome.
     *
     * @return Name of the chromosome.
     */
    public String getChromName() {
        return chrom;
    }

    /**
     * Get the start position of the gene.
     *
     * @return Start position of the gene.
     */
    public int getStart() {
        return startPos;
    }

    /**
     * Get the length of the nucleotide sequence.
     *
     * @return The length of the nucleotide sequence.
     */
    public int getNucleotideLength() {
        return nucleotideSeq.length();
    }

    /**
     * @return nucleotide sequence from this region.
     */
    public String getNucleotideSequence() {
        return nucleotideSeq;
    }

    /**
     * @return name of the gene that is located in this region.
     */
    public String getGene() {
        return gene;
    }

    /**
     * Basic implementation for comparing two genome regions by their parameters such as
     * name of the chromosome, gene and start position.
     *
     * @param other Other {@link GenomeRegion} object.
     * @return True, if both genome regions are from the same chromosome
     * and start from the same position, false otherwise. If other is not an instance
     * of {@link GenomeRegion}, than return false.
     */
    public boolean equals(Object other) {
        if (other instanceof GenomeRegion) {
            GenomeRegion otherRegion = (GenomeRegion) other;
            return this.chrom.equals(otherRegion.chrom) && this.gene.equals(otherRegion.gene) && this.startPos == otherRegion.startPos;
        }
        return false;
    }
}
