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

package genome.compare.analyzis;

import exception.GenomeException;
import genome.compare.ComparatorType;

/**
 * This class contains the results of the comparison of
 * two nucleotide sequences. Object of this class is a result
 * of comparison of two {@link genome.assembly.GenomeRegion}.
 *
 * @author Sergey Khvatov
 * @author Vladislav Marchenko
 */
public class LevenshteinComparisonResult implements ComparisonResult {

    /**
     * Name of the chromosome
     * which contains this nucleotide sequence.
     */
    private String chrom;

    /**
     * The distance value that was calculated.
     */
    private int difference;

    /**
     * Contains the length of the
     * longest nucleotide sequence
     * among the compared.
     */
    private int sequenceLen;

    /**
     * Contains the name of the gene,
     * that is located in the chromosome
     * in this region.
     */
    private String gene;

    /**
     * Default class constructor from name of the distance,
     * that was used to calculate the distance between two genomes,
     * the distance value and the length of the longest nucleotide sequence
     * between compared ones.
     *
     * @param chrom      Name of the chromosome.
     * @param difference Difference value.
     * @param len        Length of the nucl. seq.
     * @throws GenomeException if input values are lesser than 0 or the chromosome name is invalid.
     */
    public LevenshteinComparisonResult(String chrom, String gene, int difference, int len) {
        this.chrom = chrom;
        this.gene = gene;

        // check the diff value
        if (difference < 0) {
            throw new GenomeException(this.getClass().getName(), "LevenshteinComparisonResult", "difference", "is lesser than 0");
        }
        this.difference = difference;

        // check the len value
        if (len < 0) {
            throw new GenomeException(this.getClass().getName(), "LevenshteinComparisonResult", "len", "is lesser than 0");
        }
        sequenceLen = len;
    }

    /**
     * @return The difference distance between two genomes.
     */
    public int getDifference() {
        return difference;
    }

    /**
     * @return The length of the compared sequences.
     */
    public int getSequenceLen() {
        return sequenceLen;
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
     * @return name of the gene that is stored in this region.
     */
    public String getGene() {
        return gene;
    }

    /**
     * Overridden method getResults() which return String with single gene intermediate results
     *
     * @return String with single gene intermediate results
     */
    public String getResults() {
        if (sequenceLen != 0)
            return "Comparison result of gene " + gene + " from chromosome " + chrom + ": seqLength - " + sequenceLen + ", differences - " + difference + ", similarity percentage - " + (100d - ((double) difference / (double) sequenceLen) * 100d) + "%";
        return "Nucleotide sequence consists of only UNKNOWN_NUCLEOTIDES";
    }

    /**
     * Defines, which comparator was used to get these results.
     *
     * @return type of the comparator.
     */
    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.LEVENSHTEIN;
    }
}
