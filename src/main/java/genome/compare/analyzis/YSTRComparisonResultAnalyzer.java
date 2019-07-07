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


import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class analyze results of gene comparison of two persons(for Y-STR comparator type)
 * @author Vladislav Marchenko
 */
public class YSTRComparisonResultAnalyzer implements ComparisonResultAnalyzer{
    /**
     * The results of the comparison of the two genomes for marker regions as follows:
     * key of Map - name of marker region, value of Map - Pair
     * key of Pair - number of times marker motif has appeared in the first genome region,
     * value of Pair - number of times marker motif has appeared in the second genome region.
     */
    private Map<String, Pair<Integer, Integer>> markerComparisonResults = new ConcurrentHashMap<>();

    /**
     * A list of marker regions, which contains in the first genome:
     * key of Pair - name of the marker, value of Pair - number of times marker motif has appeared in the genome
     */
    private List<Pair<String, Integer>> fstPersonMarkers = new ArrayList<>();

    /**
     * A list of marker regions, which contains in the second genome:
     * key of Pair - name of the marker, value of Pair - number of times marker motif has appeared in the genome
     */
    private List<Pair<String, Integer>> scdPersonMarkers = new ArrayList<>();

    /**
     * Method for adding one gene comparison result for storage and analyzing.
     * @param comparisonResult one of the gene comparison result
     */
    @Override
    public void add(ComparisonResult comparisonResult) {
        YSTRComparisonResult ystrComparisonResult =  (YSTRComparisonResult) comparisonResult;
        if (markerComparisonResults.containsKey(ystrComparisonResult.getMarker().getMarkerName())) {
            markerComparisonResults.get(ystrComparisonResult.getMarker().getMarkerName()).setKey(
                    markerComparisonResults.get(ystrComparisonResult.getMarker().getMarkerName()).getKey() +
                            ystrComparisonResult.getResult().getKey()
            );
            markerComparisonResults.get(ystrComparisonResult.getMarker().getMarkerName()).setValue(
                    markerComparisonResults.get(ystrComparisonResult.getMarker().getMarkerName()).getValue() +
                            ystrComparisonResult.getResult().getValue()
            );
        } else {
            markerComparisonResults.put(ystrComparisonResult.getMarker().getMarkerName(), ystrComparisonResult.getResult());
        }
    }

    /**
     * Method for analyzing of comparison results.
     */
    @Override
    public void analyze() {
        for (String markerName : markerComparisonResults.keySet()) {
            switch (markerComparisonResults.get(markerName).getKey().compareTo(markerComparisonResults.get(markerName).getValue())) {
                case -1 :
                    scdPersonMarkers.add(new Pair<>(markerName, markerComparisonResults.get(markerName).getValue()));
                    break;
                case 0 :
                    scdPersonMarkers.add(new Pair<>(markerName, markerComparisonResults.get(markerName).getValue()));
                    fstPersonMarkers.add(new Pair<>(markerName, markerComparisonResults.get(markerName).getKey()));
                    break;
                case 1:
                    fstPersonMarkers.add(new Pair<>(markerName, markerComparisonResults.get(markerName).getKey()));
                    break;
            }
        }
    }

    /**
     * Method, which returns analyzing results in String format
     * @return String, which contains result of analyzing
     */
    @Override
    public String getResultString() {
        StringBuilder result = new StringBuilder("Comparison results of marker regions:\n\tMarker regions of the first person:\n");
        result.append(getPersonalResultString(fstPersonMarkers));
        result.append("\tMarker regions of the second person:\n");
        result.append(getPersonalResultString(scdPersonMarkers));
        if (fstPersonMarkers.size() == scdPersonMarkers.size()) {
            result.append("These persons are son and father");
        }
        else {
            result.append("These persons are not father and son");
        }
        return result.toString();
    }

    /**
     * Method , which returns string, which contains analyzing results for one person
     * @param personMarkers A list of marker regions, which contains in the genome of one person
     * @return string, which contains analyzing results for one person
     */
    private String getPersonalResultString(List<Pair<String, Integer>> personMarkers) {
        StringBuilder result = new StringBuilder();
        for (Pair<String, Integer> marker : personMarkers) {
            result.append("\t\tMarker region- ");
            result.append(marker.getKey());
            result.append(", number of times in the genome- ");
            result.append(marker.getValue());
            result.append(";\n");
        }
        result.append("\tTotal number of markers in the genome- ");
        result.append(personMarkers.size());
        result.append(";\n");
        return result.toString();
    }
}
