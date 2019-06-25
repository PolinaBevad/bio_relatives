/**
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import genome.compare.analyzis.GeneComparisonResultAnalyzer;
import genome.compare.comparator.executors.GenomeComparatorExecutor;
import util.Pair;

import java.util.List;

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
     * @param threadsNum         Number of threads that will be used to process exons.
     * @return String with results of comparing of two genomes
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException     if some errors occurred through the work of code
     */
    public static String compareTwoGenomes(String BAMFileName1, String BAMFileName2, String BEDFileName, int threadsNum, boolean intermediateOutput) throws
        GenomeFileException, GenomeException {
        GenomeComparatorExecutor comparator = new GenomeComparatorExecutor(BAMFileName1, BAMFileName2, BEDFileName, threadsNum);
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer = comparator.compareGenomes(intermediateOutput);
        geneComparisonResultAnalyzer.analyze();
        return geneComparisonResultAnalyzer.toString();
    }

    /**
     * Static method , which run a comparing of genomes of three persons: son with father and son with mother
     *
     * @param fatherBAMFileName  name of the BAM file which contains genome of the father
     * @param motherBAMFileName  name of the BAM file which contains genome of the mother
     * @param sonBAMFileName     name of the BAM file which contains genome of the son
     * @param BEDFileName        name of the BED file
     * @param threadsNum         Number of threads that will be used to process exons.
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @return String with results of genomes comparing of three persons: son with father and son with mother
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException     if some errors occurred through the work of code
     */
    public static String compareThreeGenomes(String fatherBAMFileName, String motherBAMFileName, String sonBAMFileName, String BEDFileName, int threadsNum, boolean intermediateOutput) throws
        GenomeFileException, GenomeException {
        // todo make the comparison in parallel
        GenomeComparatorExecutor comparator1 = new GenomeComparatorExecutor(sonBAMFileName, fatherBAMFileName, BEDFileName, threadsNum);
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer1 = comparator1.compareGenomes(intermediateOutput);
        geneComparisonResultAnalyzer1.analyze();
        StringBuilder result = new StringBuilder("Comparison of father and son genomes:\n");
        result.append(geneComparisonResultAnalyzer1);

        GenomeComparatorExecutor comparator2 = new GenomeComparatorExecutor(sonBAMFileName, motherBAMFileName, BEDFileName, threadsNum);
        GeneComparisonResultAnalyzer geneComparisonResultAnalyzer2 = comparator2.compareGenomes(intermediateOutput);
        geneComparisonResultAnalyzer2.analyze();
        result.append("\nComparison of mother and son genomes:\n");
        result.append(geneComparisonResultAnalyzer2);
        result.append(getChromosomeFromParentsInfo(geneComparisonResultAnalyzer1.getResults(), geneComparisonResultAnalyzer2.getResults()));

        return result.toString();
    }

    /**
     * Utils method, used to get the info about the results of the comparison.
     *
     * @param fatherChromosomes List with the chromosomes, that are probably were inherited from father.
     * @param motherChromosomes List with the chromosomes, that are probably were inherited from mother.
     * @return String representation of the results of the comparison.
     */
    private static String getChromosomeFromParentsInfo(List<Pair<String, Double>> fatherChromosomes, List<Pair<String, Double>> motherChromosomes) {
        StringBuilder res = new StringBuilder("\nChromosomes from father: [");
        StringBuilder motherChr = new StringBuilder("\nChromosomes from mother: [");
        int numMom = 0;
        int numDad = 0;
        for (int i = 0; i < fatherChromosomes.size(); i++) {
            if (Double.compare(fatherChromosomes.get(i).getValue(), motherChromosomes.get(i).getValue()) == 1) {
                numDad++;
                res.append(fatherChromosomes.get(i).getKey());
                res.append(", ");
            } else {
                numMom++;
                motherChr.append(motherChromosomes.get(i).getKey());
                motherChr.append(", ");
            }
        }
        if (res.toString().contains(",")) {
            res.delete(res.length() - 2, res.length());
        }
        res.append("]");
        if (motherChr.toString().contains(",")) {
            motherChr.delete(motherChr.length() - 2, motherChr.length());
        }
        motherChr.append("]\n");
        res.append(motherChr);
        res.append("Number of chromosomes from father: ");
        res.append(numDad);
        res.append("\nNumber of chromosomes from mother: ");
        res.append(numMom);
        res.append("\n");
        return res.toString();
    }
}
