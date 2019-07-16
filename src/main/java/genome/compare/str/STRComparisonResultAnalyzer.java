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


import bam.marker_region.MarkerRegionFeature;
import bam.regular.BEDFeature;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.common.ComparisonResult;
import genome.compare.common.ComparisonResultAnalyzer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import util.Pair;
import util.STRResultGraph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
     * Map with position of each marker in the genome.
     */
    private Map<String, Pair<Integer, Integer>> markerPositions = new ConcurrentHashMap<>();

    /**
     * Count of markers, which repeats different times in each genome
     */
    private int countOfDiffMarkers = 0;

    /**
     * Name of the file where graph will be saved.
     */
    private String graphFileName;

    /**
     * Creates an instance of the result analyzer class.
     *
     * @param filename Path to the file, where resulting graph will be saved.
     */
    public STRComparisonResultAnalyzer(String filename, List<BEDFeature> markers) {
        this.graphFileName = filename;
        // init positions map
        for (BEDFeature marker : markers) {
            if (!(marker instanceof MarkerRegionFeature)) {
                throw new GenomeException(getClass().getName(), "STRComparisonResultAnalyzer", "incorrect feature type was used");
            }
            markerPositions.put(((MarkerRegionFeature) marker).getMarkerName(), new Pair<>(marker.getStartPos(), marker.getEndPos()));
        }
    }

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
     *
     * @return String representation of the result.
     */
    @Override
    public String analyze() {
        for (String markerName : markerComparisonResults.keySet()) {
            if (Math.abs(markerComparisonResults.get(markerName).getKey() - markerComparisonResults.get(markerName).getValue()) > EPS) {
                countOfDiffMarkers++;
            }
        }

        if (graphFileName != null) {
            createGraph();
        }

        return generateResultString();
    }

    /**
     * Method, which returns analyzing results in String format
     *
     * @return String, which contains result of analyzing
     */
    private String generateResultString() {
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
            result.append("These persons are not relatives.");
        } else {
            result.append("These persons are relatives.");
        }
        return result.toString();
    }

    /**
     * Creates the file with the visual representation of the result.
     */
    private void createGraph() {
        STRResultGraph graph = new STRResultGraph();
        for (String markerName : markerComparisonResults.keySet()) {
            Pair<Integer, Integer> result = markerComparisonResults.get(markerName);
            // throw exception if we are trying to process unknown marker
            if (!markerPositions.containsKey(markerName)) {
                throw new GenomeException(getClass().getName(), "createGraph", "unknown marker appeared");
            }
            graph.add(markerName, "first person", "second person", markerPositions.get(markerName), result);
        }
        graph.createGraphic(graphFileName);
    }
}
