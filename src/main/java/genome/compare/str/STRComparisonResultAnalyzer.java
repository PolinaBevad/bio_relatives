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

package genome.compare.str;


import exception.GenomeException;
import genome.compare.common.ComparisonResult;
import genome.compare.common.ComparisonResultAnalyzer;
import util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class analyze results of gene comparison of two persons(for Y-STR comparator type)
 *
 * @author Vladislav Marchenko
 */
public class STRComparisonResultAnalyzer implements ComparisonResultAnalyzer {
    /**
     * Maximal difference between number of repeats of marker motif in each genome
     */
    private final static int EPS = 1;
    /**
     * The results of the comparison of the two genomes for marker regions as follows:
     * key of Map - name of marker region, value of Map - Pair
     * key of Pair - number of times marker motif has appeared in the first genome region,
     * value of Pair - number of times marker motif has appeared in the second genome region.
     */
    private Map<String, Pair<Integer, Integer>> markerComparisonResults = new ConcurrentHashMap<>();
    /**
     * Count of markers, which repeats different times in each genome
     */
    private int countOfDiffMarkers = 0;

    /**
     * Method for adding one gene comparison result for storage and analyzing.
     *
     * @param comparisonResult one of the gene comparison result
     * @throws GenomeException if geneComparisonResult is not an instance of {@link STRComparisonResult}.
     */
    @Override
    public void add(ComparisonResult comparisonResult) {
        // check the type
        if (!(comparisonResult instanceof STRComparisonResult)) {
            throw new GenomeException(this.getClass().getName(), "add", "comparison result variable has incorrect type: " + comparisonResult.getClass());
        }

        // add results
        STRComparisonResult STRComparisonResult = (STRComparisonResult) comparisonResult;
        if (markerComparisonResults.containsKey(STRComparisonResult.getMarker().getMarkerName())) {
            markerComparisonResults.get(STRComparisonResult.getMarker().getMarkerName()).setKey(markerComparisonResults.get(STRComparisonResult.getMarker().getMarkerName()).getKey() + STRComparisonResult.getResult().getKey());
            markerComparisonResults.get(STRComparisonResult.getMarker().getMarkerName()).setValue(markerComparisonResults.get(STRComparisonResult.getMarker().getMarkerName()).getValue() + STRComparisonResult.getResult().getValue());
        } else {
            markerComparisonResults.put(STRComparisonResult.getMarker().getMarkerName(), STRComparisonResult.getResult());
        }
    }

    /**
     * Method for analyzing of comparison results.
     */
    @Override
    public void analyze() {
        for (String markerName : markerComparisonResults.keySet()) {
            if (Math.abs(markerComparisonResults.get(markerName).getKey() - markerComparisonResults.get(markerName).getValue()) > EPS) {
                countOfDiffMarkers++;
            }
        }
    }

    /**
     * Method, which returns analyzing results in String format
     *
     * @return String, which contains result of analyzing
     */
    @Override
    public String getResultString() {
        StringBuilder result = new StringBuilder("Comparison results of marker regions:\n");
        for (String markerName : markerComparisonResults.keySet()) {
            result.append("\tName of marker - ");
            result.append(markerName);
            result.append(", which has appeared in the both genomes, as: ");
            result.append(markerComparisonResults.get(markerName).getKey());
            result.append(" and ");
            result.append(markerComparisonResults.get(markerName).getValue());
            result.append(" times;\n");
        }
        result.append("Total number of markers with different repeating number(more, than EPS = ");
        result.append(EPS);
        result.append(") in each genome - ");
        result.append(countOfDiffMarkers);
        result.append(";\n");
        if (countOfDiffMarkers == 0) {
            result.append("These persons are son and father.");
        } else {
            result.append("These persons are not father and son.");
        }
        return result.toString();
    }
}
