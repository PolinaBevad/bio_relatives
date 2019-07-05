package genome.compare.analyzis;

import bam.marker_region.MarkerRegionFeature;
import genome.compare.ComparatorType;
import util.Pair;

/**
 * Represents the results of the comparison of the genomes
 * using Y_STR comparator.
 *
 * @author Sergey Khvatov
 */
public class YSTRComparisonResult implements ComparisonResult {

    /**
     * Name of the marker.
     */
    private MarkerRegionFeature marker;

    /**
     * Number of times it has occurred in the
     * first genome and in the second genome.
     */
    private Pair<Integer, Integer> result;

    /**
     * Creates an instance of the class from the name of the marker
     * and number of times marker motif has appeared in
     * first and second genomes.
     *
     * @param marker    Name of the marker.
     * @param firstNum  Number of times marker motif has appeared in
     *                  the first genome region.
     * @param secondNum Number of times marker motif has appeared in
     *                  the second genome region.
     */
    public YSTRComparisonResult(MarkerRegionFeature marker, int firstNum, int secondNum) {
        this.marker = marker;
        result = new Pair<>(firstNum, secondNum);
    }


    /**
     * Defines, which comparator was used to get these results.
     *
     * @return type of the comparator.
     */
    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.Y_STR;
    }

    /**
     * Returns the result of the comparison.
     *
     * @return the result of the comparison.
     */
    public Pair<Integer, Integer> getResult() {
        return result;
    }

    /**
     * Returns string representation of the results of the comparison
     * of two genome regions.
     *
     * @return string representation of the results of the comparison.
     */
    @Override
    public String getResults() {
        return "Result of the comparison: region [" + marker.getStartPos() + "; " + marker.getEndPos() + "]; Marker: " + marker.getMarkerName() + "; Number of appearance: " + result;
    }
}
