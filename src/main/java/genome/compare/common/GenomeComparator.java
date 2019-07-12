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

package genome.compare.common;

import exception.GenomeException;
import genome.assembly.GenomeRegion;

/**
 * Interface, which defines the interface of all
 * the classes, that are designed to compare genomes.
 *
 * @author Sergey Khvatov
 */
public abstract class GenomeComparator {

    /**
     * Genome of the first person.
     */
    protected GenomeRegion first;

    /**
     * Genome of the second person.
     */
    protected GenomeRegion second;

    /**
     * Default class constructor from genomes of two people.
     *
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     * @throws GenomeException if genomes fail validation.
     */
    protected GenomeComparator(GenomeRegion first, GenomeRegion second) {
        this.first = first;
        this.second = second;

        // validate the regions
        if (!validateRegions()) {
            throw new GenomeException(this.getClass().getName(), "LevenshteinComparator", "first, second", "failed the validation");
        }
    }

    /**
     * Compares genomes and returns the result of the comparison.
     * Returns Object, because the results of the comparison
     * may vary because of the used method.
     *
     * @return Results of the comparison of two genome regions.
     * @throws GenomeException if genomes fail validation.
     */
    public abstract ComparisonResult compare();

    /**
     * Validate all input genome regions.
     *
     * @return False, if the sizes of the lists doesn't match
     * or after sorting it appears that some regions doesn't have
     * pair.
     */
    private boolean validateRegions() {
        return first.equals(second);
    }
}
