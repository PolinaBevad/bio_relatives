package genome.compare.comparator.threads;

import bam.BAMParser;
import bam.BEDFeature;
import exception.GenomeThreadException;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Sergey Khvatov
 */
public class GeneThread implements Runnable {

    /**
     * List of {@link BEDFeature} objects that form the
     * list of the exons, that contain this gene.
     */
    private final List<BEDFeature> exons;

    /**
     * Object of (@link GeneComparisonResultAnalyzer) where the results for each gene are stored.
     */
    private final GeneComparisonResultAnalyzer comparisonResults;

    /**
     * Path to the first person's BAM file.
     */
    private final BAMParser firstBAMFile;

    /**
     * Path to the second person's BAM file.
     */
    private final BAMParser secondBAMFile;

    /**
     * if this flag is true , then interim genome comparison results will be displayed,
     * else - only the main chromosome results will be obtained
     */
    private final boolean intermediateOutput;

    /**
     * Creates a Gene thread object using following arguments:
     *
     * @param exons                List of exons from the BED file.
     * @param firstBAM             First BAM file parser object.
     * @param secondBAM            Second BAM file parser object.
     * @param results              Object of (@link GeneComparisonResultAnalyzer), where the results for each gene are stored.
     * @param intermediateOutput   if this flag is true , then interim genome comparison results will be displayed,
     *                             else - only the main chromosome results will be obtained
     */
    public GeneThread(List<BEDFeature> exons, BAMParser firstBAM, BAMParser secondBAM, GeneComparisonResultAnalyzer results, boolean intermediateOutput) {
        this.exons = exons;
        this.comparisonResults = results;
        this.firstBAMFile = firstBAM;
        this.secondBAMFile = secondBAM;
        this.intermediateOutput = intermediateOutput;
    }

    /**
     * run() method of the interface {@link Runnable} implementation.
     * Processes each gene. Creates a number of {@link FeatureThread}
     * that will process all the exons, that contain this gene.
     */
    @Override
    public void run() {
        try {
            // go through all the exons
            for (BEDFeature feature : exons) {
                Thread featureThread = new Thread(new FeatureThread(feature, firstBAMFile, secondBAMFile, comparisonResults, intermediateOutput));
                featureThread.start();
                featureThread.join();
            }
        } catch (InterruptedException iex) {
            throw new GenomeThreadException(iex.getMessage());
        }
    }
}
