package util;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GenomeRegionComparisonResult;

import java.util.Arrays;


/**
 * This class is designed to store some implemented static methods,
 * that may bes useful in the nearest future.
 *
 * @author Sergey Khvatov
 */
public class Utilities {

    /**
     * Basic version of Needleman–Wunsch algorithm using a matrix [M * N].
     * It's algorithmic complexity is O(N * M). This method is used to
     * test the Needleman–Wunsch algorithm.
     *
     * @return The list of {@link GenomeRegionComparisonResult} that contain
     * the information about differences between all {@link GenomeRegion}.
     * @throws GenomeException if exception is thrown in {@link GenomeRegionComparisonResult}.
     */
    public static Pair<GenomeRegionComparisonResult, Integer[][]> LevenshteinDistanceCanonical(GenomeRegion firstPersonGenome, GenomeRegion secondPersonGenome) throws
        GenomeException {
        String f = firstPersonGenome.getNucleotideSequence();
        String s = secondPersonGenome.getNucleotideSequence();

        // table for the further usage
        Integer[][] table = new Integer[f.length() + 1][s.length() + 1];

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
        return new Pair<>(new GenomeRegionComparisonResult(firstPersonGenome.getChromName(), firstPersonGenome.getGene(), firstPersonGenome.getStart(), table[f.length()][s.length()], Math.min(f.length(), s.length())), table);
    }

    /**
     * Recover two genomes alignment relative to each other using the
     * results of LevenshteinDistanceCanonical method.
     *
     * @param firstGenome  first person's genome.
     * @param secondGenome second person's genome.
     * @return Two strings, that contains the alignments of the nucleotide sequences.
     * @throws GenomeException
     */
    public static Pair<String, String> genomeRecovery(GenomeRegion firstGenome, GenomeRegion secondGenome) throws GenomeException {
        Integer table[][] = LevenshteinDistanceCanonical(firstGenome, secondGenome).getValue();

        StringBuilder alignmentA = new StringBuilder();
        StringBuilder alignmentB = new StringBuilder();

        String firstNuclSeq = firstGenome.getNucleotideSequence(), secondNuclSeq = secondGenome.getNucleotideSequence();

        int i = firstNuclSeq.length(), j = secondNuclSeq.length();

        while (i > 0 && j > 0) {
            int score = table[i][j];
            int diag = table[i - 1][j - 1];
            int up = table[i][j - 1];
            int left = table[i - 1][j];

            if (score == diag + (firstNuclSeq.charAt(i - 1) == secondNuclSeq.charAt(j - 1) ? 0 : 1)) {
                alignmentA.append(firstNuclSeq.charAt(i - 1));
                alignmentB.append(secondNuclSeq.charAt(j - 1));
                i -= 1;
                j -= 1;
            } else if (score == left + 1) {
                alignmentA.append(firstNuclSeq.charAt(i - 1));
                alignmentB.append('-');
                i -= 1;
            } else if (score == up + 1) {
                alignmentB.append(secondNuclSeq.charAt(j - 1));
                alignmentA.append('-');
                j -= 1;
            }
        }

        while (i > 0) {
            alignmentA.append(firstNuclSeq.charAt(i - 1));
            alignmentB.append('-');
            i -= 1;
        }

        while (j > 0) {
            alignmentB.append(secondNuclSeq.charAt(j - 1));
            alignmentA.append('-');
            j -= 1;
        }

        return new Pair<>(alignmentA.toString(), alignmentB.toString());
    }
}
