package genome.compare;

import exception.GenomeException;
import util.Pair;

import java.util.*;

/**
 * This class analyze results of gene comparison of two persons
 *
 * @author Vladislav Marchenko
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
     * Input List of gene comparison results
     */
    private List<GeneComparisonResult> geneComparisonResults;

    /**
     * ArrayList which contains median similarity values of all types chromosomes
     */
    private ArrayList<Pair<String, Double>> medianSimilarityValues = new ArrayList<>();

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
     * @param geneComparisonResults Input List of gene comparison results
     * @throws GenomeException if input data is invalid
     */
    public GeneComparisonResultAnalyzer(List<GeneComparisonResult> geneComparisonResults) throws GenomeException {
        this.geneComparisonResults = geneComparisonResults;
        if (geneComparisonResults.isEmpty()) {
            throw new GenomeException(this.getClass().getName(), "GeneComparisonResultAnalyzer", "geneComparisonResults", "is empty");
        }
        analyze();
    }

    /**
     * Method , which return String with results of the analysis of the results of comparing two genes
     * @return String with these results
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Similarity percentage for each chromosome:\n");

        for (Pair<String, Double> medianSimilarityValue : medianSimilarityValues) {
            result.append("Name of chromosome: "
                    + medianSimilarityValue.getKey()
                    +". "
                    + "Similarity percentage: "
                    + medianSimilarityValue.getValue() + "\n");
        }

        result.append("Count of chromosomes with 98% similarity: "
                + highSimilarityChromosomeCount + "\n"
                + "Count of chromosomes with 45% similarity: "
                + middleSimilarityChromosomeCount + "\n"
                +"Count of dissimilar chromosomes: "
                + nonSimilarityChromosomeCount + "\n"
        );

        if ((highSimilarityChromosomeCount + middleSimilarityChromosomeCount) > nonSimilarityChromosomeCount ) {
            result.append("These persons are child and parent.");
        }
        else {
            result.append("These persons are not child and parent.");
        }

        return result.toString();
    }


    /**
     * Method that answers the question of whether the people studied are a parent and child
     * @return true if they are parent and child, else return false
     */
    public boolean areParentAndChild() {
        return ((highSimilarityChromosomeCount + middleSimilarityChromosomeCount) > nonSimilarityChromosomeCount);
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    private void analyze() {
        // similarity percentages of each chromosome
        HashMap<String, ArrayList<Double>> chromosomeSimilarities = new HashMap<>();

        //find it
        for (GeneComparisonResult geneComparisonResult : geneComparisonResults) {
            if (chromosomeSimilarities.containsKey(geneComparisonResult.getChromName())) {
                chromosomeSimilarities.get(geneComparisonResult.getChromName()).add(analyzeChromosome(geneComparisonResult));
            }
            else {
                chromosomeSimilarities.put(geneComparisonResult.getChromName(), new ArrayList<>());
                chromosomeSimilarities.get(geneComparisonResult.getChromName()).add(analyzeChromosome(geneComparisonResult));
            }
        }

        // finding median similarity and assessment of each chromosome
        Set<Map.Entry<String, ArrayList<Double>>> set = chromosomeSimilarities.entrySet();
        for (Map.Entry<String, ArrayList<Double>> s : set) {
            Pair<String, Double> chromosomeSimilarity = new Pair(s.getKey(), getMedianSimilarity(s.getValue()));
            medianSimilarityValues.add(chromosomeSimilarity);
            if (chromosomeSimilarity.getValue() >= HIGH_PERCENTAGE) {
                highSimilarityChromosomeCount++;
            }
            else if (chromosomeSimilarity.getValue() > MIDDLE_PERCENTAGE) {
                middleSimilarityChromosomeCount++;
            }
            else {
                nonSimilarityChromosomeCount++;
            }
        }
    }

    /**
     * Method which analyze results of comparison for one chromosome
     * @param geneComparisonResult result of comparison of two chromosomes
     * @return double - percentage of similarity;
     */
    private Double analyzeChromosome(GeneComparisonResult geneComparisonResult) {
        return (100d - ((double)geneComparisonResult.getDifference()
                / (double)geneComparisonResult.getSequenceLen()) *100d);
    }

    /**
     * Method which find a median value of similarities
     * @param similarities ArrayList of similarities of the chromosome
     * @return median value of similarities
     */
    private Double getMedianSimilarity(ArrayList<Double> similarities) {
        // sort ArrayList of similarities
        Collections.sort(similarities);

        // if the number of elements is odd, then we take the middle element
        if (similarities.size() % 2 != 0) {
            return similarities.get(similarities.size() / 2);
        }

        // else return half of the sum of the two middle elements of the array
        else {
            return ((similarities.get(similarities.size() / 2) + similarities.get(similarities.size() / 2 - 1)) / 2d);
        }

    }

}
