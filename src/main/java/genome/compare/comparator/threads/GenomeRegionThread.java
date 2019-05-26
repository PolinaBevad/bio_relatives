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

package genome.compare.comparator.threads;

import exception.GenomeException;
import exception.GenomeThreadException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;
import genome.compare.comparator.GenomeRegionComparator;

import java.util.List;

/**
 * This class represents a basic thread that will be created
 * to compare two genome regions and add the result of the comparison
 * to the resulting list.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegionThread implements Runnable {

    /**
     * Some genome region of the first person.
     */
    private final GenomeRegion firstGenomeRegion;

    /**
     * Some genome region of the second person.
     */
    private final GenomeRegion secondGenomeRegion;

    /**
     * Object of (@link GeneComparisonResultAnalyzer) which contains comparison results
     */
    private final GeneComparisonResultAnalyzer comparisonResults;

    /**
     * if this flag is true , then interim genome comparison results will be displayed,
     * else - only the main chromosome results will be obtained
     */
    private final boolean intermediateOutput;

    /**
     * Creates a {@link GenomeRegionThread} using the genome regions
     * of two people.
     *
     * @param first              Some genome region of the first person.
     * @param second             Some genome region of the second person.
     * @param results            Object of (@link GeneComparisonResultAnalyzer) which contains comparison results
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     */
    public GenomeRegionThread(GenomeRegion first, GenomeRegion second, GeneComparisonResultAnalyzer results, boolean intermediateOutput) {
        this.firstGenomeRegion = first;
        this.secondGenomeRegion = second;
        this.comparisonResults = results;
        this.intermediateOutput = intermediateOutput;
    }

    /**
     * run() method of the interface {@link Runnable} implementation.
     * Processes each genome region. Compares two genome regions and
     * adds the result ot the result table.
     */
    @Override
    public void run() {
        try {
            // generate new comparator
            GenomeRegionComparator comparator = new GenomeRegionComparator(this.firstGenomeRegion, this.secondGenomeRegion);
            // compare two genes
            GeneComparisonResult geneComparisonResult = comparator.LevenshteinDistance();
            // print intermediate results if user chose this feature
            if (intermediateOutput) {
                System.out.println(geneComparisonResult);
            }
            // add the results of the comparison to the Object of (@link GeneComparisonResultAnalyzer)with the results
            comparisonResults.add(geneComparisonResult);
        } catch (GenomeException gex) {
            throw new GenomeThreadException(gex.getMessage());
        }
    }
}
