/*
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

package util;

import exception.GenomeFileException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implements a basic interface to create
 * graphics with visual representation of the results
 * of the STR comparison algorithms.
 *
 * @author Sergey Khvatov
 */
public class STRResultGraph {

    /**
     * Stores information about value in the graphics
     * data set.
     */
    private static final class GraphValue {

        /**
         * Person identification strings
         * that are used as a rowKey.
         */
        String firstPersonId, secondPersonId;

        /**
         * Name of the marker.
         */
        String marker;

        /**
         * Position of the marker in the chromosome.
         */
        Pair<Integer, Integer> markerPosition;

        /**
         * Result of the comparisons.
         */
        Pair<Integer, Integer> comparisonResult;

        public GraphValue(String firstPersonId, String secondPersonId, String marker, Pair<Integer, Integer> pos, Pair<Integer, Integer> comparisonResult) {
            this.firstPersonId = firstPersonId;
            this.secondPersonId = secondPersonId;
            this.marker = marker;
            this.comparisonResult = comparisonResult;
            this.markerPosition = pos;
        }
    }

    /**
     * Comparator that is used to sort the values according to their position in the genome.
     */
    private static final Comparator<GraphValue> GRAPH_VALUE_COMPARATOR = (o1, o2) -> {
        int keyCmp = o1.markerPosition.getKey().compareTo(o2.markerPosition.getKey());
        if (keyCmp == 0) return o1.markerPosition.getValue().compareTo(o2.markerPosition.getValue());
        return keyCmp;
    };

    /**
     * Default extension of the graph file.
     */
    private static final String EXTENSION = ".png";

    /**
     * Default width of the graph file.
     */
    private static final int WIDTH = 720;

    /**
     * Default height of the graph file.
     */
    private static final int HEIGHT = 480;

    /**
     * Default number of markers in the graph file.
     */
    private static final int MARKERS_PER_GRAPH = 4;

    /**
     * List with values for the points on the graph.
     */
    private List<GraphValue> points = new ArrayList<>();

    /**
     * Adds new value to the list of points.
     *
     * @param marker Name of the marker.
     * @param fId    Id og the first person.
     * @param sId    Id of the second person.
     * @param pos    Position of the marker in the genome.
     * @param res    Result of teh comparison.
     */
    public void add(String marker, String fId, String sId, Pair<Integer, Integer> pos, Pair<Integer, Integer> res) {
        points.add(new GraphValue(fId, sId, marker, pos, res));
    }

    /**
     * Creates a graphic with the results of the comparison.
     *
     * @param path Path to the file.
     */
    public void createGraphic(String path) {
        try {
            // sort data
            points.sort(GRAPH_VALUE_COMPARATOR);
            // add data to the set
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            // generate paths
            String[] paths = generateFileNames(path, points.size());
            // generate tables
            int graphNumber = 1, fileNumber = 0;
            for (; graphNumber < points.size() + 1; graphNumber++) {
                GraphValue value = points.get(graphNumber - 1);
                String stringPos = value.marker + " " + value.markerPosition.getKey() + "-" + value.markerPosition.getValue();
                dataset.addValue(value.comparisonResult.getKey(), value.firstPersonId, stringPos);
                dataset.addValue(value.comparisonResult.getValue(), value.secondPersonId, stringPos);

                // if it is time to save
                if (graphNumber % MARKERS_PER_GRAPH == 0) {
                    save(dataset, paths[fileNumber++]);
                    dataset = new DefaultCategoryDataset();
                }
            }

            // save the last
            if (dataset.getColumnCount() != 0) {
                save(dataset, paths[fileNumber]);
            }
        } catch (IOException ioex) {
            throw new GenomeFileException(getClass().getName(), "createGraph", path, "incorrect input file");
        }
    }

    /**
     * Saves the dataset to the file.
     *
     * @param dataset Data set with the results.
     * @param path    Path to the file.
     * @throws IOException if error occurs while working with the file.
     */
    private void save(DefaultCategoryDataset dataset, String path) throws IOException {
        // create chart
        JFreeChart chart = ChartFactory.createBarChart("STR COMPARISON", "MARKER", "OCCURRENCE NUMBER", dataset, PlotOrientation.VERTICAL, true, true, false);
        // set it up
        setup(chart);
        // write it to the file
        ChartUtils.saveChartAsPNG(new File(path), chart, WIDTH, HEIGHT);
    }

    /**
     * Sets up the visual appearance of the chart.
     *
     * @param chart Chart to setup.
     */
    private void setup(JFreeChart chart) {
        // setup bar appearance
        BarRenderer br = (BarRenderer) chart.getCategoryPlot().getRenderer();
        br.setMaximumBarWidth(.35);
        br.setItemMargin(.25);
        br.setDrawBarOutline(true);

        // setup axis appearance
        CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
        domainAxis.setMaximumCategoryLabelLines(3);
        domainAxis.setLowerMargin(.1);
        domainAxis.setCategoryMargin(.2);
    }

    /**
     * Validates the path to the file, and if it is necessary then creates it.
     *
     * @param path Path to the file.
     * @throws IOException         if error occurs while working with the file.
     * @throws GenomeFileException if incorrect file path is passed.
     */
    private void validateFile(String path) throws IOException {
        // create new file
        File graphFile = new File(path);

        // create file if it doesn't exist
        if (!graphFile.exists()) {
            if (!graphFile.createNewFile()) {
                throw new GenomeFileException(getClass().getName(), "createGraph", path, "incorrect input file");
            }
        }

        // check file
        // if smth wrong, then throw exception
        if (graphFile.isDirectory() || !graphFile.canWrite() || !path.endsWith(EXTENSION)) {
            throw new GenomeFileException(getClass().getName(), "createGraph", path, "incorrect input file");
        }
    }

    /**
     * Generates filenames for the files with graphics.
     *
     * @param path         Path to the source file.
     * @param markerNumber Number of markers.
     * @return Array of paths.
     * @throws GenomeFileException if file is invalid
     * @throws IOException         if file doesn't pass the validation.
     */
    private String[] generateFileNames(String path, int markerNumber) throws IOException {
        validateFile(path);
        String[] paths = new String[(int) Math.ceil((double) markerNumber / MARKERS_PER_GRAPH)];
        String filename = path.split("\\.")[0];
        paths[0] = path;
        for (int i = 1; i < paths.length; i++) {
            paths[i] = filename + i + EXTENSION;
            validateFile(paths[i]);
        }
        return paths;
    }
}
