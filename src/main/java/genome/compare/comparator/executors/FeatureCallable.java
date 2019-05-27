/**
 * MIT License
 *
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package genome.compare.comparator.executors;

import bam.BAMParser;
import bam.BEDFeature;
import exception.GenomeException;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;

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
@Deprecated
public class FeatureCallable implements Callable<List<GeneComparisonResult>> {

    /**
     * Default number of assembling threads.
     */
    private static final int ASSEMBLY_THREADS_NUM = 2;

    /**
     * Default number of comparing threads.
     */
    private static final int COMPARISON_THREADS_NUM = 512;

    /**
     * Index of the genome of the first person in the list.
     */
    private static final int FIRST_PERSON = 0;

    /**
     * Index of the genome of the second person in the list.
     */
    private static final int SECOND_PERSON = 1;

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
     * Defines whether the some additional information
     * about the results of the comparison of two genome
     * regions will be printed.
     */
    private boolean additionalOutput;

    /**
     * Creates a feature thread using the following arguments.
     *
     * @param feature          Corresponding BED file feature.
     * @param firstParser      First person's BAM file parser.
     * @param secondParser     Second person's BAM file parser.
     * @param additionalOutput if this flag is true, then advanced region comparison results will be displayed,
     *                         else - only the main chromosome results will be obtained
     */
    public FeatureCallable(BEDFeature feature, BAMParser firstParser, BAMParser secondParser, boolean additionalOutput) {
        this.feature = feature;
        this.firstBAMFile = firstParser;
        this.secondBAMFile = secondParser;
        this.additionalOutput = additionalOutput;
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
    public List<GeneComparisonResult> call() throws GenomeException, InterruptedException {
        // executor services that will be used in the method
        ExecutorService assemblyService = Executors.newFixedThreadPool(ASSEMBLY_THREADS_NUM), comparePool = Executors.newFixedThreadPool(COMPARISON_THREADS_NUM);
        CompletionService<GeneComparisonResult> compareService = new ExecutorCompletionService<>(comparePool);

        try {
            /* first we start an ExecutorService that will
            assembly two genome regions according to the feature object;
            the main thread is waiting till both regions are assembled; */
            // list with all the tasks for the assembly executor service
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
                throw new GenomeException("Error occurred while assembling: " + feature);
            }

            // submit the tasks to the executor
            for (int i = 0; i < firstGenome.size(); i++) {
                compareService.submit(new GenomeRegionCallable(firstGenome.get(i), secondGenome.get(i), additionalOutput));
            }

            // save the results of the comparison
            List<GeneComparisonResult> results = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < firstGenome.size(); i++) {
                Future<GeneComparisonResult> temp = compareService.take();
                results.add(temp.get());
            }

            // shutdown the comparing executor
            comparePool.shutdown();

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
