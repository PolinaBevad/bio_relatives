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
import exception.GenomeFileException;
import genome.compare.comparator.executors.GenomeComparator;
import genome.compare.comparator.threads.GenomeComparatorThread;

/**
 * Class for running of genome comparing of two or three persons
 *
 * @author Vladislav Marchenko
 */
public class TrioComparator {

    /**
     * Static method, which run a comparing of genomes of two persons.
     *
     * @param BAMFileName1       name of the BAM file which contains genome of the 1st person
     * @param BAMFileName2       name of the BAM file which contains genome of the 2nd person
     * @param BEDFileName        name of the BED file
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @return String with results of comparing of two genomes
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException     if some errors occurred through the work of code
     */
    public static String compareTwoGenomes(String BAMFileName1, String BAMFileName2, String BEDFileName, boolean intermediateOutput) throws
        GenomeFileException, GenomeException {
        GenomeComparator comparator = new GenomeComparator(BAMFileName1, BAMFileName2, BEDFileName);
        return comparator.compareGenomes(intermediateOutput).toString();
    }

    /**
     * Static method , which run a comparing of genomes of three persons: son with father and son with mother
     *
     * @param fatherBAMFileName  name of the BAM file which contains genome of the father
     * @param motherBAMFileName  name of the BAM file which contains genome of the mother
     * @param sonBAMFileName     name of the BAM file which contains genome of the son
     * @param BEDFileName        name of the BED file
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @return String with results of genomes comparing of three persons: son with father and son with mother
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException     if some errors occurred through the work of code
     */
    public static String compareThreeGenomes(String fatherBAMFileName, String motherBAMFileName, String sonBAMFileName, String BEDFileName, boolean intermediateOutput) throws
        GenomeFileException, GenomeException {
        GenomeComparator comparator1 = new GenomeComparator(sonBAMFileName, fatherBAMFileName, BEDFileName);
        StringBuilder result = new StringBuilder("Comparison of father and son genomes:\n");
        result.append(comparator1.compareGenomes(intermediateOutput));

        GenomeComparator comparator2 = new GenomeComparator(sonBAMFileName, motherBAMFileName, BEDFileName);
        result.append("\nComparison of mother and son genomes:\n");
        result.append(comparator2.compareGenomes(intermediateOutput));

        return result.toString();
    }
}
