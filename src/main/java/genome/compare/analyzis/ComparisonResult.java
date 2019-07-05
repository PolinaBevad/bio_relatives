package genome.compare.analyzis;

import genome.compare.ComparatorType;

/**
 * Interface, which defines the interface of all
 * the classes, that are designed to store the results
 * of the comparison of the genomes.
 *
 * @author Sergey Khvatov
 */
public interface ComparisonResult {

    /**
     * Defines, which comparator was used to get these results.
     * @return type of the comparator.
     */
    ComparatorType getComparatorType();

    /**
     * Returns string representation of the results of the comparison
     * of two genome regions.
     *
     * @return string representation of the results of the comparison.
     */
    String getResults();
}
