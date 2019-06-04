package genome.compare.comparator.executor_advanced;

import bam.BAMParser;
import bam.BEDFeature;
import bam.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenomeComparatorExecutor {
    //TODO: Better to extract as a property
    public static final int EXONS_THREADS = 2;
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
    public GenomeComparatorExecutor(String pathToFirstBAM, String pathToSecondBAM, String pathToBED) throws GenomeException, GenomeFileException {
        this.firstBAMFile = new BAMParser(pathToFirstBAM);
        this.secondBAMFile = new BAMParser(pathToSecondBAM);
        this.exons = new BEDParser(pathToBED).parse();
    }

    /**
     * Compares two genomes parsing regions for each gene from the input files.
     *
     * @param advancedOutput if this flag is true , then interim genome comparison results will be displayed,
     *                       else - only the main chromosome results will be obtained
     * @return Object GeneComparisonResultAnalyzer which contains results of the comparison of two genomes
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public GeneComparisonResultAnalyzer compareGenomes(boolean advancedOutput) throws GenomeException {
        // results of the comparison
        GeneComparisonResultAnalyzer comparisonResults = new GeneComparisonResultAnalyzer();
        // executors that will be used in the method
        ExecutorService executorPool = Executors.newFixedThreadPool(EXONS_THREADS);
        CompletionService<List<GeneComparisonResult>> executorService = new ExecutorCompletionService<>(executorPool);
        try {
            int tasksNumber = 0;
            for (String gene : exons.keySet()) {
                // add tasks to the executor and wait for the results
                for (BEDFeature feature : exons.get(gene)) {
                    executorService.submit(new FeatureCallable(feature, firstBAMFile, secondBAMFile, advancedOutput));
                    tasksNumber++;
                }
            }
            for (int i = 0; i < tasksNumber; i++) {
                comparisonResults.add(executorService.take().get());
            }

            executorPool.shutdown();
            return comparisonResults;
        } catch (Exception ex) {
            // if exception has occurred during the call
            // then getSAMRecordList the cause and init our own exception
            Throwable t = ex.getCause();
            GenomeException gex = new GenomeException(this.getClass().getName(), "call", t.getMessage());
            gex.initCause(t);
            throw gex;
        } finally {
            executorPool.shutdownNow();
        }
    }
}
