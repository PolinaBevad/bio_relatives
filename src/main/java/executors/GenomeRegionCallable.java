/**
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package executors;

import exception.GenomeException;
import genome.compare.analyzis.ComparisonResult;
import genome.compare.analyzis.LevenshteinComparisonResult;
import genome.compare.comparator.GenomeComparator;
import genome.compare.comparator.LevenshteinComparator;

import java.util.concurrent.Callable;

/**
 * {@link GenomeRegionCallable} class implements a {@link Callable} interface.
 * Overrides the call() method, so that it compares two genomes and returns
 * the result of the comparison as an {@link LevenshteinComparisonResult} object.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegionCallable implements Callable<ComparisonResult> {

    /**
     * Comparator object.
     */
    private GenomeComparator comparator;

    /**
     * Defines whether the some additional information
     * about the results of the comparison of two genome
     * regions will be printed.
     */
    private boolean additionalOutput;

    /**
     * Creates an object of {@link GenomeRegionCallable} class from comparator
     * object and a flag, that determines, whether additional output is necessary or not.
     *
     * @param comparator      Comparator object.
     * @param additionalOutput Flag for additional output.
     */
    public GenomeRegionCallable(GenomeComparator comparator, boolean additionalOutput) {
        this.comparator = comparator;
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
    public ComparisonResult call() throws GenomeException {
        // compare two genes
        ComparisonResult geneComparisonResult = comparator.compare();
        // print additional output if it is necessary
        if (additionalOutput) {
            System.out.println(geneComparisonResult.getResults());
        }
        return geneComparisonResult;
    }
}
