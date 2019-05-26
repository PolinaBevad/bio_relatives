/**
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

import java.util.HashSet;

/**
 * This class stores genome information and data for each region in BED file.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegion {

    /**
     * Set of characters that are allowed in genome symbol name.
     */
    private static HashSet<Character> ALLOWED_SYMBOLS = new HashSet<>();

    static {
        for (char ch = 'a'; ch <= 'z'; ch++) {
            ALLOWED_SYMBOLS.add(ch);
        }
    }

    /**
     * Set of numbers that are allowed in genome symbol name.
     */
    private static HashSet<Character> ALLOWED_NUMBERS = new HashSet<>();

    static {
        for (char ch = '0'; ch <= '9'; ch++) {
            ALLOWED_NUMBERS.add(ch);
        }
    }

    /**
     * Name of the chromosome
     * which contains this nucleotide sequence.
     */
    private String chrom;

    /**
     * Nucleotide sequence in this chromosome.
     */
    private String nucleotideseq;

    /**
     * Start position of the nucleotide sequence.
     */
    private int startpos;

    /**
     * Array of qualities for each nucleotide in the sequence.
     */
    private byte[] nucleotidequality;

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
     * @throws GenomeException if starting position
     *                         of the nucleotide sequence is < 0.
     */
    public GenomeRegion(String chrom, int pos, String seq, byte[] quality, String gene) throws GenomeException {
        // set the name of the chromosome
        this.chrom = chrom;

        // check the start position
        if (pos < 0) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "pos", " < 0");
        }
        this.startpos = pos;
        this.nucleotideseq = seq;
        this.gene = gene;

        for (char ch : this.gene.toLowerCase().toCharArray()) {
            if (!ALLOWED_SYMBOLS.contains(ch) && !ALLOWED_NUMBERS.contains(ch)) {
                throw new GenomeException(this.getClass().getName(), "GenomeRegion", "gene", "incorrect value");
            }
        }

        // check the quality array
        if (quality.length != seq.length()) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegion", "seq", "not equals to the len of nucleotide sequence");
        }
        this.nucleotidequality = quality;
    }

    /**
     * Returns information about nucleotide on the position pos, where pos is in range [0, len).
     *
     * @param pos Position of the nucleotide in the sequence.
     * @return Pair of nucleotide and it's quality.
     * @throws GenomeException if pos is < 0 or len < pos.
     */
    public Pair<Character, Byte> getNucleotide(int pos) throws GenomeException {
        if (pos < 0 || pos >= nucleotideseq.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(0, len)");
        }
        return new Pair<>(nucleotideseq.charAt(pos), nucleotidequality[pos]);
    }

    /**
     * Returns normalized position of the nucleotide in this nucleotide sequence.
     *
     * @param position Position of the nucleotide in the whole genome.
     * @return Transformed position into position in the range [0, len).
     * @throws GenomeException if nucleotide is not in the range of this nucleotide sequence.
     */
    public int normalize(int position) throws GenomeException {
        if (position < startpos || position > startpos + nucleotideseq.length()) {
            throw new GenomeException(this.getClass().getName(), "getNucleotide", "pos", "is not in range(startpos, startpos + len)");
        }
        return position - startpos;
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
        return startpos;
    }

    /**
     * Get the length of the nucleotide sequence.
     *
     * @return The length of the nucleotide sequence.
     */
    public int getNucleotideLength() {
        return nucleotideseq.length();
    }

    /**
     * @return nucleotide sequence from this region.
     */
    public String getNucleotideSequence() {
        return nucleotideseq;
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
            return this.chrom.equals(otherRegion.chrom) && this.gene.equals(otherRegion.gene) && this.startpos == otherRegion.startpos;
        }
        return false;
    }
}
