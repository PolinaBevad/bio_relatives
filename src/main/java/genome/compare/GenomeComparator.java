package genome.compare;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines the class, that compares and analyses the genomes of two people,
 * using {@link} algorithm.
 *
 * @author Sergey Hvatov
 * @author Vladislav Marchenko
 */
public class GenomeComparator {
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
     * Compares two genomes. Calculates Hemming length.
     *
     * @return The percentage of the similarity between two genomes.
     * @throws GenomeException if sizes of strings from GenomeRegions are not equal.
     */
    public List<Pair<Integer, Integer>> HemmingDistance() throws GenomeException {
        // result
        ArrayList<Pair<Integer, Integer>> info = new ArrayList<>();

        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            String f = firstPersonGenome_.get(i).getNucleotideSequence();
            String s = secondPersonGenome_.get(i).getNucleotideSequence();

            int diff = 0, total = 0;

            // check the sizes because Hemming distance is calculated only
            // for equal strings
            if (s.length() != f.length()) {
                throw new GenomeException("HemmingDistance", "", "sizes of strings from GenomeRegions are not equal");
            }

            for (int j = 0; j < firstPersonGenome_.get(i).getNucleotideLength(); j++) {
                if (f.charAt(j) != s.charAt(j) && f.charAt(j) != getComplementNucleotide(s.charAt(j))) {
                    diff++;
                }
            }
            info.add(new Pair<>(diff, f.length()));
        }
        return info;
    }

    /**
     * Basic version of Needleman–Wunsch algorithm using a matrix [M * N].
     * It's algorithmic complexity is O(N * M).
     *
     * @return Array of pairs of number of different nucleotides
     * and total number of nucleotides in each region.
     */
    @Deprecated
    public List<Pair<Integer, Integer>> LevenshteinDistanceCanonical() {
        // result
        ArrayList<Pair<Integer, Integer>> info = new ArrayList<>();

        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            String f = firstPersonGenome_.get(i).getNucleotideSequence();
            String s = secondPersonGenome_.get(i).getNucleotideSequence();

            // table for the further usage
            int table[][] = new int[f.length() + 1][s.length() + 1];
            // fill the table
            for (int l = 0, k = 0; l < s.length() + 1 || k < f.length() + 1; l++, k++) {
                if (l < s.length()) table[0][l] = l;
                if (k < f.length()) table[k][0] = k;
            }

            for (int l = 1; l < f.length() + 1; l++) {
                for (int k = 1; k < s.length() + 1; k++) {
                    // calculate the minimum between
                    // table[l][k-1] + 1, table[l-1][k] + 1
                    // and table[l - 1][k - 1] + (f.charAt(l) == s.charAt(k)
                    table[l][k] = Math.min(Math.min(table[l][k - 1] + 1, table[l - 1][k] + 1), Math.min(table[l][k - 1] + 1, table[l - 1][k - 1] + (f.charAt(l - 1) == s.charAt(k - 1) ? 0 : 1)));
                }
            }
            info.add(new Pair<>(table[f.length()][s.length()], Math.min(f.length(), s.length())));
        }
        return info;
    }

    /**
     * Optimized version of Needleman–Wunsch algorithm using only two rows of the matrix.
     * It's algorithmic complexity is O(min(N, M)).
     *
     * @return Array of pairs of number of different nucleotides
     * and total number of nucleotides in each region.
     */
    public List<Pair<Integer, Integer>> LevenshteinDistance() {
        // result
        ArrayList<Pair<Integer, Integer>> info = new ArrayList<>();

        for (int i = 0; i < firstPersonGenome_.size(); i++) {
            String f = firstPersonGenome_.get(i).getNucleotideSequence();
            String s = secondPersonGenome_.get(i).getNucleotideSequence();

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
            info.add(new Pair<>(current[s.length()], Math.min(f.length(), s.length())));
        }
        return info;
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
