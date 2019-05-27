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
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * {@link GenomeAssemblyCallable} class implements a {@link Callable} interface.
 * Overrides the call() method, so that it processes all the input exons for the
 * current gene and add the results to the resulting {@link GeneComparisonResultAnalyzer} object.
 *
 * @author Sergey Khvatov
 */
@Deprecated
public class GeneCallable implements Callable<List<GeneComparisonResult>> {

    /**
     * Default number of comparing threads.
     */
    private static final int FEATURE_COMPARISON_THREADS_NUM = 512;

    /**
     * List of {@link BEDFeature} objects that form the
     * list of the exons, that contain this gene.
     */
    private List<BEDFeature> exons;

    /**
     * Path to the first person's BAM file.
     */
    private BAMParser firstBAMFile;

    /**
     * Path to the second person's BAM file.
     */
    private BAMParser secondBAMFile;

    /**
     * Defines whether the some additional information
     * about the results of the comparison of two genome
     * regions will be printed.
     */
    private boolean additionalOutput;

    /**
     * Creates a Gene thread object using following arguments:
     *
     * @param exons            List of exons from the BED file.
     * @param firstBAM         First BAM file parser object.
     * @param secondBAM        Second BAM file parser object.
     * @param additionalOutput if this flag is true, then interim genome comparison results will be displayed,
     *                         else - only the main chromosome results will be obtained
     */
    public GeneCallable(List<BEDFeature> exons, BAMParser firstBAM, BAMParser secondBAM, boolean additionalOutput) {
        this.exons = exons;
        this.firstBAMFile = firstBAM;
        this.secondBAMFile = secondBAM;
        this.additionalOutput = additionalOutput;
    }

    /**
     * {@link Callable} interface method call() override.
     * Processes each exon, that contains this gene
     * and adds the results to the {@link GeneComparisonResultAnalyzer} object.
     *
     * @return Results of the comparison of the regions that contain this gene.
     * @throws GenomeException      if regions don't pass the validation.
     * @throws InterruptedException if thread was interrupted.
     */
    @Override
    public List<GeneComparisonResult> call() throws InterruptedException, GenomeException {
        // executors that are used in this method
        ExecutorService executorPool = Executors.newFixedThreadPool(FEATURE_COMPARISON_THREADS_NUM);
        CompletionService<List<GeneComparisonResult>> executorService = new ExecutorCompletionService<>(executorPool);

        try {
            // add tasks to the executor and wait for the results
            for (BEDFeature feature : exons) {
                executorService.submit(new FeatureCallable(feature, firstBAMFile, secondBAMFile, additionalOutput));
            }

            executorPool.shutdown();

            // for each feature from exon getSAMRecordList the results
            // of the comparison and add  them to the result object
            List<GeneComparisonResult> comparisonResults = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < exons.size(); i++) {
                Future<List<GeneComparisonResult>> futureRes = executorService.take();
                comparisonResults.addAll(futureRes.get());
            }

            return comparisonResults;
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
            executorPool.shutdownNow();
        }
    }
}
