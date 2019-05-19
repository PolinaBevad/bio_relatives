package genome.compare.analyzis;

import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class analyze results of gene comparison of two persons
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public class GeneComparisonResultAnalyzer {

    /**
     * High percentage of chromosome similarity for the parent and child.
     */
    private static final Double HIGH_PERCENTAGE = 98d;
    /**
     * List which contains average similarity values of all types chromosomes
     */
    private List<Pair<String, Double>> averageSimilarityValues = new ArrayList<>();

    /**
     * Concurrent Map of ChromComparisonResults : key - chromosome name; Pair: key - length, value - differences
     */
    private Map<String, Pair<Integer, Integer>> chromComparisonResults = new ConcurrentHashMap<>();

    /**
     * Count of chromosomes which have minimum 98% of similarity
     */
    private int highSimilarityChromosomeCount = 0;

    /**
     * Count of chromosomes which have less than 45% of similarity
     */
    private int nonSimilarityChromosomeCount = 0;

    /**
     * Method , which return String with results of the analysis of the results of comparing two genes
     *
     * @return String with these results
     */
    @Override
    public String toString() {
        analyze();

        StringBuilder result = new StringBuilder("Similarity percentage for each chromosome:\n");
        for (Pair<String, Double> averageSimilarityValue : averageSimilarityValues) {
            result.append("Name of chromosome: ");
            result.append(averageSimilarityValue.getKey());
            result.append(". Similarity percentage: ");
            result.append(averageSimilarityValue.getValue());
            result.append("%\n");
            result.append("Number of nucleotides compared: ");
            result.append(chromComparisonResults.get(averageSimilarityValue.getKey()).getKey());
            result.append("%\n");
        }

        result.append("Count of chromosomes with 98% similarity: ");
        result.append(highSimilarityChromosomeCount);
        result.append("\nCount of dissimilar chromosomes: ");
        result.append(nonSimilarityChromosomeCount);
        result.append("\n");

        if ((highSimilarityChromosomeCount) > nonSimilarityChromosomeCount) {
            result.append("These persons are child and parent.");
        } else {
            result.append("These persons are not child and parent.");
        }

        return result.toString();
    }


    /**
     * Method that answers the question of whether the people studied are a parent and child
     *
     * @return true if they are parent and child, else return false
     */
    public boolean areParentAndChild() {
        return (highSimilarityChromosomeCount > nonSimilarityChromosomeCount);
    }

    public void add(GeneComparisonResult geneComparisonResult) {
        if (chromComparisonResults.containsKey(geneComparisonResult.getChromName())) {
            chromComparisonResults.get(geneComparisonResult.getChromName()).setKey(chromComparisonResults.get(geneComparisonResult.getChromName()).getKey() + geneComparisonResult.getSequenceLen());
            chromComparisonResults.get(geneComparisonResult.getChromName()).setValue(chromComparisonResults.get(geneComparisonResult.getChromName()).getValue() + geneComparisonResult.getDifference());
        } else {
            chromComparisonResults.put(geneComparisonResult.getChromName(), new Pair<>(geneComparisonResult.getSequenceLen(), geneComparisonResult.getDifference()));
        }
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    private void analyze() {
        for (String chrom : chromComparisonResults.keySet()) {
            if (chromComparisonResults.get(chrom).getKey() != 0) {
                averageSimilarityValues.add(new Pair<>(chrom, 100d - getAverageSimilarity(chromComparisonResults.get(chrom)) * 100d));
            }
        }
        for (Pair<String, Double> similarity : averageSimilarityValues) {
            if (similarity.getValue() >= HIGH_PERCENTAGE) {
                highSimilarityChromosomeCount++;
            } else {
                nonSimilarityChromosomeCount++;
            }
        }
    }


    /**
     * Method which find an average value of similarities
     *
     * @param result Pair of length and differences of chromosome
     * @return average value of similarities
     */
    private Double getAverageSimilarity(Pair<Integer, Integer> result) {
        if (result.getKey() != 0) {
            return ((double) result.getValue() / (double) result.getKey());
        }
        return 0d;
    }
}
