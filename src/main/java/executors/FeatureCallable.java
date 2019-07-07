/**
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package executors;

import bam.regular.BAMParser;
import bam.regular.BEDFeature;
import bam.marker_region.MarkerRegionFeature;
import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.ComparatorType;
import genome.compare.analyzis.ComparisonResult;
import genome.compare.comparator.GenomeComparator;
import genome.compare.comparator.LevenshteinComparator;
import genome.compare.comparator.YSTRComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * {@link GenomeAssemblyCallable} class implements a {@link Callable} interface.
 * Overrides the call() method, so that it assemblies the genome region nucleotide sequence
 * that represents this region in the BED file and compares the resulting sequences.
 *
 * @author Sergey Khvatov
 */
public class FeatureCallable implements Callable<List<ComparisonResult>> {

    /**
     * Default number of assembling threads
     */
    private static final int ASSEMBLY_THREADS_NUM = 2;

    /**
     * Index of the genome of the first person in the list.
     */
    private static final int FIRST_PERSON = 0;

    /**
     * Index of the genome of the second person in the list.
     */
    private static final int SECOND_PERSON = 1;

    /**
     * Logger that is used to write down the information about feature processing.
     */
    private static final Logger featureLogger = LogManager.getLogger(FeatureCallable.class);

    /**
     * Corresponding BED file feature.
     */
    private BEDFeature feature;

    /**
     * First person's BAM file parser.
     */
    private BAMParser firstBAMFile;

    /**
     * Second person's BAM file parser.
     */
    private BAMParser secondBAMFile;

    /**
     * Number of assembling threads.
     */
    private int compareThreadsNumber;

    /**
     * Defines whether the some additional information
     * about the results of the comparison of two genome
     * regions will be printed.
     */
    private boolean additionalOutput;

    /**
     * Type of the comparator that is used.
     */
    private ComparatorType mode;

    /**
     * Creates a feature thread using the following arguments.
     *
     * @param feature          Corresponding BED file or Marker file feature.
     * @param firstParser      First person's BAM file parser.
     * @param secondParser     Second person's BAM file parser.
     * @param type             Type of the comparator, that will be used to compare genomes.
     * @param threadsNumber    Number of threads that are used in {@link GenomeComparatorExecutor}.
     * @param additionalOutput if this flag is true, then advanced region comparison results will be displayed,
     *                         else - only the main chromosome results will be obtained
     * @throws GenomeException if mode of comparator used is X_STR or Y_STR, but regular features were passed.
     */
    public FeatureCallable(BEDFeature feature, BAMParser firstParser, BAMParser secondParser, ComparatorType type, int threadsNumber, boolean additionalOutput) {
        if ((type == ComparatorType.Y_STR || type == ComparatorType.X_STR) && !(feature instanceof MarkerRegionFeature)) {
            throw new GenomeException(this.getClass().getName(), "FeatureCallable", "Incompatible types occurred: mode=" + type + ", but wrong features were passed");
        }
        this.mode = type;
        this.feature = feature;
        this.firstBAMFile = firstParser;
        this.secondBAMFile = secondParser;
        this.additionalOutput = additionalOutput;
        this.compareThreadsNumber = Runtime.getRuntime().availableProcessors() / threadsNumber > 0 ? Runtime.getRuntime().availableProcessors() / threadsNumber : 1;
    }

    /**
     * {@link Callable} interface method call() override.
     * Assembles two persons' genomes according to the bed file
     * feature that is passed through constructor and then compares
     * these two regions.
     *
     * @return Results of the comparison of two regions.
     * @throws GenomeException      if regions don't pass the validation.
     * @throws InterruptedException if thread was interrupted.
     */
    @Override
    public List<ComparisonResult> call() throws InterruptedException {
        // executor services that will be used in the method
        ExecutorService assemblyService = Executors.newFixedThreadPool(ASSEMBLY_THREADS_NUM);
        ExecutorService comparePool = Executors.newFixedThreadPool(compareThreadsNumber);
        CompletionService<ComparisonResult> compareService = new ExecutorCompletionService<>(comparePool);

        try {
            // log the start of the processing
            featureLogger.info("Processing feature: " + feature.toString());

            List<GenomeAssemblyCallable> assemblyTasks = new ArrayList<>();
            // add the tasks to the list
            assemblyTasks.add(new GenomeAssemblyCallable(firstBAMFile, feature));
            assemblyTasks.add(new GenomeAssemblyCallable(secondBAMFile, feature));

            // getSAMRecordList the results of the assembling as a list of Future objects
            List<Future<List<GenomeRegion>>> assembledRegions = assemblyService.invokeAll(assemblyTasks);

            // save the results of the comparison
            List<GenomeRegion> firstGenome = assembledRegions.get(FIRST_PERSON).get();
            List<GenomeRegion> secondGenome = assembledRegions.get(SECOND_PERSON).get();

            // shutdown the assembling executor
            assemblyService.shutdown();

            // check the results
            if (firstGenome.size() != secondGenome.size()) {
                //throw new GenomeException("Error occurred while assembling: " + feature);
                featureLogger.error("Error occurred while assembling: " + feature);
                return Collections.synchronizedList(new ArrayList<>());
            }

            // submit the tasks to the executor for the further comparison
            for (int i = 0; i < firstGenome.size(); i++) {
                GenomeComparator comparator = null;
                switch (mode) {
                    case LEVENSHTEIN:
                        comparator = new LevenshteinComparator(firstGenome.get(i), secondGenome.get(i));
                        break;
                    case Y_STR:
                        comparator = new YSTRComparator((MarkerRegionFeature) feature, firstGenome.get(i), secondGenome.get(i));
                        break;
                    case X_STR:
                        // TODO add XSTRComparator
                        break;
                }
                compareService.submit(new GenomeRegionCallable(comparator, additionalOutput));
            }

            // save the results of the comparison
            List<ComparisonResult> results = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < firstGenome.size(); i++) {
                results.add(compareService.take().get());
            }

            // shutdown the comparing executor
            comparePool.shutdown();

            // log the end of the processing
            featureLogger.info("End of processing feature: " + feature.toString());

            // return the results
            return results;
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            throw iex;
        } catch (ExecutionException eex) {
            // if exception has occurred during the call
            // then getSAMRecordList the cause and init our own exception
            Throwable t = eex.getCause();
            GenomeException gex = new GenomeException(this.getClass().getName(), "call", t.getMessage());
            gex.initCause(t);
            throw gex;
        } finally {
            // shutdown the executors
            assemblyService.shutdownNow();
            comparePool.shutdownNow();
        }
    }
}
