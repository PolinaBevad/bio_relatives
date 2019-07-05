package genome.compare.comparator;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.ComparisonResult;

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
