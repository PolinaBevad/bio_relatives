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

package bam.regular;

import exception.GenomeException;
import exception.GenomeFileException;

import java.util.HashSet;

/**
 * BED file record class.
 *
 * @author Sergey Khvatov
 */
public class BEDFeature {

    /**
     * Regular expression that contains all
     * the allowed symbols for the names used in the records.
     */
    public static final String ALLOWED_SYMBOLS_REGEXP = "[a-zA-Z0-9.\\-_+]*";

    /**
     * Name of the chromosome.
     */
    private String chrom;

    /**
     * Start position of the feature.
     */
    private int start;

    /**
     * End position of the feature.
     */
    private int end;

    /**
     * Name of the gene that is stored in this record.
     */
    private String gene;

    /**
     * Default class constructor from chromosome name, start and positions of the feature.
     *
     * @param chrom Name of the chromosome.
     * @param start Start position.
     * @param end   End position.
     * @param gene  Name of the gene.
     * @throws GenomeFileException if start or end positions are incorrect.
     */
    public BEDFeature(String chrom, int start, int end, String gene) throws GenomeFileException {
        this.chrom = chrom;
        if (start < 0 || end < 0 || start >= end) {
            throw new GenomeFileException("Error occurred during initialization of BEDFeature object: " + "Incorrect parameters were passed: [" + chrom + ", " + start + ", " + end + ", " + gene);
        }

        this.gene = gene;
        if (!gene.matches(ALLOWED_SYMBOLS_REGEXP)) {
            throw new GenomeFileException("Error occurred during initialization of BEDFeature object: " + "Incorrect parameters were passed: [" + chrom + ", " + start + ", " + end + ", " + gene);
        }

        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "gene: [" + gene + "]; chrom: [" + chrom + "]; (start; end): (" + start + "; " + end + ");\n";
    }

    /**
     * Get the nam of the chromosome method.
     *
     * @return Name of the chromosome.
     */
    public String getChromosomeName() {
        return chrom;
    }

    /**
     * Get the start position method.
     *
     * @return Start position of the feature in the chromosome.
     */
    public int getStartPos() {
        return start;
    }

    /**
     * Get the end position method.
     *
     * @return End position of the feature in the chromosome.
     */
    public int getEndPos() {
        return end;
    }

    /**
     * @return name of the gene in this record.
     */
    public String getGene() {
        return gene;
    }
}