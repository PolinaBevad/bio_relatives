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

package genome.compare.comparator.threads;

import bam.BAMParser;
import bam.BEDFeature;
import exception.GenomeException;
import exception.GenomeFileException;
import exception.GenomeThreadException;
import genome.assembly.GenomeConstructor;
import genome.assembly.GenomeRegion;
import htsjdk.samtools.SAMRecord;
import util.LinkedSAMRecordList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Khvatov
 */
public class GenomeAssemblyThread implements Runnable {

    /**
     * Corresponding BAM file parser.
     */
    private final BAMParser bamFile;

    /**
     * BED file feature.
     */
    private final BEDFeature feature;

    /**
     * list, that is used to store the results
     */
    private final List<GenomeRegion> personGenome;

    /**
     * Creates the {@link GenomeAssemblyThread} from the corresponding BAM file parser,
     * bed file feature and the list, that is used to store the results.
     *
     * @param parser  Corresponding BAM file parser
     * @param feature BED file feature
     * @param genome  list, that is used to store the results
     * @throws GenomeException if genome is not empty.
     */
    public GenomeAssemblyThread(BAMParser parser, BEDFeature feature, List<GenomeRegion> genome) throws GenomeException {
        this.bamFile = parser;
        this.feature = feature;

        if (!genome.isEmpty()) {
            throw new GenomeException("GenomeAssemblyThread", "genome", "is not empty");
        }
        this.personGenome = genome;
    }

    /**
     * run() method of the interface {@link Runnable} implementation.
     * Processes each feature. Firstly, parses the corresponding
     * BAM file and getSAMRecordList the {@link htsjdk.samtools.SAMRecord} objects from
     * it. Than, using the corresponding BED feature assemblies the
     * genome into List of genome regions, that are then stored in the result
     * list.
     */
    @Override
    public void run() {
        try {
            // getSAMRecordList the list of sam records for each person
            LinkedSAMRecordList samRecords = bamFile.parse(feature);
            // construct genomes for this gene
            personGenome.addAll(GenomeConstructor.assembly(samRecords, feature));
        } catch (GenomeException | GenomeFileException gex) {
            throw new GenomeThreadException(gex.getMessage());
        }
    }
}
