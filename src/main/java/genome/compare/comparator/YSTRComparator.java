package genome.compare.comparator;

import bam.marker_region.MarkerRegionFeature;
import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.YSTRComparisonResult;

import java.util.regex.Matcher;

/**
 * Implements Y_STR algorithm used to compare father and son
 * genomes according to marker regions from their Y chromosomes.
 *
 * @author Sergey Khvatov
 */
public class YSTRComparator extends GenomeComparator {

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
     * @throws GenomeException if regions fail validation.
     */
    public YSTRComparator(MarkerRegionFeature marker, GenomeRegion first, GenomeRegion second) throws GenomeException {
        super(first, second);
        this.feature = marker;
    }

    /**
     * Compares genomes and returns the result of the comparison.
     * Returns Object, because the results of the comparison
     * may vary because of the used method.
     *
     * @return Results of the comparison of two genome regions.
     */
    @Override
    public YSTRComparisonResult compare() {
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

        return new YSTRComparisonResult(feature, firstNum, secondNum);
    }
}
