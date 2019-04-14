package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines the class, that compares and analyses the genomes of two people,
 * using the Levenshtein's and Hemming's distances.
 *
 * @author Sergey Hvatov
 * @author Vladislav Marchenko
 */
public class GenomeComparator {

    /**
     * Enum, that defines which
     * distance will be used or was used
     * to calculate the distance value
     * between two genomes.
     */
    public enum DistanceType {
        HEMMING_DISTANCE,
        LEVENSHTEIN_DISTANCE
    }

    /**
     * Genome of the first person.
     */
    private List<GenomeRegion> firstPersonGenome_;

    /**
     * Genome of the second person.
     */
    private List<GenomeRegion> secondPersonGenome_;

    /**
     * Default class constructor from genomes of two people.
     *
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     */
    public GenomeComparator(List<GenomeRegion> first, List<GenomeRegion> second) throws GenomeException {
        if (first.size() != second.size()) {
            throw new GenomeException("GenomeComparator", "first, second", "sizes are not equal");
        }
        this.firstPersonGenome_ = first;
        this.secondPersonGenome_ = second;

        // sort genome regions
        this.firstPersonGenome_.sort(GenomeComparator::compareRegions);
        this.secondPersonGenome_.sort(GenomeComparator::compareRegions);
    }

    /**
     * This method calculates the distances for the compared genome regions.
     * The Hemming distance will be chosen if the
     * strings lengths are the same, and optimized Levenshtein otherwise.
     *
     * @return The list of {@link GenomeComparisonResult} that contain
     * the information about differences between all {@link GenomeRegion}.
     * @throws GenomeException if exception is thrown in sub methods.
     */
    public List<GenomeComparisonResult> calculateDistance() throws GenomeException {
        ArrayList<GenomeComparisonResult> result = new ArrayList<>();
        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            GenomeRegion f = firstPersonGenome_.get(i),
                s = secondPersonGenome_.get(i);
            if (f.getNucleotideLength() == s.getNucleotideLength()) {
                result.add(hDistance(f, s));
            } else {
                result.add(lDistance(f, s));
            }
        }
        return result;
    }

    /**
     * Compares two genomes. Calculates Hemming length.
     *
     * @return The list of {@link GenomeComparisonResult} that contain
     * the information about differences between all {@link GenomeRegion}.
     * @throws GenomeException if sizes of strings from GenomeRegions are not equal.
     */
    public List<GenomeComparisonResult> HemmingDistance() throws GenomeException {
        ArrayList<GenomeComparisonResult> result = new ArrayList<>();
        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            result.add(hDistance(firstPersonGenome_.get(i), secondPersonGenome_.get(i)));
        }
        return result;
    }

    /**
     * Optimized version of Needleman–Wunsch algorithm using only two rows of the matrix.
     * It's algorithmic complexity is O(min(N, M)).
     *
     * @return The list of {@link GenomeComparisonResult} that contain
     * the information about differences between all {@link GenomeRegion}.
     * @throws GenomeException if exception is thrown in {@link GenomeComparisonResult}.
     */
    public List<GenomeComparisonResult> LevenshteinDistance() throws GenomeException {
        ArrayList<GenomeComparisonResult> result = new ArrayList<>();
        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            result.add(lDistance(firstPersonGenome_.get(i), secondPersonGenome_.get(i)));
        }
        return result;
    }

    /**
     * Basic version of Needleman–Wunsch algorithm using a matrix [M * N].
     * It's algorithmic complexity is O(N * M). This method is used to
     * test the Needleman–Wunsch algorithm.
     *
     * @return The list of {@link GenomeComparisonResult} that contain
     * the information about differences between all {@link GenomeRegion}.
     * @throws GenomeException if exception is thrown in {@link GenomeComparisonResult}.
     */
    @Deprecated
    public List<Pair<GenomeComparisonResult, Integer[][]>> LevenshteinDistanceCanonical() throws GenomeException {
        ArrayList<Pair<GenomeComparisonResult, Integer[][]>> result = new ArrayList<>();
        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            String f = firstPersonGenome_.get(i).getNucleotideSequence();
            String s = secondPersonGenome_.get(i).getNucleotideSequence();

            // table for the further usage
            Integer table[][] = new Integer[f.length() + 1][s.length() + 1];

            // initialize table
            for (int j = 0; j < f.length() + 1; j++)
                Arrays.fill(table[j], 0);

            // fill the table
            for (int l = 0; l < f.length() + 1; l++) {
                table[l][0] = l;
            }
            for (int l = 0; l < s.length() + 1; l++) {
                table[0][l] = l;
            }

            for (int l = 1; l < f.length() + 1; l++) {
                for (int k = 1; k < s.length() + 1; k++) {
                    // calculate the minimum between
                    // table[l][k-1] + 1, table[l-1][k] + 1
                    // and table[l - 1][k - 1] + (f.charAt(l) == s.charAt(k)
                    table[l][k] = Math.min(Math.min(table[l][k - 1] + 1, table[l - 1][k] + 1), Math.min(table[l][k - 1] + 1, table[l - 1][k - 1] + (f.charAt(l - 1) == s.charAt(k - 1) ? 0 : 1)));
                }
            }
            result.add(new Pair<>(new GenomeComparisonResult(DistanceType.LEVENSHTEIN_DISTANCE, table[f.length()][s.length()], Math.max(f.length(), s.length())), table));
        }
        return result;
    }

    /**
     * Implementation of the method that calculates the Hemming distance
     * for the HemmingDistance() method.
     *
     * @param first  First genome region.
     * @param second Second genome region.
     * @return The result of the comparison using Hemming distance.
     * @throws GenomeException if sizes of the regions are not equal or if exception
     *                         is thrown in {@link GenomeComparisonResult}.
     */
    private GenomeComparisonResult hDistance(GenomeRegion first, GenomeRegion second) throws GenomeException {
        String f = first.getNucleotideSequence();
        String s = second.getNucleotideSequence();

        int diff = 0;
        // check the sizes because Hemming distance is calculated only
        // for equal strings
        if (s.length() != f.length()) {
            throw new GenomeException("HemmingDistance", "", "sizes of strings from GenomeRegions are not equal");
        }

        for (int j = 0; j < first.getNucleotideLength(); j++) {
            if (f.charAt(j) != s.charAt(j) && f.charAt(j) != getComplementNucleotide(s.charAt(j))) {
                diff++;
            }
        }
        return new GenomeComparisonResult(DistanceType.HEMMING_DISTANCE, diff, f.length());
    }

    /**
     * Implementation of the method that calculates the Levenshtein distance
     * for the LevenshteinDistance() method.
     *
     * @param first  First genome region.
     * @param second Second genome region.
     * @return The result of the comparison using Levenshtein distance.
     * @throws GenomeException if sizes of the regions are not equal or if exception
     *                         is thrown in {@link GenomeComparisonResult}.
     */
    private GenomeComparisonResult lDistance(GenomeRegion first, GenomeRegion second) throws GenomeException {
        String f = first.getNucleotideSequence();
        String s = second.getNucleotideSequence();

        // table for the further usage
        int table[] = new int[s.length() + 1];
        // fill the table
        for (int l = 0; l < s.length() + 1; l++) {
            table[l] = l;
        }

        int current[] = new int[s.length() + 1];
        for (int l = 1; l < f.length() + 1; l++) {
            current[0] = l;
            for (int k = 1; k < s.length() + 1; k++) {
                // calculate the minimum between
                // table[l][k-1] + 1, table[l-1][k] + 1
                // and table[l - 1][k - 1] + (f.charAt(l) == s.charAt(k)
                current[k] = Math.min(Math.min(current[k - 1] + 1, table[k] + 1), Math.min(table[k] + 1, table[k - 1] + (f.charAt(l - 1) == s.charAt(k - 1) ? 0 : 1)));
            }
            table = Arrays.copyOf(current, current.length);
        }
        return new GenomeComparisonResult(DistanceType.LEVENSHTEIN_DISTANCE, current[s.length()], Math.min(f.length(), s.length()));
    }

    /**
     * Get the complement nucleotide for this one method.
     *
     * @param nucleotide
     * @return Complement to this nucleotide.
     */
    private static char getComplementNucleotide(char nucleotide) {
        switch (nucleotide) {
            case 'a':
                return 't';
            case 't':
                return 'a';
            case 'g':
                return 'c';
            case 'c':
                return 'g';
            default:
                return '*';
        }
    }

    /**
     * Compares two genome regions for their further sort.
     *
     * @param lhs Left genome region.
     * @param rhs Right genome region.
     * @return value according to {@link Comparable} rules.
     */
    private static int compareRegions(GenomeRegion lhs, GenomeRegion rhs) {
        Integer lhsChrom = Integer.parseInt(lhs.getChromName().replaceAll("[^0-9]", ""));
        Integer rhsChrom = Integer.parseInt(rhs.getChromName().replaceAll("[^0-9]", ""));
        if (lhsChrom.compareTo(rhsChrom) != 0) {
            return lhsChrom.compareTo(rhsChrom);
        } else {
            return ((Integer) lhs.getStart()).compareTo(rhs.getStart());
        }
    }
}
