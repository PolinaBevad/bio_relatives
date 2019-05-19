package genome.compare.comparator.threads;

import exception.GenomeException;
import exception.GenomeThreadException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GenomeRegionComparisonResult;
import genome.compare.comparator.GenomeRegionComparator;

import java.util.List;

/**
 * This class represents a basic thread that will be created
 * to compare two genome regions and add the result of the comparison
 * to the resulting list.
 *
 * @author Sergey Khvatov
 */
public class GenomeRegionThread implements Runnable {

    /**
     * Some genome region of the first person.
     */
    private final GenomeRegion firstGenomeRegion;

    /**
     * Some genome region of the second person.
     */
    private final GenomeRegion secondGenomeRegion;

    /**
     * Synchronized list with the results of the comparison of collocated
     * on the same positions regions.
     */
    private final List<GenomeRegionComparisonResult> comparisonResults;

    /**
     * Creates a {@link GenomeRegionThread} using the genome regions
     * of two people.
     *
     * @param first  Some genome region of the first person.
     * @param second Some genome region of the second person.
     */
    public GenomeRegionThread(GenomeRegion first, GenomeRegion second, List<GenomeRegionComparisonResult> results) {
        this.firstGenomeRegion = first;
        this.secondGenomeRegion = second;
        this.comparisonResults = results;
    }

    /**
     * run() method of the interface {@link Runnable} implementation.
     * Processes each genome region. Compares two genome regions and
     * adds the result ot the result table.
     */
    @Override
    public void run() {
        try {
            // generate new comparator
            GenomeRegionComparator comparator = new GenomeRegionComparator(this.firstGenomeRegion, this.secondGenomeRegion);
            // add the results of the comparison to the sync. list with the results
            comparisonResults.add(comparator.LevenshteinDistance());
        } catch (GenomeException gex) {
            throw new GenomeThreadException(gex.getMessage());
        }
    }
}
