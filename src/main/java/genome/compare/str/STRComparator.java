/*
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

package genome.compare.str;

import bam.marker_region.MarkerRegionFeature;
import bam.regular.BEDFeature;
import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.common.GenomeComparator;

import java.util.regex.Matcher;

/**
 * Implements Y_STR algorithm used to compare father and son
 * genomes according to marker regions from their Y chromosomes.
 *
 * @author Sergey Khvatov
 */
public class STRComparator extends GenomeComparator {

    /**
     * Information about marker.
     */
    private MarkerRegionFeature feature;


    /**
     * Creates an instance of this comparator from two genome regions
     * from different people and
     *
     * @param marker Marker region feature.
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     * @throws GenomeException if regions fail validation or if marker is not an instance of {@link MarkerRegionFeature}.
     */
    public STRComparator(BEDFeature marker, GenomeRegion first, GenomeRegion second) {
        super(first, second);
        if (!(marker instanceof MarkerRegionFeature)) {
            throw new GenomeException(getClass().getName(), getClass().getName(), "expected marker region feature.");
        }
        this.feature = (MarkerRegionFeature) marker;
    }

    /**
     * Compares genomes and returns the result of the comparison.
     * Returns Object, because the results of the comparison
     * may vary because of the used method.
     *
     * @return Results of the comparison of two genome regions.
     */
    @Override
    public STRComparisonResult compare() {
        Matcher firstMatcher = feature.getRepeatMotif().matcher(first.getNucleotideSequence());
        int firstNum = 0;
        while (firstMatcher.find()) {
            firstNum++;
        }

        Matcher secondMatcher = feature.getRepeatMotif().matcher(second.getNucleotideSequence());
        int secondNum = 0;
        while (secondMatcher.find()) {
            secondNum++;
        }

        return new STRComparisonResult(feature, firstNum, secondNum);
    }
}
