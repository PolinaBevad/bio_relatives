/*
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

package genome.compare.levenshtein;

import exception.GenomeException;
import genome.compare.common.ComparisonResult;
import genome.compare.common.ComparisonResultAnalyzer;
import util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class analyze results of gene comparison of two persons(for Levenshtein comparator type)
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */

public class LevenshteinComparisonResultAnalyzer implements ComparisonResultAnalyzer {

    /**
     * High percentage of chromosome similarity for the parent and child.
     */
    private static final double HIGH_PERCENTAGE = 99.7;
    /**
     * List which contains average similarity values of all types chromosomes
     */
    private List<Pair<String, Double>> averageSimilarityValues = Collections.synchronizedList(new ArrayList<>());

    /**
     * Concurrent Map of geneComparisonResults : key - chromosome name; value: Map of genes: key - gene name , value - Pair of differences and sequence length
     */
    private Map<String, Map<String, Pair<Integer, Integer>>> geneComparisonResults = new ConcurrentHashMap<>();

    /**
     * Count of chromosomes which have minimum 99.7% of similarity
     */
    private int highSimilarityChromosomeCount = 0;

    /**
     * Count of chromosomes which have less than 99.7% of similarity
     */
    private int nonSimilarityChromosomeCount = 0;

    /**
     * Method which returns List of two person similarities for each chromosomes
     *
     * @return field averageSimilarityValues
     */
    public List<Pair<String, Double>> getResults() {
        return averageSimilarityValues;
    }

    /**
     * Method for adding gene comparison result into Map of chromosomes and genes
     *
     * @param comparisonResult -  gene comparison result which we take from (@link GenomeRegionComparator)
     * @throws GenomeException if geneComparisonResult is not an instance of {@link LevenshteinComparisonResult}.
     */
    @Override
    public void add(ComparisonResult comparisonResult) {
        // check the type
        if (!(comparisonResult instanceof LevenshteinComparisonResult)) {
            throw new GenomeException(this.getClass().getName(), "add", "comparison result variable has incorrect type: " + comparisonResult.getClass());
        }

        // add results
        LevenshteinComparisonResult levenshteinComparisonResult = (LevenshteinComparisonResult) comparisonResult;
        String chrom = levenshteinComparisonResult.getChromName();
        String gene = levenshteinComparisonResult.getGene();
        Integer diff = levenshteinComparisonResult.getDifference();
        Integer len = levenshteinComparisonResult.getSequenceLen();
        if (geneComparisonResults.containsKey(chrom)) {
            Map<String, Pair<Integer, Integer>> chromComparisonResult = geneComparisonResults.get(chrom);
            if (chromComparisonResult.containsKey(gene)) {
                chromComparisonResult.get(gene).setKey(chromComparisonResult.get(gene).getKey() + diff);
                chromComparisonResult.get(gene).setValue(chromComparisonResult.get(gene).getValue() + len);
            } else {
                chromComparisonResult.put(gene, new Pair<>(diff, len));
            }
        } else {
            Map<String, Pair<Integer, Integer>> currentGene = new ConcurrentHashMap<>();
            currentGene.put(gene, new Pair<>(diff, len));
            geneComparisonResults.put(chrom, currentGene);
        }
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    @Override
    public String analyze() {
        for (String chrom : geneComparisonResults.keySet()) {
            if (getSumSeqLengthFromChrom(geneComparisonResults.get(chrom)) != 0) {
                averageSimilarityValues.add(new Pair<>(chrom, getAverageSimilarity(geneComparisonResults.get(chrom))));
            }
        }
        for (Pair<String, Double> similarity : averageSimilarityValues) {
            if (Double.compare(similarity.getValue(), HIGH_PERCENTAGE) == 0 || Double.compare(similarity.getValue(), HIGH_PERCENTAGE) == 1) {
                highSimilarityChromosomeCount++;
            } else {
                nonSimilarityChromosomeCount++;
            }
        }
        return generateResultString();
    }

    /**
     * Method, which return String with results of the analysis of the results of comparing two genes
     *
     * @return String with these results
     */
    private String generateResultString() {
        StringBuilder result = new StringBuilder("Similarity percentage for each chromosome:\n");
        for (Pair<String, Double> averageSimilarityValue : averageSimilarityValues) {
            result.append("\tName of chromosome: ");
            result.append(averageSimilarityValue.getKey());
            result.append(". Similarity percentage: ");
            result.append(averageSimilarityValue.getValue());
            result.append("%\n");
            result.append("\tNumber of nucleotides compared: ");
            result.append(getSumSeqLengthFromChrom(geneComparisonResults.get(averageSimilarityValue.getKey())));
            result.append("\n");
            result.append(getGeneComparisonResultsString(averageSimilarityValue.getKey()));
        }

        result.append("Count of chromosomes with 99.7+% similarity: ");
        result.append(highSimilarityChromosomeCount);
        result.append("\nCount of dissimilar chromosomes: ");
        result.append(nonSimilarityChromosomeCount);
        result.append("\n");
        return result.toString();
    }

    /**
     * Method which returns info String of genes similarities from the chromosome
     *
     * @param chrName name of chromosome
     * @return string with info of each gene similarity
     */
    private String getGeneComparisonResultsString(String chrName) {
        StringBuilder result = new StringBuilder("\tSimilarity percentage for each gene from this chromosome:\n");
        Map<String, Pair<Integer, Integer>> currentGenes = geneComparisonResults.get(chrName);
        for (String gene : currentGenes.keySet()) {
            result.append("\t\tName of gene: ");
            result.append(gene);
            result.append(". Similarity percentage: ");
            result.append(getAverageSimilarity(currentGenes.get(gene).getKey(), currentGenes.get(gene).getValue()));
            result.append("%\n");
        }
        return result.toString();
    }

    /**
     * Method which find an average value of similarities
     *
     * @param chrom - all genes from chromosome- Map : key - name of gene ; value - Pair of differences and sequence length;
     * @return average value of similarities
     */
    private Double getAverageSimilarity(Map<String, Pair<Integer, Integer>> chrom) {
        if (getSumSeqLengthFromChrom(chrom) != 0) {
            return 100d - ((double) getSumDiffFromChrom(chrom) / (double) getSumSeqLengthFromChrom(chrom)) * 100d;
        }
        return 0d;
    }

    /**
     * Overloaded method which find an average value of similarities
     *
     * @param diff   differences between two genes
     * @param seqLen length of gene
     * @return average value of similarities
     */
    private Double getAverageSimilarity(int diff, int seqLen) {
        if (seqLen != 0) {
            return 100d - ((double) diff / (double) seqLen) * 100d;
        }
        return 0d;
    }

    /**
     * Method which return total sequence length of chromosome
     *
     * @param chrom Map of genes from the chromosome
     * @return total sequence length of chromosome
     */
    private Integer getSumSeqLengthFromChrom(Map<String, Pair<Integer, Integer>> chrom) {
        return chrom.keySet().stream().mapToInt(gene -> chrom.get(gene).getValue()).sum();
    }

    /**
     * Method which return total difference count of chromosome
     *
     * @param chrom Map of genes from the chromosome
     * @return total difference count of chromosome
     */
    private Integer getSumDiffFromChrom(Map<String, Pair<Integer, Integer>> chrom) {
        return chrom.keySet().stream().mapToInt(gene -> chrom.get(gene).getKey()).sum();
    }
}

