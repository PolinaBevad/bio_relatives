/**
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

package genome.compare.comparator;

import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.ComparatorType;
import genome.compare.analyzis.LevenshteinComparisonResult;
import util.Pair;

import java.util.Arrays;

/**
 * Implements algorithm for Levenshtein distance calculation
 * to compare genomes.
 *
 * @author Sergey Khvatov
 */
public class LevenshteinComparator extends GenomeComparator {
    /**
     * Unknown nucleotide symbol.
     */
    private static final char UNKNOWN_NUCLEOTIDE = '*';

    /**
     * Default class constructor from genomes of two people.
     *
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     */
    public LevenshteinComparator(GenomeRegion first, GenomeRegion second) throws GenomeException {
        super(first, second);
    }

    /**
     * Implementation of the method that calculates the Levenshtein distance
     * for the LevenshteinDistance() method.
     *
     * @return The result of the comparison using Levenshtein distance. Compares two nucleotides,
     * if they are both known, otherwise deletes it from the nucleotide sequence.
     * @throws GenomeException if sizes of the regions are not equal or if exception
     *                         is thrown in {@link LevenshteinComparisonResult}.
     */
    public LevenshteinComparisonResult compare() throws GenomeException{
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
        return new LevenshteinComparisonResult(first.getChromName(), first.getGene(), current[s.length()], Math.max(f.length(), s.length()));
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
