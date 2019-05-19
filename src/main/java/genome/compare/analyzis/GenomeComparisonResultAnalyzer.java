package genome.compare.analyzis;

import exception.GenomeException;
import util.Pair;

import java.util.*;

/**
 * This class analyze results of gene comparison of two persons
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public class GenomeComparisonResultAnalyzer {

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
    private List<GenomeRegionComparisonResult> genomeRegionComparisonResults;

    /**
     * ArrayList which contains average similarity values of all types chromosomes
     */
    private ArrayList<Pair<String, Double>> averageSimilarityValues = new ArrayList<>();

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
     * @param genomeRegionComparisonResults Input List of gene comparison results
     * @throws GenomeException if input data is invalid
     */
    public GenomeComparisonResultAnalyzer(List<GenomeRegionComparisonResult> genomeRegionComparisonResults) throws GenomeException {
        this.genomeRegionComparisonResults = genomeRegionComparisonResults;
        if (genomeRegionComparisonResults.isEmpty()) {
            throw new GenomeException(this.getClass().getName(), "GenomeComparisonResultAnalyzer", "genomeRegionComparisonResults", "is empty");
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

        for (Pair<String, Double> medianSimilarityValue : averageSimilarityValues) {
            result.append("Name of chromosome: " + medianSimilarityValue.getKey() + ". " + "Similarity percentage: " + medianSimilarityValue.getValue() + "\n");
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
        // similarity percentages of each chromosome
        HashMap<String, ArrayList<Double>> chromosomeSimilarities = new HashMap<>();

        //find it
        for (GenomeRegionComparisonResult genomeRegionComparisonResult : genomeRegionComparisonResults) {
            if (chromosomeSimilarities.containsKey(genomeRegionComparisonResult.getChromName())) {
                chromosomeSimilarities.get(genomeRegionComparisonResult.getChromName()).add(analyzeChromosome(genomeRegionComparisonResult));
            } else {
                chromosomeSimilarities.put(genomeRegionComparisonResult.getChromName(), new ArrayList<>());
                chromosomeSimilarities.get(genomeRegionComparisonResult.getChromName()).add(analyzeChromosome(genomeRegionComparisonResult));
            }
        }

        // finding median similarity and assessment of each chromosome
        Set<Map.Entry<String, ArrayList<Double>>> set = chromosomeSimilarities.entrySet();
        for (Map.Entry<String, ArrayList<Double>> s : set) {
            Pair<String, Double> chromosomeSimilarity = new Pair<>(s.getKey(), getAverageSimilarity(s.getValue()));
            averageSimilarityValues.add(chromosomeSimilarity);
            if (chromosomeSimilarity.getValue() >= HIGH_PERCENTAGE) {
                highSimilarityChromosomeCount++;
            } else if (chromosomeSimilarity.getValue() > MIDDLE_PERCENTAGE) {
                middleSimilarityChromosomeCount++;
            } else {
                nonSimilarityChromosomeCount++;
            }
        }
    }

    /**
     * Method which analyze results of comparison for one chromosome
     *
     * @param genomeRegionComparisonResult result of comparison of two chromosomes
     * @return double - percentage of similarity;
     */
    private Double analyzeChromosome(GenomeRegionComparisonResult genomeRegionComparisonResult) {
        return (100d - ((double) genomeRegionComparisonResult.getDifference() / (double) genomeRegionComparisonResult.getSequenceLen()) * 100d);
    }

    /**
     * Method which find an average value of similarities
     *
     * @param similarities ArrayList of similarities of the chromosome
     * @return average value of similarities
     */
    private Double getAverageSimilarity(ArrayList<Double> similarities) {
        double sum = 0.0;
        for (Double similarity : similarities) {
            sum += similarity;
        }
        return sum / similarities.size();
    }

}
