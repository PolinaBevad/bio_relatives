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

package genome.compare.comparator.executors;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.comparator.GenomeRegionComparator;

import java.util.concurrent.Callable;

/**
 * {@link GenomeRegionCallable} class implements a {@link Callable} interface.
 * Overrides the call() method, so that it compares two genomes and returns
 * the result of the comparison as an {@link GeneComparisonResult} object.
 *
 * @author Sergey Khvatov
 */
@Deprecated
public class GenomeRegionCallable implements Callable<GeneComparisonResult> {

    /**
     * Some genome region of the first person.
     */
    private GenomeRegion firstGenomeRegion;

    /**
     * Some genome region of the second person.
     */
    private GenomeRegion secondGenomeRegion;

    /**
     * Defines whether the some additional information
     * about the results of the comparison of two genome
     * regions will be printed.
     */
    private boolean additionalOutput;

    /**
     * Creates an object of {@link GenomeRegionCallable} class from the references to
     * the genome regions of two persons that must be compared and a flag, that determines,
     * whether additional output is necessary or not.
     *
     * @param firstRegion      First persons genome region.
     * @param secondRegion     Second persons genome region.
     * @param additionalOutput Flag for additional output.
     */
    public GenomeRegionCallable(GenomeRegion firstRegion, GenomeRegion secondRegion, boolean additionalOutput) {
        this.firstGenomeRegion = firstRegion;
        this.secondGenomeRegion = secondRegion;
        this.additionalOutput = additionalOutput;
    }

    /**
     * {@link Callable} interface method call() override.
     * Compares two genome regions, prints additional info
     * if it is necessary.
     *
     * @return Results of the comparison of two regions.
     * @throws GenomeException if regions don't pass the validation.
     */
    @Override
    public GeneComparisonResult call() throws GenomeException {
        // generate new comparator
        GenomeRegionComparator comparator = new GenomeRegionComparator(this.firstGenomeRegion, this.secondGenomeRegion);
        // compare two genes
        GeneComparisonResult geneComparisonResult = comparator.LevenshteinDistance();
        // print additional output if it is necessary
        if (additionalOutput) {
            System.out.println(geneComparisonResult);
        }
        return geneComparisonResult;
    }
}
