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
    private static final Double HIGH_PERCENTAGE = 99.7;
    /**
     * List which contains average similarity values of all types chromosomes
     */
    private List<Pair<String, Double>> averageSimilarityValues = new ArrayList<>();

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
     * Method , which return String with results of the analysis of the results of comparing two genes
     *
     * @return String with these results
     */
    @Override
    public String toString() {
        analyze();

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

        if ((highSimilarityChromosomeCount) > nonSimilarityChromosomeCount) {
            result.append("These persons are child and parent.");
        } else {
            result.append("These persons are not child and parent.");
        }

        return result.toString();
    }

    /**
     * Method which returns info String of genes similarities from the chromosome
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
     * Method that answers the question of whether the people studied are a parent and child
     *
     * @return true if they are parent and child, else return false
     */
    public boolean areParentAndChild() {
        return (highSimilarityChromosomeCount > nonSimilarityChromosomeCount);
    }

    /**
     * Method for adding gene comparison result into Map of chromosomes and genes
     * @param geneComparisonResult -  gene comparison result which we take from (@link GenomeRegionComparator)
     */
    public void add(GeneComparisonResult geneComparisonResult) {
        if (geneComparisonResults.containsKey(geneComparisonResult.getChromName())) {
            if (geneComparisonResults.get(geneComparisonResult.getChromName()).containsKey(geneComparisonResult.getGene())) {
                geneComparisonResults.get(geneComparisonResult.getChromName()).get(geneComparisonResult.getGene()).setKey(
                        geneComparisonResults.get(geneComparisonResult.getChromName()).get(geneComparisonResult.getGene()).getKey() + geneComparisonResult.getDifference()
                );
                geneComparisonResults.get(geneComparisonResult.getChromName()).get(geneComparisonResult.getGene()).setValue(
                        geneComparisonResults.get(geneComparisonResult.getChromName()).get(geneComparisonResult.getGene()).getValue() + geneComparisonResult.getSequenceLen()
                );
            }
            else {
                geneComparisonResults.get(
                        geneComparisonResult.getChromName()).put(geneComparisonResult.getGene(), new Pair<>(geneComparisonResult.getDifference(), geneComparisonResult.getSequenceLen())
                );
            }
        }
        else {
            Map<String , Pair<Integer, Integer>> currentGene = new ConcurrentHashMap<>();
            currentGene.put(geneComparisonResult.getGene(), new Pair<>(geneComparisonResult.getDifference(), geneComparisonResult.getSequenceLen()));
            geneComparisonResults.put(geneComparisonResult.getChromName(), currentGene);
        }
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    private void analyze() {
        for (String chrom : geneComparisonResults.keySet()) {
            if (getSumSeqLengthFromChrom(geneComparisonResults.get(chrom)) != 0) {
                averageSimilarityValues.add(new Pair<>(chrom, getAverageSimilarity(geneComparisonResults.get(chrom))));
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
     * @param diff differences between two genes
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
     * @param chrom Map of genes from the chromosome
     * @return total sequence length of chromosome
     */
    private Integer getSumSeqLengthFromChrom(Map<String, Pair<Integer, Integer>> chrom) {
        int length = 0;
        for (String gene : chrom.keySet()) {
            length+=chrom.get(gene).getValue();
        }
        return length;
    }

    /**
     * Method which return total difference count of chromosome
     * @param chrom Map of genes from the chromosome
     * @return total difference count of chromosome
     */
    private Integer getSumDiffFromChrom(Map<String, Pair<Integer, Integer>> chrom) {
        int diff = 0;
        for (String gene : chrom.keySet()) {
            diff+=chrom.get(gene).getKey();
        }
        return diff;
    }


}
