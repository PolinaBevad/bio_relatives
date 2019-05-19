package genome.compare.comparator.threads;

import bam.BAMParser;
import bam.BEDFeature;
import bam.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares several genomes that are stored in the BAM files
 * and stores the result of the comparison.
 *
 * @author Sergey Hvatov
 */
public class GenomeComparatorThread {

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
     * Default class constructor from paths to the BAM files and corresponding to them BED file.
     *
     * @param pathToFirstBAM  Path to the BAM file where first person's genome is stored.
     * @param pathToSecondBAM Path to the BAM file where first person's genome is stored.
     * @param pathToBED       Path to the BED file.
     * @throws GenomeException     if exception occurs file parsing the BED file.
     * @throws GenomeFileException if incorrect BED or BAM file is passed.
     */
    public GenomeComparatorThread(String pathToFirstBAM, String pathToSecondBAM, String pathToBED) throws GenomeException, GenomeFileException {
        this.firstBAMFile = new BAMParser(pathToFirstBAM);
        this.secondBAMFile = new BAMParser(pathToSecondBAM);
        this.exons = new BEDParser(pathToBED).parse();
    }

    /**
     * Compares two genomes parsing regions for each gene from the input files.
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @return Results of the comparison of two genomes - Object of (@link GeneComparisonResultAnalyzer)
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public GeneComparisonResultAnalyzer compareGenomes(boolean intermediateOutput) throws GenomeException {
        /*
         * Results of the comparison of two genomes -
         * Object of (@link GeneComparisonResultAnalyzer)
         */
        GeneComparisonResultAnalyzer comparisonResults = new GeneComparisonResultAnalyzer();
        try {
            for (String gene : exons.keySet()) {
                Thread geneThread = new Thread(new GeneThread(exons.get(gene), firstBAMFile, secondBAMFile, comparisonResults, intermediateOutput));
                geneThread.start();
                geneThread.join();
            }
            return comparisonResults;
        } catch (InterruptedException iex) {
            throw new GenomeException(iex.getMessage());
        }
    }
}
