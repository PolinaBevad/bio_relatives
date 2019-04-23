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
     * Name of X chromosomes which we can find in BAM file
     */
    private static final String X_CHR_NAME_1 = "chrX";

    /**
     * Another name of X chromosomes which we can find in BAM file
     */
    private static final String X_CHR_NAME_2 = "X";

    /**
     * Name of mitochondrial chromosomes which we can find in BAM file
     */
    private static final String MT_CHR_NAME_1 = "chrM";

    /**
     * Another name of mitochondrial chromosomes which we can find in BAM file
     */
    private static final String MT_CHR_NAME_2 = "MT";

    /**
     * The minimum percentage of similarity in mitochondrial chromosomes for the parent and child.
     */
    private static final Double MT_PERCENTAGE = 98d;

    /**
     * The minimum percentage of similarity in X chromosomes for the parent and child.
     */
    private static final Double X_PERCENTAGE = 45d;

    /**
     * The minimum percentage of similarity in autosomal chromosomes for the parent and child.
     */
    private static final Double AUTOSOMAL_PERCENTAGE = 45d;

    /**
     * Types of chromosomes
     */
    private enum ChromosomeType {
        MT_CHROMOSOME,
        X_CHROMOSOME,
        AUTOSOMAL_CHROMOSOME
    }

    /**
     * Input List of gene comparison results
     */
    private List<GeneComparisonResult> geneComparisonResults;

    /**
     * ArrayList which contains average similarity values of all types chromosomes
     */
    private ArrayList<Pair<Double, Boolean>> averageSimilarityValues = new ArrayList<>();

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

        // pass through the array with average values of chromosome similarity
        for (int i = 0; i < averageSimilarityValues.size(); i++) {
            if (averageSimilarityValues.get(i) != null) {
                // if these persons are parent and child, then return String with name of method and percentage of similarity
                if (averageSimilarityValues.get(i).getValue()) {
                    return getString(i);
                }
            }
        }

        // if these persons are not parent and child, then build a String with a message of it
        // and data of average similarity percentages for each type of chromosome
        String result ="Persons under study are not parent and child.\n";

        if (averageSimilarityValues.get(0) != null) {
            result += "The average percentage of similarity of mitochondrial chromosomes - "
                    + averageSimilarityValues.get(0).getKey() +"\n";

        }

        if (averageSimilarityValues.get(1) != null) {
            result += "The average percentage of similarity of X chromosomes - "
                    + averageSimilarityValues.get(1).getKey() +"\n";

        }

        if (averageSimilarityValues.get(2) != null) {
            result += "The average percentage of similarity of autosomal chromosomes - "
                    + averageSimilarityValues.get(2).getKey();

        }

        return result;
    }


    /**
     * Method which returns average similarity of the type of chromosome and this type.
     * If the persons are not child and parent, return maximum average similarity and type of chromosome which have this value
     * @return Pair<ChromosomeType, Double> - type of chromosome and average similarity value
     */
    public Pair<String, Double> getAverageSimilarity() {
        if ((averageSimilarityValues.get(0) != null) && (averageSimilarityValues.get(0).getValue())) {
            return new Pair<>(ChromosomeType.MT_CHROMOSOME.toString(), averageSimilarityValues.get(0).getKey());
        }

        else if ((averageSimilarityValues.get(1) != null) && (averageSimilarityValues.get(1).getValue())) {
            return new Pair<>(ChromosomeType.X_CHROMOSOME.toString(), averageSimilarityValues.get(1).getKey());
        }

        else if ((averageSimilarityValues.get(2) != null) && (averageSimilarityValues.get(2).getValue())) {
            return new Pair<>(ChromosomeType.AUTOSOMAL_CHROMOSOME.toString(), averageSimilarityValues.get(2).getKey());
        }

        else {
            ChromosomeType maxType = ChromosomeType.MT_CHROMOSOME;
            Double maxAverageValue = -1d;
            for (int i = 0; i < averageSimilarityValues.size(); i++) {
                if ((averageSimilarityValues.get(i) != null)
                        && (Double.compare(averageSimilarityValues.get(i).getKey(), maxAverageValue) > 0)) {
                    maxType = ChromosomeType.values()[i];
                    maxAverageValue = averageSimilarityValues.get(i).getKey();
                }
            }
            return new Pair<>(maxType.toString(), maxAverageValue);
        }
    }


    /**
     * This method return a string with results of our analysis
     * @param i - number of the number of the required sample results
     * @return required string with results of analysis
     */
    private String getString(int i) {
        switch (i) {
            case 0:
                return "Criterion of comparison of mitochondrial chromosomes.\n"
                        +"Persons under study are parent and child.\n"
                        +"The average percentage of similarity - "
                        + averageSimilarityValues.get(i).getKey();

            case 1:
                return "Criterion of comparison of X chromosomes.\n"
                        +"Persons under study are parent and child.\n"
                        +"The average percentage of similarity - "
                        + averageSimilarityValues.get(i).getKey();

            case 2:
                return "Criterion of comparison of autosomal chromosomes.\n"
                        +"Persons under study are parent and child.\n"
                        +"The average percentage of similarity - "
                        + averageSimilarityValues.get(i).getKey();

            default :
                return "";
        }
    }

    /**
     * Method which analyze results of comparison of two gene
     */
    private void analyze() {

        for (int i =0 ; i < 3; i++) {
            averageSimilarityValues.add(null);
        }

        // average similarity and count of mitochondrial chromosomes
        Double MTChrAverageSimilarity = 0.0;
        int MTChrCount = 0;

        // average similarity and count of X chromosomes
        Double XChrAverageSimilarity = 0.0;
        int XChrCount = 0;

        // average similarity and count of autosomal chromosomes
        Double autosomalChrAverageSimilarity = 0.0;
        int autosomalChrCount = 0;

        // for each gene comparison result
        for (GeneComparisonResult geneComparisonResult : geneComparisonResults) {
            if (geneComparisonResult.getChromName().equals(MT_CHR_NAME_1)
                    || geneComparisonResult.getChromName().equals(MT_CHR_NAME_2)) {
                MTChrAverageSimilarity += analyzeChromosome(geneComparisonResult);
                MTChrCount++;
            }
            else if (geneComparisonResult.getChromName().equals(X_CHR_NAME_1)
                    || geneComparisonResult.getChromName().equals(X_CHR_NAME_2)) {
                XChrAverageSimilarity += analyzeChromosome(geneComparisonResult);
                XChrCount++;
            }
            else {
                autosomalChrAverageSimilarity +=analyzeChromosome(geneComparisonResult);
                autosomalChrCount++;
            }
        }

        // adding of analysis results into ArrayList(field of class)
        if (MTChrCount > 0) {
            averageSimilarityValues.set(
                    0,
                    new Pair<>(MTChrAverageSimilarity / MTChrCount,
                            ((MTChrAverageSimilarity / MTChrCount) >= MT_PERCENTAGE))
            );
        }

        if (XChrCount > 0) {
            averageSimilarityValues.set(
                    1,
                    new Pair<>(XChrAverageSimilarity / XChrCount,
                            ((XChrAverageSimilarity / XChrCount) >= X_PERCENTAGE))
            );
        }

        if (autosomalChrCount > 0) {
            averageSimilarityValues.set(
                    2,
                    new Pair<>(autosomalChrAverageSimilarity / autosomalChrCount,
                            ((autosomalChrAverageSimilarity / autosomalChrCount) >= AUTOSOMAL_PERCENTAGE))
            );
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

}
