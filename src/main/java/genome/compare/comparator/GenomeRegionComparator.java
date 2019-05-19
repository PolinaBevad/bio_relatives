package genome.compare.comparator;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import util.Pair;

import java.util.Arrays;

/**
 * Defines the class, that compares and analyses the genomes of two people,
 * using the Levenshtein's and Hemming's distances.
 *
 * @author Sergey Hvatov
 * @author Vladislav Marchenko
 */
public class GenomeRegionComparator {

    /**
     * Unknown nucleotide symbol.
     */
    private static final char UNKNOWN_NUCLEOTIDE = '*';

    /**
     * Genome of the first person.
     */
    private GenomeRegion firstPersonGenome;

    /**
     * Genome of the second person.
     */
    private GenomeRegion secondPersonGenome;

    /**
     * Default class constructor from genomes of two people.
     *
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     */
    public GenomeRegionComparator(GenomeRegion first, GenomeRegion second) throws GenomeException {
        this.firstPersonGenome = first;
        this.secondPersonGenome = second;

        // validate the regions
        if (!validateRegions()) {
            throw new GenomeException(this.getClass().getName(), "GenomeRegionComparator", "first, second", "failed the validation");
        }
    }

    /**
     * Optimized version of Needleman–Wunsch algorithm using only two rows of the matrix.
     * It's algorithmic complexity is O(min(N, M)).
     *
     * @return information about differences between these two {@link GenomeRegion}.
     * @throws GenomeException if exception is thrown in {@link GeneComparisonResult}.
     */
    public GeneComparisonResult LevenshteinDistance() throws GenomeException {
        return lDistance(firstPersonGenome, secondPersonGenome);
    }

    /**
     * Compares two genomes. Calculates Hemming length.
     *
     * @return information about differences between these two {@link GenomeRegion}.
     * @throws GenomeException if sizes of strings from GenomeRegions are not equal.
     */
    public GeneComparisonResult HemmingDistance() throws GenomeException {
        return hDistance(firstPersonGenome, secondPersonGenome);
    }

    /**
     * Validate all input genome regions.
     *
     * @return False, if the sizes of the lists doesn't match
     * or after sorting it appears that some regions doesn't have
     * pair.
     */
    private boolean validateRegions() {
        return firstPersonGenome.equals(secondPersonGenome);
    }

    /**
     * Implementation of the method that calculates the Hemming distance
     * for the HemmingDistance() method.
     *
     * @param first  First genome region.
     * @param second Second genome region.
     * @return The result of the comparison using Hemming distance.
     * @throws GenomeException if sizes of the regions are not equal or if exception
     *                         is thrown in {@link GeneComparisonResult}.
     */
    private GeneComparisonResult hDistance(GenomeRegion first, GenomeRegion second) throws GenomeException {
        // normalize the input nucleotide sequences
        Pair<String, String> temp = getNormalizedAlignments(first.getNucleotideSequence(), second.getNucleotideSequence());
        // save new genome sequences
        String f = temp.getKey(), s = temp.getValue();

        int diff = 0;
        // check the sizes because Hemming distance is calculated only
        // for equal strings
        if (s.length() != f.length()) {
            throw new GenomeException(this.getClass().getName(), "HemmingDistance", "sizes of strings from GenomeRegions are not equal");
        }

        for (int j = 0; j < first.getNucleotideLength(); j++) {
            if (f.charAt(j) != s.charAt(j) && f.charAt(j) != getComplementNucleotide(s.charAt(j))) {
                diff++;
            }
        }
        return new GeneComparisonResult(first.getChromName(), first.getGene(), diff, f.length());
    }

    /**
     * Implementation of the method that calculates the Levenshtein distance
     * for the LevenshteinDistance() method.
     *
     * @param first  First genome region.
     * @param second Second genome region.
     * @return The result of the comparison using Levenshtein distance. Compares two nucleotides,
     * if they are both known, otherwise deletes it from the nucleotide sequence.
     * @throws GenomeException if sizes of the regions are not equal or if exception
     *                         is thrown in {@link GeneComparisonResult}.
     */
    private GeneComparisonResult lDistance(GenomeRegion first, GenomeRegion second) throws GenomeException {
        // normalize the input nucleotide sequences
        Pair<String, String> temp = getNormalizedAlignments(first.getNucleotideSequence(), second.getNucleotideSequence());
        // save new genome sequences
        String f = temp.getKey(), s = temp.getValue();

        // table for the further usage
        int[] table = new int[s.length() + 1];
        // fill the table
        for (int l = 0; l < s.length() + 1; l++) {
            table[l] = l;
        }

        int[] current = new int[s.length() + 1];
        for (int l = 1; l < f.length() + 1; l++) {
            current[0] = l;
            for (int k = 1; k < s.length() + 1; k++) {
                // calculate the minimum between
                // table[l][k-1] + 1, table[l-1][k] + 1
                // and table[l - 1][k - 1] + (f.charAt(l) == s.charAt(k)
                current[k] = Math.min(Math.min(current[k - 1] + 1, table[k] + 1), Math.min(table[k] + 1, table[k - 1] + ((f.charAt(l - 1) == s.charAt(k - 1)) ? 0 : 1)));
            }
            table = Arrays.copyOf(current, current.length);
        }
        /*
         return difference between these two regions
         without considering unknown nucleotides.
         */
        // also, after validation we consider that the
        // start positions and the names of chromosomes and genes in these two regions
        // are the same.
        return new GeneComparisonResult(first.getChromName(), first.getGene(), current[s.length()], Math.max(f.length(), s.length()));
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
     * Check if nucleotide wasn't processed by sequencer.
     *
     * @param nucleotide Nucleotide to check
     * @return True, if nucleotide wasn't processed by sequencer, false otherwise.
     */
    private static boolean isUnknownNucleotide(char nucleotide) {
        return nucleotide == UNKNOWN_NUCLEOTIDE;
    }

    /**
     * Deletes all the unread nucleotides from the nucleotide sequences.
     *
     * @param first  First nucleotide sequence.
     * @param second Second nucleotide sequence.
     * @return Pair of two strings, that are created by deleting nucleotides
     * from both nucleotide sequence if at least one of them is UNKNOWN_NUCLEOTIDE.
     */
    private static Pair<String, String> getNormalizedAlignments(String first, String second) {
        // delete unknown nucleotides from both genome strings
        StringBuilder firstGenome = new StringBuilder();
        StringBuilder secondGenome = new StringBuilder();
        for (int i = 0; i < Math.min(first.length(), second.length()); i++) {
            // add nucleotide to the result sequence if
            // both nucleotides were correctly processed by the
            // sequencer
            if (!isUnknownNucleotide(first.charAt(i)) && !isUnknownNucleotide(second.charAt(i))) {
                firstGenome.append(first.charAt(i));
                secondGenome.append(second.charAt(i));
            }
        }

        // for the longest sequence check the ending
        if (firstGenome.length() < secondGenome.length()) {
            for (int i = first.length(); i < second.length(); i++) {
                if (!isUnknownNucleotide(second.charAt(i))) {
                    secondGenome.append(i);
                }
            }
        } else {
            for (int i = second.length(); i < first.length(); i++) {
                if (!isUnknownNucleotide(first.charAt(i))) {
                    firstGenome.append(i);
                }
            }
        }
        return new Pair<>(firstGenome.toString(), secondGenome.toString());
    }
}
