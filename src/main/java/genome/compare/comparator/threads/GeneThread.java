package genome.compare.comparator.threads;

import bam.BAMParser;
import bam.BEDFeature;
import exception.GenomeThreadException;
import genome.compare.analyzis.GenomeRegionComparisonResult;

import java.util.List;
import java.util.Map;
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
     * Map where the results for each gene are stored.
     */
    private final ConcurrentMap<String, List<GenomeRegionComparisonResult>> comparisonResults;

    /**
     * Path to the first person's BAM file.
     */
    private final BAMParser firstBAMFile;

    /**
     * Path to the second person's BAM file.
     */
    private final BAMParser secondBAMFile;

    /**
     * Creates a Gene thread object using following arguments:
     *
     * @param exons     List of exons from the BED file.
     * @param firstBAM  First BAM file parser object.
     * @param secondBAM Second BAM file parser object.
     * @param results   Map, where the results for each gene are stored.
     */
    public GeneThread(List<BEDFeature> exons, BAMParser firstBAM, BAMParser secondBAM, ConcurrentMap<String, List<GenomeRegionComparisonResult>> results) {
        this.exons = exons;
        this.comparisonResults = results;
        this.firstBAMFile = firstBAM;
        this.secondBAMFile = secondBAM;
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
                Thread featureThread = new Thread(new FeatureThread(feature, firstBAMFile, secondBAMFile, comparisonResults));
                featureThread.start();
                featureThread.join();
            }
        } catch (InterruptedException iex) {
            throw new GenomeThreadException(iex.getMessage());
        }
    }
}
