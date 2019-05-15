package genome.compare.analyzis;

import exception.GenomeException;
import genome.compare.analyzis.GeneComparisonResult;
import util.Pair;

import java.util.*;

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
     * Middle percentage of chromosome similarity for the parent and child.
     */
    private static final Double MIDDLE_PERCENTAGE = 44.9d;

    /**
     * Input HashMap of gene comparison results
     */
    private HashMap<String, ArrayList<GeneComparisonResult>> geneComparisonResults;

    /**
     * ArrayList which contains average similarity values of all types chromosomes
     */
    private ArrayList<Pair<String, Double>> averageSimilarityValues = new ArrayList<>();
    
    /**
     * HashMap of ChromComparisonResults : key - chromosome name; Pair: key - length, value - differences
     */
    private HashMap<String, Pair<Integer, Integer>> chromComparisonResults = new HashMap<>();

    /**
     * Count of chromosomes which have minimum 98% of similarity
     */
    private int highSimilarityChromosomeCount = 0;

    /**
     * Count of chromosomes which have minimum 45% but not less than 98% of similarity
     */
    private int middleSimilarityChromosomeCount = 0;

    /**
     * Count of chromosomes which have less than 45% of similarity
     */
    private int nonSimilarityChromosomeCount = 0;

    /**
     * Constructor of this class from List of gene comparison results
     *
     * @param geneComparisonResults Input HashMap of gene comparison results
     * @throws GenomeException if input data is invalid
     */
    public GeneComparisonResultAnalyzer(HashMap<String, ArrayList<GeneComparisonResult>> geneComparisonResults) throws GenomeException {
        this.geneComparisonResults = geneComparisonResults;
        if (geneComparisonResults.isEmpty()) {
            throw new GenomeException(this.getClass().getName(), "GeneComparisonResultAnalyzer", "geneComparisonResults", "is empty");
        }
        analyze();
    }

    /**
     * Method , which return String with results of the analysis of the results of comparing two genes
     *
     * @return String with these results
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Similarity percentage for each chromosome:\n");

        for (Pair<String, Double> averageSimilarityValue : averageSimilarityValues) {
            result.append("Name of chromosome: " + averageSimilarityValue.getKey() + ". " + "Similarity percentage: " + averageSimilarityValue.getValue() + "\n");
        }

        result.append("Count of chromosomes with 98% similarity: " + highSimilarityChromosomeCount + "\n" + "Count of chromosomes with 45% similarity: " + middleSimilarityChromosomeCount + "\n" + "Count of dissimilar chromosomes: " + nonSimilarityChromosomeCount + "\n");

        if ((highSimilarityChromosomeCount + middleSimilarityChromosomeCount) > nonSimilarityChromosomeCount) {
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
        return ((highSimilarityChromosomeCount + middleSimilarityChromosomeCount) > nonSimilarityChromosomeCount);
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    private void analyze() {
        for (String gene : geneComparisonResults.keySet()) {
            ArrayList<GeneComparisonResult> curGeneComparisonResults = geneComparisonResults.get(gene);
            for (GeneComparisonResult geneComparisonResult : curGeneComparisonResults) {
                if (chromComparisonResults.containsKey(geneComparisonResult.getChromName())) {
                    chromComparisonResults.get(geneComparisonResult.getChromName()).setKey(
                            chromComparisonResults.get(geneComparisonResult.getChromName()).getKey() + 
                                    geneComparisonResult.getSequenceLen()
                    );
                    chromComparisonResults.get(geneComparisonResult.getChromName()).setValue(
                            chromComparisonResults.get(geneComparisonResult.getChromName()).getValue() +
                                    geneComparisonResult.getDifference()
                    );
                }
                else {
                    chromComparisonResults.put(geneComparisonResult.getChromName(), new Pair<>(geneComparisonResult.getSequenceLen(),
                            geneComparisonResult.getDifference()));
                }
            }
        }
        for (String chrom : chromComparisonResults.keySet()) {
            averageSimilarityValues.add(new Pair<>(chrom, 100d - getAverageSimilarity(chromComparisonResults.get(chrom))*100d));
        }
        for (Pair<String, Double> similarity : averageSimilarityValues) {
            if (similarity.getValue() >= HIGH_PERCENTAGE) {
                highSimilarityChromosomeCount++;
            }
            else if (similarity.getValue() >= MIDDLE_PERCENTAGE) {
                middleSimilarityChromosomeCount++;
            }
            else {
                nonSimilarityChromosomeCount++;
            }
        }
    }

    /**
     * Method which find an average value of similarities
     *
     * @param differences Pair of length and differences of chromosome
     * @return average value of similarities
     */
    private Double getAverageSimilarity(Pair<Integer, Integer> differences) {
        return ((double)differences.getValue() / (double)differences.getKey());
    }

}
