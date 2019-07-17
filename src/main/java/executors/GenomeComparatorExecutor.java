/*
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

package executors;

import bam.marker_region.MarkerRegionFileParser;
import bam.regular.BAMParser;
import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.common.ComparatorType;
import genome.compare.common.ComparisonResult;
import genome.compare.common.ComparisonResultAnalyzer;
import genome.compare.levenshtein.LevenshteinComparisonResultAnalyzer;
import genome.compare.str.STRComparisonResultAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenomeComparatorExecutor {

    /**
     * Path to the first person's BAM file.
     */
    private BAMParser firstBAMFile;

    /**
     * Path to the second person's BAM file.
     */
    private BAMParser secondBAMFile;

    /**
     * Map with the exons that
     * are parsed from the input BED file.
     */
    private Map<String, List<BEDFeature>> exons;

    /**
     * Type of the comparator that will be used to compare genomes.
     */
    private ComparatorType type;

    /**
     * Default class constructor from paths to the BAM files and corresponding to them BED file
     * and number of threads that will process the exons.
     *
     * @param pathToFirstBAM  Path to the BAM file where first person's genome is stored.
     * @param pathToSecondBAM Path to the BAM file where first person's genome is stored.
     * @param pathToBED       Path to the BED file.
     * @param type            Type of the comparator, that will be used to compare genomes.
     * @throws GenomeException     if exception occurs file parsing the BED file.
     * @throws GenomeFileException if incorrect BED or BAM file is passed.
     */
    public GenomeComparatorExecutor(String pathToFirstBAM, String pathToSecondBAM, String pathToBED, ComparatorType type) {
        this.firstBAMFile = new BAMParser(pathToFirstBAM);
        this.secondBAMFile = new BAMParser(pathToSecondBAM);
        this.type = type;
        // depending on the type of the comparator we are going to use
        this.exons = type == ComparatorType.LEVENSHTEIN ? new BEDParser(pathToBED).parse() : new MarkerRegionFileParser(pathToBED).parse();
    }

    /**
     * Compares two genomes parsing regions for each gene from the input files.
     *
     * @param threadsNum     Number of threads that will be used to process exons.
     * @param advancedOutput if this flag is true , then interim genome comparison results will be displayed,
     *                       else - only the main chromosome results will be obtained
     * @param path           Path to the file with graph.
     * @return Object ComparisonResultAnalyzer which contains results of the comparison of two genomes
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public ComparisonResultAnalyzer compareGenomes(int threadsNum, boolean advancedOutput, String path) throws GenomeException {
        // results of the comparison
        ComparisonResultAnalyzer comparisonResults;
        // executors that will be used in the method
        ExecutorService executorPool = Executors.newFixedThreadPool(threadsNum);
        CompletionService<List<ComparisonResult>> executorService = new ExecutorCompletionService<>(executorPool);
        try {
            if (type == ComparatorType.LEVENSHTEIN) {
                comparisonResults = new LevenshteinComparisonResultAnalyzer();
            } else {
                List<BEDFeature> features = new ArrayList<>();
                for (String gene : exons.keySet()) {
                    features.addAll(exons.get(gene));
                }
                comparisonResults = new STRComparisonResultAnalyzer(path, features);
            }

            int tasksNumber = 0;
            for (String gene : exons.keySet()) {
                // add tasks to the executor and wait for the results
                for (BEDFeature feature : exons.get(gene)) {
                    executorService.submit(new FeatureCallable(feature, firstBAMFile, secondBAMFile, type, threadsNum, advancedOutput));
                    tasksNumber++;
                }
            }

            for (int i = 0; i < tasksNumber; i++) {
                comparisonResults.add(executorService.take().get());
            }

            executorPool.shutdown();
            return comparisonResults;
        } catch (Exception ex) {
            // if exception has occurred during the call
            // then get the cause and init our own exception
            Throwable t = ex.getCause();
            GenomeException gex = new GenomeException(this.getClass().getName(), "call", t.getMessage());
            gex.initCause(t);
            throw gex;
        } finally {
            executorPool.shutdownNow();
        }
    }

    /**
     * Compares two genomes parsing regions for each gene from the input files.
     *
     * @param threadsNum     Number of threads that will be used to process exons.
     * @param advancedOutput if this flag is true , then interim genome comparison results will be displayed,
     *                       else - only the main chromosome results will be obtained
     * @return Object ComparisonResultAnalyzer which contains results of the comparison of two genomes
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public ComparisonResultAnalyzer compareGenomes(int threadsNum, boolean advancedOutput) throws GenomeException {
        return compareGenomes(threadsNum, advancedOutput, null);
    }
}
